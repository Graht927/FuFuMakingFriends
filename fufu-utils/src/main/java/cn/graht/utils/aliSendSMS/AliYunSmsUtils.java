package cn.graht.utils.aliSendSMS;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 * 阿里云的短信通知发送功能
 *
 */
public class AliYunSmsUtils {
    /**
     * @param phoneNumber 手机号
     * @param code        验证码
     */
    public static SendSmsResponse sendSms(SMSParams smsParams,String phoneNumber, String code){
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
                .templateCode(smsParams.getTemplateCode())
                .phoneNumbers(phoneNumber)
                .templateParam("{\"code\":"+"\""+code+"\"}")
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }finally {
            client.close();
        }
        return resp;
    }

}


