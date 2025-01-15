package cn.graht.utils.aliSendSMS;

import cn.hutool.json.JSONUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 * 阿里云的短信通知发送功能
 *
 */
public class AliYunSmsUtils {
    private static final Logger log = LoggerFactory.getLogger(AliYunSmsUtils.class);

    /**
     * @param smsParams 参数类
     * @param templateCode 模板代码
     * @param phoneNumber 手机号
     * @param templateParam 发送参数
     */
    public static SendSmsResponse sendSms(SMSParams smsParams,String templateCode,String phoneNumber, String templateParam){
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(smsParams.getAccessKeyId())
                .accessKeySecret(smsParams.getAccessSecret())
                .build());
        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-qingdao") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(SMSParams.DOMAIN)
                )
                .build();
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(smsParams.getSignName())
                .templateCode(templateCode)
                .phoneNumbers(phoneNumber)
                .templateParam(templateParam)
                .build();


        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;
        try {
            resp = response.get();
            System.out.println(JSONUtil.toJsonStr(resp));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        } finally {
            client.close();
        }
        return resp;
    }

}


