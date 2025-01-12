package cn.graht.user.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class FuFuEventListener implements ApplicationListener<FuFuEvent> {

    @Override
    public void onApplicationEvent(FuFuEvent event) {
        //处理事件
        System.out.println("Received custom event - " + event.getMessage());
    }
}
