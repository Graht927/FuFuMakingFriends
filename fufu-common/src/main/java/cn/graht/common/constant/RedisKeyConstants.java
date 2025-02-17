package cn.graht.common.constant;

import java.util.Random;

public interface RedisKeyConstants {
    /**
     * 正常redisKey的过期时间
     */
    Integer DEFAULT_TIMEOUT = 60*60*10+new Random().nextInt(1000);

    String SMS_TEMPLATE_CODE_PREFIX = "fufu:sms:";
    String SMS_LOGIN_PREFIX = "fufu:sms:login:";
    String SMS_REGISTER_PREFIX = "fufu:sms:register:";

    String USER_LOGIN_LOCK_PREFIX = "fufu:user:login.lock:";
    String USER_LOGIN_COUNT_PREFIX = "fufu:user:login:count:";
    String USER_REGISTER_LOCK_PREFIX = "fufu:user:register.lock:";

    String USER_UNREGISTER_PREFIX = "fufu:user:un:register:";
    String USER_UNREGISTER_LOCK = "fufu:user:un:register:lock:";


    String PRODUCER_LOCK_PREFIX = "fufu:producer:";
    String PRODUCER_LOCK_SUFFIX = "lock:";


    String SMS_LOCK_PREFIX = "fufu:sms:lock:";
    Integer SMS_TIMEOUT = 60*5+new Random().nextInt(500);
}
