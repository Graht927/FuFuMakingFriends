package cn.graht.utils.aliSendSMS;

import lombok.Data;

/**
 * @author GRAHT
 */


@Data
public class SMSTemplateCode {
    private String loginTemplateCode;

    private String registerTemplateCode;

    private String remoteLoginTemplateCode;
    private String unregisterTemplateCode;

    public String getTemplateCode(String templateCodeStr){
        if ("login".equals(templateCodeStr)) {
            return loginTemplateCode;
        }
        if ("register".equals(templateCodeStr)) {
            return registerTemplateCode;
        }
        if ("remoteLogin".equals(templateCodeStr)){
            return remoteLoginTemplateCode;
        }
        if ("unregister".equals(templateCodeStr)){
            return unregisterTemplateCode;
        }
        return "";
    }
}
