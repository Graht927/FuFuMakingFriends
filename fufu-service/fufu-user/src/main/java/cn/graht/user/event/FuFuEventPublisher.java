package cn.graht.user.event;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class FuFuEventPublisher {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void doStuffAndPublishAnEvent(final String message) {
        FuFuEvent fuFuEvent = new FuFuEvent(this, message);
        applicationEventPublisher.publishEvent(fuFuEvent);
    }
}
