package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * 推出队伍
 * @author GRAHT
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private long teamId;
}
