package cn.graht.model.user.vos;

import lombok.Data;

import java.util.List;

/**
 * @author GRAHT
 */
@Data
public class UserIdsVo {
    private long total;
    private List<String> userIds;
}
