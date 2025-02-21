package cn.graht.model.luaScript.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * lua脚本
 * @TableName luaScript
 */
@TableName(value ="luaScript")
@Data
public class LuaScript {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String scriptName;

    /**
     * 
     */
    private String scriptContent;

    /**
     * 
     */
    private String sha1Checksum;

    /**
     * 
     */
    private Date createdTime;

    /**
     * 
     */
    private Date updateTime;

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
        LuaScript other = (LuaScript) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getScriptName() == null ? other.getScriptName() == null : this.getScriptName().equals(other.getScriptName()))
            && (this.getScriptContent() == null ? other.getScriptContent() == null : this.getScriptContent().equals(other.getScriptContent()))
            && (this.getSha1Checksum() == null ? other.getSha1Checksum() == null : this.getSha1Checksum().equals(other.getSha1Checksum()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getScriptName() == null) ? 0 : getScriptName().hashCode());
        result = prime * result + ((getScriptContent() == null) ? 0 : getScriptContent().hashCode());
        result = prime * result + ((getSha1Checksum() == null) ? 0 : getSha1Checksum().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", scriptName=").append(scriptName);
        sb.append(", scriptContent=").append(scriptContent);
        sb.append(", sha1Checksum=").append(sha1Checksum);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}