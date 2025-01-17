package cn.graht.user.mq.consumer;

   import cn.graht.common.commons.ErrorCode;
   import cn.graht.common.exception.ThrowUtils;
   import cn.graht.user.service.UserService;
   import jakarta.annotation.Resource;
   import lombok.extern.slf4j.Slf4j;
   import org.apache.commons.lang3.StringUtils;
   import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
   import org.apache.rocketmq.spring.core.RocketMQListener;
   import org.springframework.stereotype.Component;

   @Component
   @Slf4j
   @RocketMQMessageListener(topic = "user-unregister-topic", consumerGroup = "user-unregister-consumer-group")
   public class UserUnregisterConsumer implements RocketMQListener<String> {

       @Resource
       private UserService userService;

       @Override
       public void onMessage(String userId) {
           ThrowUtils.throwIf(StringUtils.isBlank(userId), ErrorCode.PARAMS_ERROR);
           boolean remove = userService.UnregisterRemoveById(userId);
           if (!remove) log.info(" 注销失败 有可能因为已经取消注销: {} ",userId);
           else log.info("注销成功: {} ",userId);
       }
   }
   