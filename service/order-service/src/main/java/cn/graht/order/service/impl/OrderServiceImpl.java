package cn.graht.order.service.impl;

import cn.graht.order.service.OrderService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author GRAHT
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Override
    //单独对Service中的方法进行流控
    @SentinelResource(value = "orderTestSentinel",blockHandler = "handleException")
    public String testSentinelOrder() {
       log.info("order测试熔断");
        return "order测试熔断";
    }

    public String handleException(BlockException ex) {
        return "order测试熔断出错 保底机制";
    }
}
