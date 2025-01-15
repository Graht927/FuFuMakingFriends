package cn.graht.feignApi.sms;

import cn.graht.common.commons.ResultApi;
import cn.graht.model.sms.dto.SMSRequestParam;
import cn.graht.model.sms.dto.TenXunRequestParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author GRAHT
 */

@FeignClient("fufu-sms")
public interface TXFeignApi {
    @PostMapping("/v1/tx")
    ResultApi<Object> tx(@RequestBody TenXunRequestParams params, @RequestHeader MultiValueMap<String, String> headers);
    @PostMapping("/v1/g")
    ResultApi<Object> requestSms(@RequestBody SMSRequestParam smsRequestParam,@RequestHeader MultiValueMap<String, String> headers);
}
