package cn.graht.model.socializing.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 群聊消息表
 * @TableName group_chat_message
 */
@TableName(value ="group_chat_message")
@Data
public class GroupChatMessage {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 群聊Id
     */
    private Integer groupId;

    /**
     * 发送者Id
     */
    private String senderId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 是否已读 0 为否
     */
    private Integer isRead;

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
        GroupChatMessage other = (GroupChatMessage) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getSenderId() == null ? other.getSenderId() == null : this.getSenderId().equals(other.getSenderId()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getMessageType() == null ? other.getMessageType() == null : this.getMessageType().equals(other.getMessageType()))
            && (this.getSendTime() == null ? other.getSendTime() == null : this.getSendTime().equals(other.getSendTime()))
            && (this.getIsRead() == null ? other.getIsRead() == null : this.getIsRead().equals(other.getIsRead()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getSenderId() == null) ? 0 : getSenderId().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getMessageType() == null) ? 0 : getMessageType().hashCode());
        result = prime * result + ((getSendTime() == null) ? 0 : getSendTime().hashCode());
        result = prime * result + ((getIsRead() == null) ? 0 : getIsRead().hashCode());
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
        sb.append(", groupId=").append(groupId);
        sb.append(", senderId=").append(senderId);
        sb.append(", content=").append(content);
        sb.append(", messageType=").append(messageType);
        sb.append(", sendTime=").append(sendTime);
        sb.append(", isRead=").append(isRead);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}