package cn.graht.producer.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.exception.BusinessException;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class ProducerController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private Redisson redisson;

    @PostMapping("/producer/sendMsg")
    public ResultApi<Boolean> sendMsg(@RequestBody SendMSGRequestParams params) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(params), ErrorCode.PARAMS_ERROR);
        String redisKey = RedisKeyConstants.PRODUCER_LOCK_PREFIX+params.getTopic()+RedisKeyConstants.PRODUCER_LOCK_SUFFIX+params.getContent().getUid();
        RLock lock = redisson.getLock(redisKey);
        lock.lock();
        try {
            rocketMQTemplate.syncSend(
                    params.getTopic(),
                    MessageBuilder.withPayload(JSONUtil.toJsonStr(params.getContent())).build(),
                    params.getTimeout(), params.getDelayLevel());
            log.info("调用来源: {}发送消息{} ok",params.getFrom(),params.getContent());
            return ResultUtil.ok(Boolean.TRUE);
        }catch (BusinessException e){
            return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
        }catch (Exception e) {
            log.error("调用来源: {}发送消息{}失败 原因: rocketmq服务异常",params.getFrom(),params.getContent());
            return ResultUtil.ok(Boolean.FALSE);
        }finally {
            lock.unlock();
        }
    }

}
