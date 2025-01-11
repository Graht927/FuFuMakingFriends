package cn.graht.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.graht.user.feign.TestOrderFeign;
import cn.graht.common.ErrorCode;
import cn.graht.common.ResultApi;
import cn.graht.common.ResultUtil;
import exception.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GRAHT
 */

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    private TestOrderFeign testOrderFeign;

    @GetMapping("/t1")
    public String t1(){
        return  "dsad";
    }

    @GetMapping("/login")
    public ResultApi<SaTokenInfo> login(@RequestParam String username,@RequestParam String password){
        ThrowUtils.throwIf(StringUtils.isBlank(username) || StringUtils.isBlank(password),ErrorCode.LOGIN_PARAMS_ERROR);
        StpUtil.login(1001);
        return ResultUtil.ok(StpUtil.getTokenInfo());
    }

    @SaCheckPermission("user")
    @GetMapping("/userPower")
    public String userPower(){
        log.info("被调用啦");
        return  "user权限";
    }
    @GetMapping("/test")
    public String test(){
        log.info("被调用啦");
        return  "user-serviceTestFeign";
    }
    @GetMapping("/ot")
    public ResultApi<String> ot(){
        log.info("开始测试 user => feign => order (/test/ut)");
        ThrowUtils.throwIf(true, ErrorCode.SYSTEM_ERROR);
        return ResultUtil.ok(testOrderFeign.test());
    }
}

