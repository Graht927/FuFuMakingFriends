package cn.graht.feignApi.socializing;

import cn.graht.common.commons.ResultApi;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author GRAHT
 */

@FeignClient("fufu-socializing")
public interface NoticeFeignApi {
    @PostMapping("/v1/dynamic/notice/add")
    ResultApi<Boolean> addNotice(@RequestBody DynamicNoticeDto noticeDto);
    @PostMapping("/v1/system/notice/add")
    ResultApi<Boolean> addNotice(@RequestBody SystemNoticeDto noticeDto);
}
