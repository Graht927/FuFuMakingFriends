package cn.graht.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author GRAHT
 */

@FeignClient("order-service-test")
public interface TestOrderFeign {
    @GetMapping("/test/ut")
    String test();
}
