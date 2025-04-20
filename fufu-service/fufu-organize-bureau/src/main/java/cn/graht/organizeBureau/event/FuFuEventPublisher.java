package cn.graht.organizeBureau.event;

import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
/**
 * @author GRAHT
 */
@Component
public class FuFuEventPublisher {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void doStuffAndPublishAnEvent(final String message, Map<String,String> params) {
        FuFuEvent fuFuEvent = new FuFuEvent(this, message,params);
        applicationEventPublisher.publishEvent(fuFuEvent);
    }
}
