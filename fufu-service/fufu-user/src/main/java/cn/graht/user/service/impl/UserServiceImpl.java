package cn.graht.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.UserConstant;
import cn.graht.common.exception.BusinessException;
import cn.graht.model.user.dtos.EditUserInfoDto;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.dtos.RandomGetUserDto;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.boot.UserRedissonCache;
import cn.graht.user.controller.v1.SearchUserController;
import cn.graht.user.event.FuFuEventEnum;
import cn.graht.user.event.FuFuEventPublisher;
import cn.graht.user.mapper.UserMapper;
import cn.graht.user.mq.producer.UserUnregisterProducer;
import cn.graht.user.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GRAHT
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-01-12 10:42:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private FuFuEventPublisher fuFuEventPublisher;
    @Resource
    private Redisson redisson;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRedissonCache userRedissonCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaTokenInfo login(LoginDto loginDto) {
        RLock lock = redisson.getLock(RedisKeyConstants.USER_LOGIN_LOCK_PREFIX + loginDto.getPhone());
        lock.lock();
        SaTokenInfo tokenInfo = null;
        try {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(loginDto) ||
                            StringUtils.isAnyBlank(loginDto.getPhone()) ||
                            loginDto.getPhoneCode().length() != 6,
                    ErrorCode.LOGIN_PARAMS_ERROR);
            //校验手机验证码是否正确
            String redisKey = RedisKeyConstants.SMS_LOGIN_PREFIX + loginDto.getPhone();
            String captcha = redisson.getBucket(redisKey).get().toString();
            ThrowUtils.throwIf(!loginDto.getPhoneCode().equals(captcha), ErrorCode.USER_PHONE_CODE_ERROR);
            User user = null;
            if (StringUtils.isNotBlank(loginDto.getUserPassword())){
                String userPassword = DigestUtils.md5DigestAsHex((SystemConstant.SALT + loginDto.getUserPassword()).getBytes());
                user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, loginDto.getPhone()).eq(User::getUserPassword, userPassword));
                ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.USER_NOT_ERROR);
            }else {
                user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, loginDto.getPhone()));
                ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.USER_NOT_ERROR);
            }
            StpUtil.login(user.getId());
            redisson.getBucket(redisKey).delete();
            HashMap<String, Object> eventParams = new HashMap<>();
            eventParams.put("user", user);
            eventParams.put("loginDto", loginDto);
            //todo 测试期间不调用tx接口
            /*((Runnable) () -> {
                fuFuEventPublisher.doStuffAndPublishAnEvent(
                        FuFuEventEnum.CHECK_REMOTE_LOGIN.getValue() + user.getId()
                        , eventParams);
            }).run();*/
            /*
            已经修改为事件发布响应
            //修改当前数据库地址[addr] 上一次地址放入数据库upAddr
            user.setUpAddr(user.getAddr());
            user.setAddr(loginDto.getAddr());
            ThrowUtils.throwIf(!update(user,new LambdaQueryWrapper<User>().eq(User::getId,user.getId())), ErrorCode.SYSTEM_ERROR);

            if (loginDto.getAddr().equals(user.getUpAddr())) {
                //如果 当前登录地址和上一次mysql一致
                //redis 地址登录 + 1
                stringRedisTemplate.opsForValue().increment(RedisKeyConstants.USER_LOGIN_COUNT_PREFIX + loginDto.getPhone());
            } else {
                //如果 当前登录地址和mysql不一致
                //判断redis中的地址登录次数是否大于等于3
                String lc = stringRedisTemplate.opsForValue().get(RedisKeyConstants.USER_LOGIN_COUNT_PREFIX + user.getPhone());
                int loginCount = Integer.parseInt(lc == null ? "0" : lc);
                //如果登录次数>=3 发送短信事件
                if (loginCount >= 3) {
                    fuFuEventPublisher.doStuffAndPublishAnEvent(FuFuEventEnum.REMOTE_LOGIN.getValue() + user.getId(),null);
                    //将redis 地址登录清空 删除
                    stringRedisTemplate.delete(RedisKeyConstants.USER_LOGIN_COUNT_PREFIX + user.getPhone());
                }
            }
            */
            return StpUtil.getTokenInfo();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        RLock lock = redisson.getLock(RedisKeyConstants.USER_REGISTER_LOCK_PREFIX + registerDto.getPhone());
        lock.lock();
        try {
            //校验数据
            ThrowUtils.throwIf(ObjectUtils.isEmpty(registerDto)
                            || StringUtils.isBlank(registerDto.getNickname())
                            || StringUtils.isBlank(registerDto.getPhone())
                            || StringUtils.isBlank(registerDto.getPhoneCode())
                            || StringUtils.isBlank(registerDto.getUserPassword())
                            || StringUtils.isBlank(registerDto.getCheckPassword())
                            || ObjectUtils.isEmpty(registerDto.getBirthday())
                    , ErrorCode.LOGIN_PARAMS_ERROR);
            int nicknameLength = registerDto.getNickname().length();
            int phoneCodeLength = registerDto.getPhoneCode().length();
            ThrowUtils.throwIf(nicknameLength < 3 || nicknameLength > 8, ErrorCode.REGISTER_PARAMS_ERROR);
            ThrowUtils.throwIf(!ReUtil.isMatch(UserConstant.PHONE_NUMBER_PATTERN, registerDto.getPhone())
                    , ErrorCode.REGISTER_PARAMS_ERROR);
            ThrowUtils.throwIf(phoneCodeLength != 6, ErrorCode.REGISTER_PARAMS_ERROR);
            ThrowUtils.throwIf(!ReUtil.isMatch(UserConstant.PASSWORD_PATTERN, registerDto.getUserPassword()) ||
                    !ReUtil.isMatch(UserConstant.PASSWORD_PATTERN, registerDto.getUserPassword()), ErrorCode.REGISTER_PARAMS_ERROR);
            //判断二次密码是否一致
            ThrowUtils.throwIf(!registerDto.getUserPassword().equals(registerDto.getCheckPassword()), ErrorCode.REGISTER_PASSWORD_ERROR);

            //判断该手机号是否已注册
            long phoneCount = count(new LambdaQueryWrapper<User>().eq(User::getPhone, registerDto.getPhone()));
            ThrowUtils.throwIf(phoneCount == 1, ErrorCode.REGISTER_PHONE_ERROR);
            //判断nickname是否已存在
           /* long nicknameCount = count(new LambdaQueryWrapper<User>().eq(User::getNickname, registerDto.getNickname()));
            ThrowUtils.throwIf(nicknameCount == 1, ErrorCode.REGISTER_NICKNAME_ERROR);*/
            //否 => 判断验证码是否正确
            String redisKey = RedisKeyConstants.SMS_REGISTER_PREFIX + registerDto.getPhone();
            String captcha = stringRedisTemplate.opsForValue().get(redisKey);
            ThrowUtils.throwIf(!registerDto.getPhoneCode().equals(captcha), ErrorCode.USER_PHONE_CODE_ERROR);
            stringRedisTemplate.delete(redisKey);
            //所有信息都正确  将信息存放至数据库
            User user = new User();
            user.setNickname(registerDto.getNickname());
            user.setPhone(registerDto.getPhone());
            user.setUserPassword(DigestUtils.md5DigestAsHex((SystemConstant.SALT + registerDto.getUserPassword()).getBytes()));
            user.setAddr(registerDto.getAddr());
            user.setBirthday(registerDto.getBirthday());
            user.setAvatarUrl("/2025/03/23/f7afe779-8cf0-4253-a1a5-a918f4e61256.jpg");
            return save(user);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserVo editUserInfo(EditUserInfoDto editUserInfoDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editUserInfoDto), ErrorCode.PARAMS_NULL_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editUserInfoDto.getId()), ErrorCode.PARAMS_ERROR);
        String user1 = userRedissonCache.getUser(editUserInfoDto.getId());
        User user = null;
        User userVo = null;
        if (StringUtils.isNotBlank(user1)){
               userVo = JSONUtil.toBean(user1, User.class);
               user = new User();
               BeanUtils.copyProperties(userVo,user);
        }else {
            user = getById(editUserInfoDto.getId());
            ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.NULL_ERROR);
        }
        boolean isUpdated = false;
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getNickname()) && StringUtils.isNotBlank(editUserInfoDto.getNickname())) {
            //修改昵称
            user.setNickname(editUserInfoDto.getNickname());
            isUpdated = true;
        }
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getGender()) && editUserInfoDto.getGender() == 0 || editUserInfoDto.getGender() == 1) {
            //修改性别
            user.setGender(editUserInfoDto.getGender());
            isUpdated = true;
        }
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getAvatarUrl()) && StringUtils.isNotBlank(editUserInfoDto.getAvatarUrl())) {
            //修改头像
            user.setAvatarUrl(editUserInfoDto.getAvatarUrl());
            isUpdated = true;
        }
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getProfile()) && StringUtils.isNotBlank(editUserInfoDto.getProfile())) {
            //修改简介
            user.setProfile(editUserInfoDto.getProfile());
            isUpdated = true;
        }
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getTags()) && StringUtils.isNotBlank(editUserInfoDto.getTags())) {
            //修改标签
            user.setTags(editUserInfoDto.getTags());
            isUpdated = true;
        }
        if (ObjectUtils.isNotEmpty(editUserInfoDto.getEmail()) && StringUtils.isNotBlank(editUserInfoDto.getEmail()) && ReUtil.isMatch(UserConstant.EMAIL_PATTERN, editUserInfoDto.getEmail())) {
            //修改邮箱
            user.setEmail(editUserInfoDto.getEmail());
            isUpdated = true;
        }
        if (isUpdated) {
            boolean b = updateById(user);
            ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        }
        UserVo userVo1 = UserVo.objToVo(user);
        userRedissonCache.addUser(userVo1.getId(), JSONUtil.toJsonStr(userVo1));
        return userVo1;
    }


    @Resource //生产者
    @Lazy //循环依赖
    private UserUnregisterProducer userUnregisterProducer;
    private RMap<String, Boolean> unregisterRequests;

    @PostConstruct
    private void init() {
        String redisKey = RedisKeyConstants.USER_UNREGISTER_PREFIX + "maps";
        unregisterRequests = redisson.getMap(redisKey);
    }

    @PreDestroy
    public void destroy() {
        redisson.shutdown();
    }

    @Override
    public void sendUnregisterRequest(String uid) {
        ThrowUtils.throwIf(unregisterRequests.containsKey(uid), ErrorCode.USER_UNREGISTER_MQ_ERROR);
        unregisterRequests.put(uid, Boolean.TRUE);
    }

    @Override
    public boolean cancelUnregisterRequest(String userId) {
        // 从 ConcurrentHashMap 中移除用户ID
        return unregisterRequests.remove(userId) != null;
    }

    @Override
    public boolean UnregisterRemoveById(String uid) {
        if (!unregisterRequests.containsKey(uid)) {
            return false;
        }
        long count = count(new LambdaQueryWrapper<User>().eq(User::getId, uid));
        if (count != 1) {
            unregisterRequests.remove(uid);
            return false;
        }
        RLock lock = redisson.getLock(RedisKeyConstants.USER_UNREGISTER_LOCK + uid);
        lock.lock();
        try {
            if (unregisterRequests.containsKey(uid)) {
                // 如果存在，则进行注销操作
                unregisterRequests.remove(uid);
                return remove(new LambdaQueryWrapper<User>().eq(User::getId, uid));
            }else return false;
        }  catch (Exception e) {
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserIdsVo getAllUserId(PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(pageQuery), ErrorCode.PARAMS_ERROR);
        Page<User> page = new Page<>(pageQuery.getPageNum(),pageQuery.getPageSize());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getId);
        Page<User> userPage = userMapper.selectPage(page, lambdaQueryWrapper);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userPage),ErrorCode.NULL_ERROR);
        UserIdsVo userIdsVo = new UserIdsVo();
        userIdsVo.setTotal(userPage.getTotal());
        userIdsVo.setUserIds(userPage.getRecords().stream().map(user -> {
            return user.getId();
        }).toList());
        return userIdsVo;
    }

    @Override
    public List<UserVo> randomGetUserVo(RandomGetUserDto randomGetUserDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(randomGetUserDto),ErrorCode.PARAMS_ERROR);
        List<UserVo> userVos = userMapper.randomGetUserVo(randomGetUserDto, randomGetUserDto.getPageNum()* randomGetUserDto.getPageSize());
        if (ObjectUtils.isEmpty(userVos)) return List.of();
        Collections.shuffle(userVos);
        if (userVos.size()<=10) return userVos;else userVos = userVos.stream().limit(10).toList();
        return userVos;
    }
}




