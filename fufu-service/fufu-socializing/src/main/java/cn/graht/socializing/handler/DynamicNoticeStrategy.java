package cn.graht.socializing.handler;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author GRAHT
 */
@Component("dynamic:notice:")
public class DynamicNoticeStrategy implements FuFuEventStrategy{
    @Override
    public void handle(Map<String, String> param) {
        //处理逻辑
    }
}
