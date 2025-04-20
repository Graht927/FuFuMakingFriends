package cn.graht.search.event;

/**
 * @author GRAHT
 */
public enum FuFuEventEnum {
    ;
    private String value;
    private FuFuEventEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
