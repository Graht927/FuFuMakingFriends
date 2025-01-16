package cn.graht.user.event;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.sms.TXFeignApi;
import cn.graht.model.sms.dto.SMSRequestParam;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.pojos.User;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.graht.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import static cn.graht.user.event.FuFuEventEnum.*;

@Component
@Slf4j
public class FuFuEventListener implements ApplicationListener<FuFuEvent> {

    @Resource
    private UserService userService;
    @Resource
    private TXFeignApi txFeignApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FuFuEventPublisher fuFuEventPublisher;
    @Value("${ali.sms.reqHeaderCode}")
    private String smsReqCode;

    /**
     * 处理应用事件
     * 根据事件类型和内容执行相应的处理逻辑
     *
     * @param event FuFu事件，包含事件消息和参数
     */
    @Override
    public void onApplicationEvent(FuFuEvent event) {
        String message = event.getMessage();
        Map<String, Object> params = event.getParams();
        String[] split = message.split(":");
        String eName = split[0] + ":";
        String userId = split[1];

        // 检查是否为异地登录事件
        if (FuFuEventEnum.REMOTE_LOGIN.getValue().equals(eName)) {
            remoteLoginDispose(userId);
        }
        // 检查是否为检查异地登录事件
        if (FuFuEventEnum.CHECK_REMOTE_LOGIN.getValue().equals(eName)) {
            checkRemoteLogin(params);
        }
    }

    /**
     * 检查远程登录情况，根据用户登录地址和注册地址是否相同来决定下一步操作
     * 如果登录地址与用户注册地址或上级地址相同，则增加登录计数
     * 如果不相同，则更新用户地址信息并进行远程登录检查
     *
     * @param params 包含用户信息和登录信息的参数映射
     */
    private void checkRemoteLogin(Map<String, Object> params) {
        // 从参数中获取用户对象
        User user = (User) params.get("user");
        // 从参数中获取登录信息对象
        LoginDto loginDto = (LoginDto) params.get("loginDto");

        // 判断登录地址是否与用户注册地址或上级地址相同
        boolean isSameAddr = loginDto.getAddr().equals(user.getAddr()) &&
                (!ObjectUtils.isEmpty(user.getUpAddr()) && loginDto.getAddr().equals(user.getUpAddr()));

        // 如果地址相同，则增加登录计数
        if (isSameAddr) {
            incrementLoginCount(loginDto.getPhone());
        } else {
            // 如果地址不同，则更新用户地址信息并进行远程登录检查
            updateAddressAndCheckRemoteLogin(user, loginDto);
        }
    }


    /**
     * 增加用户登录次数
     *
     * @param phone 手机号，用于标识用户
     *              <p>
     *              此方法通过Redis实现，每次用户登录时调用此方法
     *              它会根据用户手机号在Redis中增加登录计数
     *              这有助于监控用户行为和系统性能
     */
    private void incrementLoginCount(String phone) {
        stringRedisTemplate.opsForValue().increment(RedisKeyConstants.USER_LOGIN_COUNT_PREFIX + phone);
    }


    /**
     * 更新用户地址信息并检查异地登录情况
     * 此方法首先更新用户的地址信息，然后检查用户是否在不同地点频繁登录
     * 如果登录次数达到一定阈值，触发异地登录事件，并重置登录次数计数
     *
     * @param user     当前用户对象，包含用户的相关信息
     * @param loginDto 登录数据传输对象，包含登录所需的地址等信息
     */
    private void updateAddressAndCheckRemoteLogin(User user, LoginDto loginDto) {
        // 更新用户地址信息
        user.setUpAddr(user.getAddr());
        user.setAddr(loginDto.getAddr());
        // 使用更新方法更新用户地址，如果更新失败，抛出系统错误异常
        ThrowUtils.throwIf(!userService.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId())), ErrorCode.SYSTEM_ERROR);

        // 检查异地登录情况
        String loginCountKey = RedisKeyConstants.USER_LOGIN_COUNT_PREFIX + user.getPhone();
        // 从Redis中获取用户登录次数，如果不存在，则默认为0
        Integer loginCount = Optional.ofNullable(stringRedisTemplate.opsForValue().get(loginCountKey))
                .map(Integer::parseInt)
                .orElse(0);

        // 如果登录次数达到3次或以上，认为可能存在异地登录风险
        if (loginCount >= 3) {
            // 触发异地登录事件，执行相关处理逻辑，并删除Redis中的登录次数计数
            fuFuEventPublisher.doStuffAndPublishAnEvent(REMOTE_LOGIN.getValue() + user.getId(), null);
            stringRedisTemplate.delete(loginCountKey);
        } else {
            // 如果登录次数未达到阈值，增加登录次数计数
            incrementLoginCount(loginDto.getPhone());
        }
    }


    /**
     * 处理远程登录的逻辑
     * 当用户尝试从远程登录时，此方法用于发送验证短信
     *
     * @param userId 用户ID，用于查询用户信息
     */
    private void remoteLoginDispose(String userId) {
        // 根据用户ID查询用户信息
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, userId));
        // 如果用户信息为空，则抛出异常，表示用户未找到
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.FORBIDDEN_ERROR);

        // 初始化短信发送参数对象
        SMSRequestParam smsRequestParam = new SMSRequestParam();
        // 设置短信接收手机号码
        smsRequestParam.setPhone(user.getPhone());
        // 设置短信模板代码
        smsRequestParam.setTemplateCodeStr("remoteLogin");
        // 设置用户昵称，用于短信内容
        smsRequestParam.setUserNick(user.getNickname());
        // 设置当前时间，用于短信内容
        smsRequestParam.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        // 设置用户地址，用于短信内容
        smsRequestParam.setAddress(user.getAddr());

        // 初始化HTTP请求头对象
        HttpHeaders httpHeaders = new HttpHeaders();
        // 生成请求验证码，并添加到请求头中
        String reqCode = DigestUtils.md5DigestAsHex((SystemConstant.SALT + smsReqCode).getBytes());
        httpHeaders.add("reqCode", reqCode);

        // 调用Feign客户端方法，发送短信
        txFeignApi.requestSms(smsRequestParam, httpHeaders);
    }

}

