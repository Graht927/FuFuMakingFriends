package cn.graht.feignApi.user;

import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GRAHT
 */

@FeignClient("fufu-user")
public interface UserFeignApi {
    @DeleteMapping("/v1/unregisterList/{uid}")
    ResultApi<Boolean> UnregisterRemoveById(@PathVariable String uid);
    @GetMapping("/v1/info/{uid}")
    ResultApi<UserVo> getUserInfo(@PathVariable String uid);
    @GetMapping("/v1/dynamics/{id}")
    ResultApi<Dynamic> getDynamicById(@PathVariable Long id);
    @PostMapping("/v1/initGetUserIds")
    ResultApi<UserIdsVo> getAllUserId(@RequestBody PageQuery pageQuery,@RequestHeader MultiValueMap<String, String> headers);

}
