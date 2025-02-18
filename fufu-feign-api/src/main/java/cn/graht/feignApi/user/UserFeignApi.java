package cn.graht.feignApi.user;

import cn.graht.common.commons.ResultApi;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
