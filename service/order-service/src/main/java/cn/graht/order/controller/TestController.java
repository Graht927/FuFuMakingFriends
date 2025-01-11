package cn.graht.order.controller;

import cn.graht.order.feign.TestUserFeign;
import cn.graht.order.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GRAHT
 */

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/t1")
    public String t1() {
        return "order";
    }

    @Resource
    private TestUserFeign testUserFeign;
    @Resource
    private OrderService orderService;

    @GetMapping("/test")
    public String test() {
        log.info("远程调用user");
        return testUserFeign.test();
    }

    @GetMapping("/ut")
    public String ut() {
        log.info("order测试熔断");
        return orderService.testSentinelOrder();
    }
}
