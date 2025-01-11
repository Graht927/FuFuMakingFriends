package cn.graht.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author GRAHT
 */
@FeignClient(value = "test-order",contextId = "orderClient")
public interface OrderFeignClient {
    @GetMapping("/test/t1")
    public String t1();
}
