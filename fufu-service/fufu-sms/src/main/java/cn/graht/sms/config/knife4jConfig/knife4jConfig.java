package cn.graht.sms.config.knife4jConfig;
 
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * @author GRAHT
 * @description 该文件夹内两个文件主要解决Knife4j请求接口时没有token[Authorization]请求头的问题
 */
@Configuration
public class knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                        .title("sms")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList("reqCode"))
                .components(new Components().addSecuritySchemes(
                                "reqCode",
                                new SecurityScheme()
                                        .name("reqCode")
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}