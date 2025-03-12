package cn.graht.socializing.handler;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GRAHT
 */
@Component
public class FuFuEventStrategyContext {
    private final Map<String,FuFuEventStrategy> strategies = new HashMap<>();
    @Autowired
    public FuFuEventStrategyContext(Map<String, FuFuEventStrategy> strategyMap) {
        strategyMap.forEach((key, value) -> strategies.put(key, value));
    }

    public void executeStrategy(String msgType, Map<String, String> params) {
        FuFuEventStrategy strategy = strategies.get(msgType);
        if (ObjectUtils.isNotEmpty(strategy)) {
            strategy.handle(params);
        } else {
            throw new IllegalArgumentException("No strategy found for message type: " + msgType);
        }
    }
}
