package cn.graht.sms.utils;

import cn.graht.utils.tenXunMapApi.TenXunApiConstant;
import cn.graht.utils.tenXunMapApi.TenXunParams;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author GRAHT
 */

public class RestTemplateUtils {

    /**
     * 拼接TenXun地图Api
     *
     * @param u     路由地址
     * @param param 参数map
     * @return 接口返回的json
     */
    public static Object GateRestUrl(String u, Map<String, String> param) {
        TenXunParams tenXunParams = SpringContextUtils.getBean(TenXunParams.class);
        RestTemplate restTemplate = SpringContextUtils.getBean(RestTemplate.class);
        Set<String> keys = param.keySet();
        StringBuilder p = new StringBuilder();
        for (String key : keys) {
            p.append(key).append("=").append(param.get(key)).append("&");
        }
        StringBuilder url = new StringBuilder(tenXunParams.getPath());
        url.append(u + "?");
        StringBuilder params = new StringBuilder();

        if (TenXunApiConstant.TX_IP_ADDRESS.getValue().equals(u)) {
            params.append(p.toString());
            params.append("key=" + tenXunParams.getKey() + "&");
        } else {
            params.append("key=" + tenXunParams.getKey() + "&");
            params.append(p.toString());
        }
        params.append("output=json");
        String sig = DigestUtils.md5DigestAsHex((u + "?" + params.toString() + tenXunParams.getSecretKey()).getBytes());
        params.append("&sig=" + sig);
        url.append(params.toString());
        return restTemplate.getForObject(url.toString(), Object.class);
    }

    public static void main(String[] args) {
        System.out.println(DigestUtils.md5DigestAsHex("/ws/geocoder/v1?key=LFHBZ-PQVWC-D562K-AS5XH-6VUOF-GKBU7&location=35.513151,112.818450&output=jsonUATbDxllHoJp2617txij0b1LFA9fPOS2".getBytes()));
    }
}
