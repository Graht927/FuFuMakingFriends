package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamSendJoinRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private long teamId;
    private String userId;
    private String password;
}
