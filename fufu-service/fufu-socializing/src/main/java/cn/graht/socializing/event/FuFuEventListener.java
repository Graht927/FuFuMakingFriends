package cn.graht.socializing.event;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.sms.TXFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.handler.FuFuEventStrategyContext;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
/**
 * @author GRAHT
 */
@Component
@Slf4j
public class FuFuEventListener implements ApplicationListener<FuFuEvent> {

    @Resource
    private FuFuEventStrategyContext  fuFuEventStrategyContext;

    /**
     * 处理应用事件
     * 根据事件类型和内容执行相应的处理逻辑
     * @param event FuFu事件，包含事件消息和参数
     */
    @Override
    public void onApplicationEvent(FuFuEvent event) {
        String msgType = event.getMessage();
        Map<String, String> params = event.getParams();
        fuFuEventStrategyContext.executeStrategy(msgType, params);
    }






}

