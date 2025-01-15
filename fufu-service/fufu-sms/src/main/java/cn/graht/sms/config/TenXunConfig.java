package cn.graht.sms.config;

import cn.graht.utils.tenXunMapApi.TenXunParams;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */
@Configuration
public class TenXunConfig {
    private static final Logger log = LoggerFactory.getLogger(TenXunConfig.class);
    @Value("${tx.path}")
    private String path;
    @Value("${tx.key}")
    private String key;
    @Value("${tx.secretKey}")
    private String secretKey;
    @Value("${tx.reqHeaderCode}")
    private String reqHeaderCode;

    @Bean
    public TenXunParams tenXunParam() {
        TenXunParams tenXunParams = new TenXunParams();
        tenXunParams.setPath(path);
        tenXunParams.setKey(key);
        tenXunParams.setSecretKey(secretKey);
        tenXunParams.setReqHeaderCode(reqHeaderCode);
        return tenXunParams;
    }
}
