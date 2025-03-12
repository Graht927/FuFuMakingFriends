package cn.graht.socializing.event;

/**
 * @author GRAHT
 */
public enum FuFuEventEnum {
    //动态通知
    DYNAMIC_NOTICE("dynamic:notice:"),
    //系统通知
    SYSTEM_NOTICE("system:notice:");
    private String value;
    private FuFuEventEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
