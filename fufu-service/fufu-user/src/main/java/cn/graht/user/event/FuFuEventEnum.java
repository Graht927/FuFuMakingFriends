package cn.graht.user.event;

/**
 * @author GRAHT
 */
public enum FuFuEventEnum {
    //异地登录
    REMOTE_LOGIN("RemoteLogin:"),
    CHECK_REMOTE_LOGIN("CheckRemoteLogin:");
    private String value;
    private FuFuEventEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
