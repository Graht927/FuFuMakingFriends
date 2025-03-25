package cn.graht.socializing.utils;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author GRAHT
 */
@Lazy
public class UserToolUtils {
    @Resource
    private UserRedissonCache userRedissonCache;
    @Resource
    private CaffeineCacheService caffeineCacheService;
    @Resource
    private UserFeignApi userFeignApi;

    // 辅助方法：从缓存中获取用户信息，若不存在则调用feign接口并存入缓存
    public UserVo getUserFromCacheOrFeign(String userId) {
        UserVo userVo = null;
       /* userVo = caffeineCacheService.getUserCache(userId);
        if (ObjectUtils.isNotEmpty(userVo)) {
            return userVo;
        }
        String user = userRedissonCache.getUser(userId);
        if (StringUtils.isNotBlank(user)) {
            userVo = JSONUtil.toBean(user, UserVo.class);
            caffeineCacheService.putUserCache(userId, userVo);
            return userVo;
        }*/
        //如果redis中不存在 调用feign 并且将结果存储到redis中
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(userId);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userInfo)
                || ObjectUtils.isEmpty(userInfo.getData())
                || userInfo.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
        userVo = userInfo.getData();
//        caffeineCacheService.putUserCache(userId, userVo);
        return userVo;
    }
}
