package cn.graht.model.socializing.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 私聊会话表
 * @TableName private_chat_session
 */
@TableName(value ="private_chat_session")
@Data
public class PrivateChatSession {
    /**
     * 私聊会话id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户1
     */
    private String userId1;

    /**
     * 用户2
     */
    private String userId2;

    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PrivateChatSession other = (PrivateChatSession) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId1() == null ? other.getUserId1() == null : this.getUserId1().equals(other.getUserId1()))
            && (this.getUserId2() == null ? other.getUserId2() == null : this.getUserId2().equals(other.getUserId2()))
            && (this.getLastMessageTime() == null ? other.getLastMessageTime() == null : this.getLastMessageTime().equals(other.getLastMessageTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId1() == null) ? 0 : getUserId1().hashCode());
        result = prime * result + ((getUserId2() == null) ? 0 : getUserId2().hashCode());
        result = prime * result + ((getLastMessageTime() == null) ? 0 : getLastMessageTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId1=").append(userId1);
        sb.append(", userId2=").append(userId2);
        sb.append(", lastMessageTime=").append(lastMessageTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}