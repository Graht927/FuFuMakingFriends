package cn.graht.common.commons;

/**
 * 错误码
 * @author GRAHT
 */
public enum ErrorCode {
    SUCCESS(20000,"响应成功","success"),
    SYSTEM_ERROR(50000,"系统内部错误",""),
    SMS_PARAMS_ERROR(10001,"参数错误","sms参数错误"),
    PARAMS_ERROR(40000,"参数错误","请求参数错误"),
    PARAMS_NULL_ERROR(40001,"参数为空","请求参数为空"),
    NULL_ERROR(40002,"结果为空","请求结果为空"),
    NO_AUTH(40101,"无权限","没权限"),
    NOT_LOGIN_ERROR(40100,"未登录","用户没登录"),
    USER_UNREGISTER_MQ_ERROR(9001,"mq","用户已存在mq注销队列中"),

    LOGIN_PARAMS_ERROR(40101,"登录参数错误","用户名或密码错误"),
    REGISTER_PARAMS_ERROR(40110,"注册参数错误","注册参数有误"),
    REGISTER_PASSWORD_ERROR(40111,"注册参数错误","二次密码不一致"),
    REGISTER_PHONE_ERROR(40112,"手机号已被注册","手机号不可以重复注册"),
    REGISTER_NICKNAME_ERROR(40113,"昵称已被注册","该昵称已被占用"),
    USER_PHONE_CODE_ERROR(40114,"验证码错误","手机验证码错误"),
    USER_NOT_ERROR(40115,"用户不存在","用户不存在或已注销"),

    NOT_FOUND_ERROR(40400, "请求数据不存在","请求数据为空"),
    FORBIDDEN_ERROR(40300, "禁止访问","禁止访问"),
    OPERATION_ERROR(50001, "操作失败","error"),
    AUTH_ERROR(60001,"权限认证","")
    ;
    /**
     * 状态码信息
     */
    private final int code;
    private final String msg;
    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }
}
