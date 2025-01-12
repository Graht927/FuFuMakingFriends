package cn.graht.user.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author GRAHT
 */

public class FuFuEvent extends ApplicationEvent {
    private String message;
    public FuFuEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
