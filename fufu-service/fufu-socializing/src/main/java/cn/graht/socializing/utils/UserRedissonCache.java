package cn.graht.socializing.utils;

import cn.graht.common.constant.RedisKeyConstants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * 用户缓存
 * @author GRAHT
 */
@Service
@Slf4j
public class UserRedissonCache {

    @Resource
    private Redisson redisson;

    private static final int SHARD_COUNT = 10; // 分片数量
    private static final int SHARD_MAX_SIZE = 2000; // 每个分片的最大容量
    private static final long CACHE_TTL = 7; // 缓存过期时间（天）
    private RMapCache<String, String>[] userShards; // 分片数组

    @PostConstruct
    public void init() {
        userShards = new RMapCache[SHARD_COUNT];
        for (int i = 0; i < SHARD_COUNT; i++) {
            userShards[i] = redisson.getMapCache(RedisKeyConstants.USER_CACHE_SHARD + i);
            // 设置分片的最大容量和淘汰策略
            userShards[i].trySetMaxSize(SHARD_MAX_SIZE);
        }
        log.info("用户缓存加载成功");
    }

    /**
     * 根据用户ID获取分片
     */
    private RMapCache<String, String> getShard(String userId) {
        int shardIndex = getShardIndex(userId, SHARD_COUNT);
        return userShards[shardIndex];
    }

    /**
     * 添加用户数据到分片
     */
    public void addUser(String userId, String userData) {
        RMapCache<String, String> shard = getShard(userId);
        String userKey = String.valueOf(userId);
        shard.put(userKey, userData, CACHE_TTL, TimeUnit.DAYS); // 设置过期时间
    }

    /**
     * 从分片中获取用户数据
     */
    public String getUser(String userId) {
        RMapCache<String, String> shard = getShard(userId);
        String userKey = String.valueOf(userId);
        return shard.get(userKey);
    }

    /**
     * 批量获取用户数据
     */
    public Map<String, String> getUsers(List<String> userIds) {
        Map<String, String> result = new HashMap<>();
        for (String userId : userIds) {
            String userData = getUser(userId);
            if (userData != null) {
                result.put(userId, userData);
            }
        }
        return result;
    }

    /**
     * 根据用户ID计算分片索引
     */
    private int getShardIndex(String userId, int shardCount) {
        return (int) (userId.hashCode() % shardCount);
    }

}