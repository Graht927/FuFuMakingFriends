package cn.graht.model.sms.dto;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class SMSRequestParam {
    private String phone;
    private String templateCodeStr;
    private String userNick;
    private String time;
    private String address;

}
