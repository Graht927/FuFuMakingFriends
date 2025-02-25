package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 * @author graht
 */
@Data
public class UserRegisterParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
