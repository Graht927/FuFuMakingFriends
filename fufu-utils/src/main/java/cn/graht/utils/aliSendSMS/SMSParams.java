package cn.graht.utils.aliSendSMS;

import cn.hutool.core.codec.Base64;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

/**
 * @author GRAHT
 */
@Data
public class SMSParams {
    /**
     * 阿里云的参数配置类
     * 短信服务
     */
    /**
     * 这个需要替换自己的AK(在阿里云的Accesskey管理中寻找)
     */
    private String accessKeyId;
    private String accessSecret;

    /**
     * 签名名称（需要替换以及只有审核后才能使用）在阿里云控制台中找到签名管理中的签名名称
     */
    private String signName;


    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    public static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    public static final String DOMAIN = "dysmsapi.aliyuncs.com";

    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8")
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new Base64().encode(signData);
    }

    public int getCaptcha() {
         return (int) (Math.random() * 999999) + 100;  //每次调用生成一位六位数的随机数;
    }
}
