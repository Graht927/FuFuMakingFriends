package cn.graht.common.constant;

import java.util.Random;

public interface RedisKeyConstants {
    String RECOMMEND_USERS = "find_friends:user:recommend_users:";
    String LOCK_UPDATE_TEAM = "find_friends:team:update:";
    String LOCK_SEND_JOIN_TEAM = "find_friends:team:send_join_team:";
    String LOCK_USER_MATCH = "find_friends:team:user_match:";
    Integer DEFAULT_TIMEOUT = 60*60*10+new Random().nextInt(1000);

    String SMS_TEMPLATE_CODE_PREFIX = "fufu:sms:";
    String SMS_LOCK_PREFIX = "fufu:sms:lock:";
    Integer SMS_TIMEOUT = 60*5+new Random().nextInt(500);
}
