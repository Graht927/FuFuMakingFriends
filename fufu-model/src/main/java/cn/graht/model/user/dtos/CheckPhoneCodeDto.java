package cn.graht.model.user.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class CheckPhoneCodeDto {
    private String phone;
    private String phoneCode;
}
