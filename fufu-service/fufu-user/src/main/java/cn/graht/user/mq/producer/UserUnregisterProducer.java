package cn.graht.user.mq.producer;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import cn.graht.user.service.UserService;

@Component
@ConditionalOnBean(UserService.class)
public class UserUnregisterProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private ApplicationContext applicationContext;
    public void sendUnregisterRequest(String userId) {
        UserService userService = applicationContext.getBean(UserService.class);
        userService.sendUnregisterRequest(userId);
        rocketMQTemplate.syncSend(
                "user-unregister-topic",
                MessageBuilder.withPayload(userId).build(),
                3000, 1);
    }

}
   