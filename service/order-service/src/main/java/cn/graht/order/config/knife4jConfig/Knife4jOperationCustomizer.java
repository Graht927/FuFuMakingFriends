package cn.graht.order.config.knife4jConfig;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
/**
 * @author GRAHT
 * @description 该文件夹内两个文件主要解决Knife4j请求接口时没有token[Authorization]请求头的问题
 */
@Slf4j
@Component
public class Knife4jOperationCustomizer implements GlobalOperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<SecurityRequirement> security = operation.getSecurity();
        if (security == null) {
            security = List.of(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
            operation.setSecurity(security);
        }
        return operation;
    }
}