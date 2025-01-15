package cn.graht.utils.tenXunMapApi;

import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class TenXunParams {
    /**
     * 腾讯地址 apis.map.qq.com
     */
    private String path;
    /**
     * 请求链接 例: /ws/location/v1/ip
     */
    private String url;
    /**
     * 例: ip=xxxxxx&key=xxx
     */
    private String params;
    /**
     * param 中的key
     */
    private String key;
    /**
     * 用来计算sig
     */
    private String secretKey;

    private String reqHeaderCode;
}
