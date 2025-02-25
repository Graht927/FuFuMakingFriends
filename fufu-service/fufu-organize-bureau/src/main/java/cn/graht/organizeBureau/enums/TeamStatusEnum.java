package cn.graht.organizeBureau.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密"),;
    private Integer value;
    private String text;

    public static TeamStatusEnum getEnumByVal(Integer value) {
        if (value==null) return null;
        TeamStatusEnum[] enums = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : enums) {
            if (teamStatusEnum.getValue().equals(value)) {
                return teamStatusEnum;
            }
        }
        return null;
    }
    TeamStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
