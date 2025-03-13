package cn.graht.feignApi.producer;

import cn.graht.common.commons.ResultApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author GRAHT
 */

@FeignClient("fufu-mq-producer")
public interface ProducerApi {
    @PostMapping("/v1/producer/sendMsg")
    ResultApi<Object> sendMsg(@RequestBody SendMSGRequestParams params, @RequestHeader MultiValueMap<String, String> headers);
    @PostMapping("/v1/producer/sendMsgAsync")
    ResultApi<Object> sendMsgAsync(@RequestBody SendMSGRequestParams params, @RequestHeader MultiValueMap<String, String> headers);

}
