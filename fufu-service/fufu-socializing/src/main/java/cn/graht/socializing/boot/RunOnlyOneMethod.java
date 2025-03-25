package cn.graht.socializing.boot;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.ChatServerConstant;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.luaScript.pojos.LuaScript;
import cn.graht.model.socializing.pojos.Focus;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.FocusMapper;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import cn.graht.socializing.utils.UserRedissonCache;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import cn.graht.socializing.mapper.LuaScriptMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

/**
 * @author GRAHT
 */
@Slf4j
@Order(1)
@Component
public class RunOnlyOneMethod implements CommandLineRunner {

    @Resource
    private Redisson redisson;
    @Resource
    private CaffeineCacheService caffeineCacheService;
    @Resource
    private FocusMapper focusMapper;
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private LuaScriptMapper luaScriptMapper;
    @Resource
    private UserRedissonCache userRedissonCache;

    private final String INIT_REDISSON_LOCK_KEY = "fufu:socializing:init:data:lock:";
    private final Integer MAX_RETRIES = 10;
    private final long INITIAL_BACKOFF = 3000;

    @Override
    public void run(String... args) {
        RLock lock = null;
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                lock = redisson.getLock(INIT_REDISSON_LOCK_KEY);
                lock.lock();
                start();
                break;
            } catch (Exception e) {
                retryCount++;
                log.error("初始化失败: 准备重试 (尝试次数: {})", retryCount, e);
                if (retryCount >= MAX_RETRIES) {
                    log.error("达到最大重试次数，放弃重试", e);
                    throw new RuntimeException("初始化失败，达到最大重试次数", e);
                }
                try {
                    long backoff = INITIAL_BACKOFF * (1L << (retryCount - 1)); // 指数退避
                    Thread.sleep(backoff);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试期间线程被中断", ex);
                    throw new RuntimeException(ex);
                }
            } finally {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private void start() throws Exception {
        log.info("执行初始化开始");
        int pageSize = 100;
        int page = 1;
        PageQuery pageQuery = new PageQuery(pageSize, page);
        //获取全部用户uid
        HttpHeaders headers = new HttpHeaders();
        headers.add("reqCode", md5DigestAsHex((SystemConstant.SALT + SystemConstant.SYSTEM_INIT_REQ_CODE).getBytes()));
        ResultApi<UserIdsVo> allUserId = userFeignApi.getAllUserId(pageQuery, headers);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(allUserId)
                || ObjectUtils.isEmpty(allUserId.getData())
                || allUserId.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
        List<String> userIds = allUserId.getData().getUserIds();
        log.info("获取到用户: {}", userIds);
        ((Runnable) () -> {
            userIds.forEach(this::initRedisFocusUserVo);
        }).run();
        long total = allUserId.getData().getTotal();
        long pages = total / pageSize;
        while (page < pages) {
            page++;
            PageQuery pageQuery1 = new PageQuery(pageSize, page);
            HttpHeaders headers1 = new HttpHeaders();
            headers.add("reqCode", md5DigestAsHex((SystemConstant.SALT + SystemConstant.SYSTEM_INIT_REQ_CODE).getBytes()));
            ResultApi<UserIdsVo> allUserId1 = userFeignApi.getAllUserId(pageQuery1, headers);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(allUserId1)
                    || ObjectUtils.isEmpty(allUserId1.getData())
                    || allUserId1.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
            List<String> userIds1 = allUserId1.getData().getUserIds();
            log.info("获取到用户: {}", userIds1);
            ((Runnable) () -> {
                userIds1.forEach(this::initRedisFocusUserVo);
            }).run();
        }
        initLuaScriptToCaffeineCache();
        log.info("初始化完成");

    }

    @DS("app")
    private void initLuaScriptToCaffeineCache() {
        log.info("Initializing Lua scripts into Caffeine cache...");
        List<LuaScript> scripts = luaScriptMapper.getAll();
        for (LuaScript script : scripts) {
            //加载到redis中获取sha1校验码 存回redis
            String s = loadScriptToRedis(script.getScriptContent());
            if (script.getSha1Checksum().contains("WaitInit") && !script.getSha1Checksum().equals(s)) {
                LambdaQueryWrapper<LuaScript> eq = new LambdaQueryWrapper<LuaScript>().eq(LuaScript::getId, script.getId());
                script.setSha1Checksum(s);
                luaScriptMapper.update(script, eq);
            }
            caffeineCacheService.putLuaScriptCache(script.getScriptName(), script.getScriptContent());
            log.info("Loaded Lua script name: {} sha1: {}", script.getScriptName(), script.getSha1Checksum());
        }
        log.info("Loaded {} Lua scripts into cache", scripts.size());
    }

    private String loadScriptToRedis(String scriptContent) {
        try {
            RScript script = redisson.getScript();
            return redisson.getScript().scriptLoad(scriptContent);
        } catch (Exception e) {
            log.error("Failed to load Lua script", e);
            throw new RuntimeException("Failed to load Lua script", e);
        }
    }

    private void initRedisFocusUserVo(String uid) {
        //异步
        UserVo userVo = getUserFromCacheOrFeign(uid);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userVo), ErrorCode.SYSTEM_ERROR);
        RScoredSortedSet<String> set1 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":1");
        RScoredSortedSet<String> set2 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":2");
        RScoredSortedSet<String> set3 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":3");
        RScoredSortedSet<String> set4 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":4");
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
                RScoredSortedSet<String> set1 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":1");
                if (set1.contains(focusId)) {
                    break;
                }
                set1.add(System.currentTimeMillis(), focusId);
                break;
            case 1:
                RScoredSortedSet<String> set2 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":2");
                if (set2.contains(focusId)) {
                    break;
                }
                set2.add(System.currentTimeMillis(), focusId);
                break;
            case 2:
                RScoredSortedSet<String> set3 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":3");
                if (set3.contains(focusId)) {
                    break;
                }
                set3.add(System.currentTimeMillis(), focusId);
                break;
            case 3:
                RScoredSortedSet<String> set4 = redisson.getScoredSortedSet(RedisKeyConstants.SOCIALIZING_FOCUS_ZSET_KEY + uid + ":4");
                if (set4.contains(focusId)) {
                    break;
                }
                set4.add(System.currentTimeMillis(), focusId);
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

    private UserVo getUserFromCacheOrFeign(String userId) {
        UserVo userVo = caffeineCacheService.getUserCache(userId);
        if (ObjectUtils.isNotEmpty(userVo)) {
            return userVo;
        }
        String user = userRedissonCache.getUser(userId);
        if (StringUtils.isNotBlank(user)) {
            userVo = JSONUtil.toBean(user, UserVo.class);
            caffeineCacheService.putUserCache(userId, userVo);
            return userVo;
        }
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
