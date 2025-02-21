package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.dtos.EditFocusDto;
import cn.graht.model.socializing.dtos.GetFansByUidDto;
import cn.graht.model.socializing.dtos.GetFocusByUidDto;
import cn.graht.model.socializing.pojos.Focus;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.FocusMapper;
import cn.graht.socializing.service.FocusService;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RSet;
import org.redisson.api.RSortedSet;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.graht.common.constant.LuaConstant;

/**
 * @author GRAHT
 * @description 针对表【focus(关注)】的数据库操作Service实现
 * @createDate 2025-02-17 16:18:14
 */
@Service
@Slf4j
public class FocusServiceImpl extends ServiceImpl<FocusMapper, Focus>
        implements FocusService {
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private FocusMapper focusMapper;
    @Resource
    private CaffeineCacheService caffeineCacheService;
    @Resource
    private Redisson redisson;

    @Override
    public List<UserVo> getFocusByUid(GetFocusByUidDto getFocusByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFocusByUidDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFocusByUidDto.getUid())
                        || ObjectUtils.isEmpty(getFocusByUidDto.getFocusUid())
                , ErrorCode.PARAMS_ERROR);
        Page<Focus> page = new Page<>(getFocusByUidDto.getPageNum(), getFocusByUidDto.getPageSize());
        LambdaQueryWrapper<Focus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Focus::getUserId, getFocusByUidDto.getFocusUid());
        UserVo data1 = getUserFromCacheOrFeign(getFocusByUidDto.getUid());
        UserVo data2 = getUserFromCacheOrFeign(getFocusByUidDto.getFocusUid());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(data1), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(data2), ErrorCode.PARAMS_ERROR);
        Object eval = redisson.getScript().eval(
                RScript.Mode.READ_ONLY,
                caffeineCacheService.getLuaScriptCache(LuaConstant.SOCIALIZING_GET_FOCUSES_LUA_SCRIPT),
                RScript.ReturnType.INTEGER,
                Collections.singletonList(getFocusByUidDto.getFocusUid()),
                String.valueOf(getFocusByUidDto.getPageNum()+1), String.valueOf(getFocusByUidDto.getPageSize()));
        log.info("Eval result: {}", eval);
        log.info("FocusUid: {}", getFocusByUidDto.getFocusUid());
        log.info("PageNum: {}", getFocusByUidDto.getPageNum());
        log.info("PageSize: {}", getFocusByUidDto.getPageSize());

        if (eval == null) {
            log.warn("Eval returned null. Check Lua script and Redis data.");
        } else {
            log.info("Eval returned value: {}", eval);
        }
        //暂不支持查看更多数据~_~
        Page<Focus> focusPage = focusMapper.selectPage(page, queryWrapper);
        if (ObjectUtils.isEmpty(focusPage) || ObjectUtils.isEmpty(focusPage.getRecords())) {
            return List.of();
        }
        List<Focus> records = focusPage.getRecords();
        List<UserVo> userVos = records.stream().map(focus -> {
            //拿到关注者的id
            String focusId = focus.getFocusId();
            return getUserFromCacheOrFeign(focusId);
        }).toList();
        return userVos;
    }

    @Override
    public Boolean addFocus(EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getUserId())
                        || ObjectUtils.isEmpty(editFocusDto.getFocusUserId())
                , ErrorCode.PARAMS_ERROR);
        //查看关注者与被关注者是否存在
        //被关注者
        UserVo focusUserVo = getUserFromCacheOrFeign(editFocusDto.getFocusUserId());
        //关注者
        UserVo userVo = getUserFromCacheOrFeign(editFocusDto.getUserId());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(focusUserVo) || ObjectUtils.isEmpty(userVo), ErrorCode.PARAMS_ERROR);
        Focus focus = new Focus();
        focus.setUserId(editFocusDto.getUserId());
        focus.setFocusId(editFocusDto.getFocusUserId());
        if (save(focus)) {
            setFocusToRedis(editFocusDto.getUserId(), editFocusDto.getFocusUserId());
        }
        return true;
    }

    @Override
    public Boolean delFocus(EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getUserId())
                        || ObjectUtils.isEmpty(editFocusDto.getFocusUserId())
                , ErrorCode.PARAMS_ERROR);
        UserVo userVo = getUserFromCacheOrFeign(editFocusDto.getUserId());
        UserVo focusUserVo = getUserFromCacheOrFeign(editFocusDto.getFocusUserId());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userVo) || ObjectUtils.isEmpty(focusUserVo), ErrorCode.PARAMS_ERROR);
        boolean remove = remove(new LambdaQueryWrapper<Focus>().eq(Focus::getUserId, editFocusDto.getUserId())
                .eq(Focus::getFocusId, editFocusDto.getFocusUserId()));
        if (remove) {
            removeFocusToRedis(editFocusDto.getUserId(), editFocusDto.getFocusUserId());
        }
        return remove;
    }

    @Override
    public List<UserVo> getFansByUid(GetFansByUidDto getFansByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFansByUidDto) || ObjectUtils.isEmpty(getFansByUidDto.getUid()) || ObjectUtils.isEmpty(getFansByUidDto.getFocusId()), ErrorCode.PARAMS_ERROR);
        //我
        UserVo userVo = getUserFromCacheOrFeign(getFansByUidDto.getUid());
        //他
        UserVo focusUserVo = getUserFromCacheOrFeign(getFansByUidDto.getFocusId());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userVo), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(focusUserVo), ErrorCode.PARAMS_ERROR);
        Page<Focus> page = new Page<>(getFansByUidDto.getPageNum(), getFansByUidDto.getPageSize());
        //redis
        //mysql
        Page<Focus> focusPage = focusMapper.selectPage(page, new LambdaQueryWrapper<Focus>().eq(Focus::getFocusId, getFansByUidDto.getFocusId()));
        if (ObjectUtils.isEmpty(focusPage) || ObjectUtils.isEmpty(focusPage.getRecords())) {
            return List.of();
        }
        List<Focus> records = focusPage.getRecords();
        List<UserVo> userVos = records.stream().map(focus -> {
            String userId = focus.getUserId();
            return getUserFromCacheOrFeign(userId);
        }).toList();
        return userVos;
    }

    @Override
    public Boolean isFocus(EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getUserId())
                        || ObjectUtils.isEmpty(editFocusDto.getFocusUserId())
                , ErrorCode.PARAMS_ERROR);
        UserVo userVo = getUserFromCacheOrFeign(editFocusDto.getUserId());
        UserVo focusUserVo = getUserFromCacheOrFeign(editFocusDto.getFocusUserId());
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userVo) || ObjectUtils.isEmpty(focusUserVo), ErrorCode.PARAMS_ERROR);
        return isFocusMethod(editFocusDto);
    }

    private boolean isFocusMethod(EditFocusDto editFocusDto) {
        String userKey = editFocusDto.getUserId();
        String focusUserKey = editFocusDto.getFocusUserId();
        // 执行 Lua 脚本
        try {
            return (Long) redisson.getScript().eval(
                    RScript.Mode.READ_ONLY,
                    caffeineCacheService.getLuaScriptCache(LuaConstant.SOCIALIZING_IF_FOCUS_LUA_SCRIPT),
                    RScript.ReturnType.INTEGER,
                    Arrays.asList(userKey, focusUserKey)
            ) == 1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Lua script", e);
        }
    }

    private void setFocusToRedis(String uid, String focusId) {
        int abs = Math.abs(focusId.hashCode() % 4);
        switch (abs) {
            case 0:
                RScoredSortedSet<String> set1 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":1");
                set1.add(System.currentTimeMillis(),focusId);

                break;
            case 1:
                RScoredSortedSet<String> set2 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":2");
                set2.add(System.currentTimeMillis(),focusId);
                break;
            case 2:
                RScoredSortedSet<String> set3 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":3");
                set3.add(System.currentTimeMillis(),focusId);
                break;
            case 3:
                RScoredSortedSet<String> set4 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":4");
                set4.add(System.currentTimeMillis(),focusId);
                break;
        }
    }

    private void removeFocusToRedis(String uid, String focusId) {
        int abs = Math.abs(focusId.hashCode() % 4);
        switch (abs) {
            case 0:
                RScoredSortedSet<String> set1 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":1");
                set1.remove(focusId);
                break;
            case 1:
                RScoredSortedSet<String> set2 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":2");
                set2.remove(focusId);
                break;
            case 2:
                RScoredSortedSet<String> set3 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":3");
                set3.remove(focusId);
                break;
            case 3:
                RScoredSortedSet<String> set4 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":4");
                set4.remove(focusId);
                break;
        }
    }

    // 辅助方法：从缓存中获取用户信息，若不存在则调用feign接口并存入缓存
    private UserVo getUserFromCacheOrFeign(String userId) {
        UserVo userVo = null;
        userVo = caffeineCacheService.getUserCache(userId);
        if (ObjectUtils.isNotEmpty(userVo)) {
            return userVo;
        }
        //todo redis读取
        //如果redis中不存在 调用feign 并且将结果存储到redis中
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(userId);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userInfo)
                || ObjectUtils.isEmpty(userInfo.getData())
                || userInfo.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
        userVo = userInfo.getData();
        caffeineCacheService.putUserCache(userId, userVo);
        return userVo;
    }

}




