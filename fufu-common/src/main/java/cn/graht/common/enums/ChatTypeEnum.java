package cn.graht.common.enums;

/**
 * @author GRAHT
 */


public enum ChatTypeEnum {
    PRIVATE(1, "private"),
    GROUP(2, "group"),
    ;
    private Integer code;
    private String type;
    ChatTypeEnum(Integer code, String type){
        this.code = code;
        this.type = type;
    }
    public Integer getCode() {
        return code;
    }
    public String getType() {
        return type;
    }
}
