package cn.graht.search.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author GRAHT
 */

public class FuFuEvent extends ApplicationEvent {
    private String type;
    private Map<String,String> params;

    public FuFuEvent(Object source, String message, Map<String,String> params) {
        super(source);
        this.type = message;
        this.params = params;
    }

    public String getMessage() {
        return type;
    }
    public Map<String, String> getParams() {
        return params;
    }
}
