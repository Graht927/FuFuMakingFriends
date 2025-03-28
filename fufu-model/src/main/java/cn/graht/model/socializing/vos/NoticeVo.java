package cn.graht.model.socializing.vos;

import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class NoticeVo {
    private Integer id;

    private String userId;
    private String type;
    private String content;
    private String userId2;

    private String dynamicId;
    private String coverImage;

    private String avatarUrl;
    private String nickname;
}
