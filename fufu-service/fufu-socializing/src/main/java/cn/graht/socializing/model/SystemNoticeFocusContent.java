package cn.graht.socializing.model;

import cn.graht.model.socializing.dtos.SystemNoticeDto;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class SystemNoticeFocusContent {
    private String focusUserId;
    private String focusUserName;
    private String focusUserAvatar;
}
