package cn.graht.socializing.boot;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.pojos.Focus;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.FocusMapper;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * @author GRAHT
 */
@Slf4j
@Order(1)
@Component
public class RunOnlyOneMethod implements CommandLineRunner{

    @Resource
    private Redisson redisson;
    @Resource
    private CaffeineCacheService caffeineCacheService;
    @Resource
    private FocusMapper focusMapper;
    @Resource
    private UserFeignApi userFeignApi;
    private final String initRedissonKey = "fufu:socializing:init:data:lock:";

    @Override
    public void run(String... args) {
        RLock lock = redisson.getLock(initRedissonKey);
        lock.lock();
        try {
            log.info("执行初始化开始");
            int pageSize = 1;
            int page = 0;
            PageQuery pageQuery = new PageQuery(pageSize, page);
            //获取全部用户uid
            HttpHeaders headers = new HttpHeaders();
            headers.add("reqCode", DigestUtils.md5DigestAsHex((SystemConstant.SALT + SystemConstant.SYSTEM_INIT_REQ_CODE).getBytes()));
            ResultApi<UserIdsVo> allUserId = userFeignApi.getAllUserId(pageQuery, headers);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(allUserId)
                    || ObjectUtils.isEmpty(allUserId.getData())
                    || allUserId.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
            List<String> userIds = allUserId.getData().getUserIds();
            ((Runnable) () -> {
                userIds.forEach(this::initRedisFocusUserVo);
            }).run();
            long total = allUserId.getData().getTotal();
            long pages = total / pageSize;
            while (page < pages) {
                page++;
                PageQuery pageQuery1 = new PageQuery(pageSize,page);
                HttpHeaders headers1 = new HttpHeaders();
                headers.add("reqCode", DigestUtils.md5DigestAsHex((SystemConstant.SALT + SystemConstant.SYSTEM_INIT_REQ_CODE).getBytes()));
                ResultApi<UserIdsVo> allUserId1 = userFeignApi.getAllUserId(pageQuery, headers);
                ThrowUtils.throwIf(ObjectUtils.isEmpty(allUserId)
                        || ObjectUtils.isEmpty(allUserId.getData())
                        || allUserId.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
                List<String> userIds1 = allUserId.getData().getUserIds();
                ((Runnable) () -> {
                    userIds1.forEach(this::initRedisFocusUserVo);
                }).run();
            }
            log.info("初始化完成");
        } catch (Exception e) {
            lock.unlock();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            //重试机制
            log.error("初始化失败:准备重试");
            run();
        } finally {
            lock.unlock();
        }

    }

    public void initRedisFocusUserVo(String uid) {
        //异步
        UserVo userVo = getUserFromCacheOrFeign(uid);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userVo), ErrorCode.SYSTEM_ERROR);
        RSet<String> set1 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":1");
        RSet<String> set2 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":2");
        RSet<String> set3 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":3");
        RSet<String> set4 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":4");
        Page<Focus> focusPage = focusMapper.selectPage(new Page<>(1, 100), new LambdaQueryWrapper<Focus>().eq(Focus::getUserId, uid));
        if (focusPage.getTotal() == 0 || focusPage.getTotal() == (set1.size() + set2.size() + set3.size() + set4.size())) {
            return;
        }
        focusPage.getRecords().forEach(focus -> {
            setFocusToRedis(focus.getUserId(), focus.getFocusId());
        });
        long total = focusPage.getTotal();
        //向下取整
        long pages = total % 100 == 0 ? total / 100 - 1 : total / 100;
        pages--;
        while (pages > 0) {
            Page<Focus> page = focusMapper.selectPage(new Page<>(pages, 100), new LambdaQueryWrapper<Focus>().eq(Focus::getUserId, uid));
            if (CollectionUtil.isEmpty(page.getRecords())) {
                return;
            }
            page.getRecords().forEach(focus -> {
                setFocusToRedis(focus.getUserId(), focus.getFocusId());
            });
            pages--;
        }
    }

    private void setFocusToRedis(String uid, String focusId) {
        int abs = Math.abs(focusId.hashCode() % 4);
        switch (abs) {
            case 0:
                RSet<String> set1 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":1");
                set1.add(focusId);
                break;
            case 1:
                RSet<String> set2 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":2");
                set2.add(focusId);
                break;
            case 2:
                RSet<String> set3 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":3");
                set3.add(focusId);
                break;
            case 3:
                RSet<String> set4 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":4");
                set4.add(focusId);
                break;
        }
    }

    private void removeFocusToRedis(String uid, String focusId) {
        int abs = Math.abs(focusId.hashCode() % 4);
        switch (abs) {
            case 0:
                RSet<String> set1 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":1");
                set1.add(focusId);
                break;
            case 1:
                RSet<String> set2 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":2");
                set2.add(focusId);
                break;
            case 2:
                RSet<String> set3 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":3");
                set3.add(focusId);
                break;
            case 3:
                RSet<String> set4 = redisson.getSet(RedisKeyConstants.SOCIALIZING_FOCUS_SET_KEY + uid + ":4");
                set4.add(focusId);
                break;
        }
    }

    private UserVo getUserFromCacheOrFeign(String userId) {
        UserVo userVo = caffeineCacheService.getUserCache(userId);
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
