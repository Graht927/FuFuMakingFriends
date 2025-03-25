package cn.graht.model.socializing.vos;

import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class NoticeVo {
    private Integer id;

    private String uid;
    private String type;
    private String content;

    private String dynamicId;
    private String coverImage;

    private String avatarUrl;
    private String nickname;
}
