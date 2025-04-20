package cn.graht.search.caffeine;

import cn.graht.common.constant.CaffeineKeyConstant;
import cn.graht.model.user.vos.UserVo;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */

@Getter
public class CaffeineCacheService {
    private final Cache<String, UserVo> userCache;

    public CaffeineCacheService(){
        this.userCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS) // 设置缓存过期时间
                .maximumSize(10000) // 设置最大缓存条目数
                .build();
    }
    public void putUserCache(String key, UserVo value){
        userCache.put(CaffeineKeyConstant.USER_CACHE_KEY+key, value);
    }

    public UserVo getUserCache(String key){
        return userCache.getIfPresent(CaffeineKeyConstant.USER_CACHE_KEY+key);
    }
    public void removeUserCache(String key){
        userCache.invalidate(CaffeineKeyConstant.USER_CACHE_KEY+key);
    }
    public void removeUserCacheByUser(UserVo userVo){
        userCache.invalidate(CaffeineKeyConstant.USER_CACHE_KEY+Objects.requireNonNull(userVo.getId()));
    }

}
