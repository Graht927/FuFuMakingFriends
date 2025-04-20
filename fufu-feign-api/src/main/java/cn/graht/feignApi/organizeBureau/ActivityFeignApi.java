package cn.graht.feignApi.organizeBureau;

import cn.graht.common.commons.ResultApi;
import cn.graht.feignApi.interceptor.TokenDelayRequestInterceptor;
import cn.graht.model.organizeBureau.dtos.GetDto;
import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author GRAHT
 */
@FeignClient(value = "fufu-organize-bureau",configuration = TokenDelayRequestInterceptor.class)
public interface ActivityFeignApi {
    @GetMapping("/v1/team/info")
    ResultApi<ActivityUserVo> getTeamInfoByTid(long teamId);
    @PostMapping("/v1/team/info")
    ResultApi<ActivityUserVo> getTeamInfo(@RequestBody GetDto dto);
    @PostMapping("/v1/team/list")
    ResultApi<List<ActivityUserVo>> listTeamByPage(@RequestBody TeamQuery teamQuery);
}
