package cn.graht.common.constant;

/**
 * 用户常量
 *
 * @author GRAHT
 */
public interface UserConstant {


    /**
     * 密码正则校验
     */
    String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_.])[A-Za-z\\d@$!%*?&_.]{8,16}$";
    /**
     * 手机号校验正则
     */
    String PHONE_NUMBER_PATTERN = "^(13[0-9]|14[5-9]|15[0-9]|166|17[0-8]|18[0-9]|19[8-9])\\d{8}$";
    /**
     * 邮箱校验正则
     */
    String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,6})+$";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion
}
