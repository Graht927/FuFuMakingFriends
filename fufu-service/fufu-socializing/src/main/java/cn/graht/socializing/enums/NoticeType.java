package cn.graht.socializing.enums;

/**
 * @author GRAHT
 */


public enum NoticeType {
    FOCUS("focus"),
    THUMBS_UO("thumbsUp"),
    SYSTEM("system"),
    CREATE_ACTIVITY("createActivity"),;
    private String value;
    NoticeType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
