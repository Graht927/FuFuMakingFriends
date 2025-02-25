package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userAccount;
    private String userPassword;
}
