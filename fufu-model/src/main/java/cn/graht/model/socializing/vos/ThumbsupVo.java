package cn.graht.model.socializing.vos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class ThumbsupVo {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 点赞动态
     */
    private Long dynamicId;

    /**
     * 创建|发布时间
     */
    private Date createTime;
}
