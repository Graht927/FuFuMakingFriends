package cn.graht.model.socializing.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class SystemNoticeDto {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户Id2
     */
    private String userId2;

    /**
     * 内容
     */
    private String content;

    /**
     * 通知类型
     */
    private String type;
}
