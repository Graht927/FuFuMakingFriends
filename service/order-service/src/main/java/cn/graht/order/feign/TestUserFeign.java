package cn.graht.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author GRAHT
 */
@FeignClient(value = "user-service-test")
public interface TestUserFeign {
    @GetMapping("/test/test")
    String test();
}
