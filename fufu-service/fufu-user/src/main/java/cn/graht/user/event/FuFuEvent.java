package cn.graht.user.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author GRAHT
 */

public class FuFuEvent extends ApplicationEvent {
    private String message;
    private Map<String,Object> params;

    public FuFuEvent(Object source, String message, Map<String,Object> params) {
        super(source);
        this.message = message;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }
    public Map<String, Object> getParams() {
        return params;
    }
}
