package cn.graht.user.event;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.sms.TXFeignApi;
import cn.graht.model.sms.dto.SMSRequestParam;
import cn.graht.model.user.pojos.User;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.graht.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Slf4j
public class FuFuEventListener implements ApplicationListener<FuFuEvent> {

    @Resource
    private UserService userService;
    @Resource
    private TXFeignApi txFeignApi;

    @Override
    public void onApplicationEvent(FuFuEvent event) {
        String message = event.getMessage();
        String[] split = message.split(":");
        String eName = split[0];
        String userId = split[1];
        if (FuFuEventEnum.REMOTE_LOGIN.getValue().equals(eName)) {
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, userId));
            ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.FORBIDDEN_ERROR);
            SMSRequestParam smsRequestParam = new SMSRequestParam();
            smsRequestParam.setPhone(user.getPhone());
            smsRequestParam.setTemplateCodeStr("remoteLogin");
            smsRequestParam.setUserNick(user.getNickname());
            smsRequestParam.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            smsRequestParam.setAddress(user.getAddr());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("reqCode","a8c97810016369c2c2a842d636157f5f");
            txFeignApi.requestSms(smsRequestParam,httpHeaders);
        }
        //处理事件
        System.out.println("Received custom event - " + event.getMessage());
    }
}
