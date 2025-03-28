package cn.graht.feignApi.socializing;

import cn.graht.common.commons.ResultApi;
import cn.graht.feignApi.interceptor.TokenDelayRequestInterceptor;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author GRAHT
 */

@FeignClient(value = "fufu-socializing",configuration = TokenDelayRequestInterceptor.class)
public interface SocializingFeignApi {
    @GetMapping("/v1/thumbsUp/{dynamicId}/{uid}")
    ResultApi<Boolean> isThumbsup(@PathVariable Long dynamicId, @PathVariable String uid);
    @PostMapping("/v1/dynamic/notice/add")
    ResultApi<Boolean> addNotice(@RequestBody DynamicNoticeDto noticeDto);
    @PostMapping("/v1/system/notice/add")
    ResultApi<Boolean> addNotice(@RequestBody SystemNoticeDto noticeDto);
}
