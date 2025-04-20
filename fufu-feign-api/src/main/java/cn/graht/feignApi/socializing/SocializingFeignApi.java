package cn.graht.feignApi.socializing;

import cn.graht.common.commons.ResultApi;
import cn.graht.feignApi.interceptor.TokenDelayRequestInterceptor;
import cn.graht.model.socializing.dtos.CreateGroupChatMemberDto;
import cn.graht.model.socializing.dtos.CreateGroupSessionDto;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import cn.graht.model.socializing.pojos.GroupChatSession;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/v1/groupChat/session")
    ResultApi<Integer> createSession(@RequestBody CreateGroupSessionDto sessionDto);
    @PostMapping("/v1/groupChat/member")
    ResultApi<Boolean> addMember(@RequestBody CreateGroupChatMemberDto memberDto);
    @DeleteMapping("/v1/groupChat/member/{id}")
    ResultApi<Boolean> deleteMember(@PathVariable Integer id);
    @GetMapping("/v1/groupChat/session/activity/{activityId}")
    ResultApi<GroupChatSession> getSessionByActivityId(@PathVariable Integer activityId);
}
