package cn.graht.common.enums;

/**
 * @author GRAHT
 */

public enum MessageDelayLevelEnum {
    L1(1,"1s"),
    L2(2,"5s"),
    L3(3,"10s"),
    L4(4,"30s"),
    L5(5,"1m"),
    L6(6,"2m"),
    L7(7,"3m"),
    L8(8,"1d"),
    L9(9,"5m"),
    L10(10,"6m"),
    L11(11,"7m"),
    L12(12,"8m"),
    L13(13,"9m"),
    L14(14,"10m"),
    L15(15,"20m"),
    L16(16,"30m"),
    L17(17,"1h"),
    L18(18,"2h"),
    ;
    private Integer level;
    private String delayTime;
    MessageDelayLevelEnum(Integer level, String delayTime){
        this.level = level;
        this.delayTime = delayTime;
    }
    public Integer getLevel() {
        return level;
    }
}
