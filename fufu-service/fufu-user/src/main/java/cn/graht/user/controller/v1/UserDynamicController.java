package cn.graht.user.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.user.dtos.CreateDynamicDto;
import cn.graht.model.user.dtos.EditDynamicDto;
import cn.graht.model.user.dtos.GetDynamicByUidDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.model.user.vos.UserDynamicVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.service.DynamicService;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RHyperLogLog;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/v1/dynamics")
@Tag(name = "用户动态", description = "用户动态Controller")
public class UserDynamicController {

    @Resource
    private DynamicService dynamicService;
    @Resource
    private Redisson redisson;
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private SocializingFeignApi socializingFeignApi;

    // 根据用户ID查询动态
    @PostMapping("/user/byUid")
    @Operation(summary = "通过uid获取|分页", description = "通过uid获取动态信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<DynamicVo>> getDynamicsByUserId(@RequestBody GetDynamicByUidDto getDynamicByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getDynamicByUidDto), ErrorCode.PARAMS_ERROR);
        String loginId = (String) StpUtil.getLoginId();
        List<Dynamic> dynamics = dynamicService.getDynamicsByUserId(getDynamicByUidDto);
        if (dynamics != null && !dynamics.isEmpty()) {
            List<DynamicVo> dynamicVos = dynamics.stream().map(dynamic -> {
                DynamicVo dynamicVo = new DynamicVo();
                try {
                    if (ObjectUtils.isNotEmpty(dynamic.getImages())){
                        String images = dynamic.getImages();
                        String substring = images.substring(1, images.length() - 1);
                        dynamicVo.setImages(List.of(substring.split(",")));
                    }
                    if (ObjectUtils.isNotEmpty(dynamic.getCoverImages())){
                        dynamicVo.setImage(dynamic.getCoverImages());
                    }
                    BeanUtils.copyProperties(dynamic, dynamicVo);
                    ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(dynamic.getUserId());
                    if (ObjectUtils.isNotEmpty(userInfo)) {
                        UserDynamicVo userDynamicVo = new UserDynamicVo();
                        userDynamicVo.setAvatar(userInfo.getData().getAvatarUrl());
                        userDynamicVo.setName(userInfo.getData().getNickname());
                        dynamicVo.setAuthor(userDynamicVo);
                    }
                    ResultApi<Boolean> thumbsup = socializingFeignApi.isThumbsup(dynamic.getId(), loginId);
                    dynamicVo.setIsLike(thumbsup.getData());
                    return dynamicVo;
                } catch (Exception e) {
                    return dynamicVo;
                }
            }).toList();
            return ResultUtil.ok(dynamicVos);
        } else {
            return ResultUtil.ok(List.of());
        }
    }

    // 根据ID查询动态
    @GetMapping("/{id}")
    @Operation(summary = "通过id获取", description = "通过id获取动态信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<DynamicVo> getDynamicById(@PathVariable Long id) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(id) || id < 0L, ErrorCode.PARAMS_ERROR);
        Dynamic dynamic = dynamicService.getById(id);
        String loginId = (String) StpUtil.getLoginId();
        DynamicVo dynamicVo = new DynamicVo();
        try {
            if (ObjectUtils.isNotEmpty(dynamic.getImages())){
                String images = dynamic.getImages();
                String substring = images.substring(1, images.length() - 1);
                dynamicVo.setImages(List.of(substring.split(",")));
            }
            if (ObjectUtils.isNotEmpty(dynamic.getCoverImages())){
                dynamicVo.setImage(dynamic.getCoverImages());
            }
            BeanUtils.copyProperties(dynamic, dynamicVo);
            ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(dynamic.getUserId());
            if (ObjectUtils.isNotEmpty(userInfo)) {
                UserDynamicVo userDynamicVo = new UserDynamicVo();
                userDynamicVo.setAvatar(userInfo.getData().getAvatarUrl());
                userDynamicVo.setName(userInfo.getData().getNickname());
                dynamicVo.setAuthor(userDynamicVo);
            }
            ResultApi<Boolean> thumbsup = socializingFeignApi.isThumbsup(dynamic.getId(), loginId);
            dynamicVo.setIsLike(thumbsup.getData());
        } catch (Exception e) {
            log.error("dynamic getting fail");
        }
        return ResultUtil.ok(dynamicVo);
    }

    // 创建动态
    @PostMapping
    @Operation(summary = "创建动态", description = "创建新的动态信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<DynamicVo> createDynamic(@RequestBody CreateDynamicDto createDynamicDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(createDynamicDto)
                , ErrorCode.PARAMS_ERROR);
        DynamicVo dynamic = dynamicService.createDynamic(createDynamicDto);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamic), ErrorCode.SYSTEM_ERROR);
        ((Runnable) () -> {
            initRedisDynamic(dynamic.getId());
        }).run();
        return ResultUtil.ok(dynamic);
    }

    // 更新动态
    @PutMapping("/{id}")
    @Operation(summary = "修改动态信息", description = "修改动态")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Dynamic> updateDynamic(@PathVariable Long id, @RequestBody EditDynamicDto editDynamicDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(id) || id < 0L
                || ObjectUtils.isEmpty(editDynamicDto), ErrorCode.PARAMS_ERROR);
        editDynamicDto.setId(id);
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(editDynamicDto, dynamic);
        List<String> images = editDynamicDto.getImages();
        if (ObjectUtils.isNotEmpty(images)) {
            dynamic.setImages(JSONUtil.toJsonStr(images));
        }
        boolean updated = dynamicService.updateById(dynamic);
        if (updated) {
            return ResultUtil.ok(dynamic);
        } else {
            return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    // 删除动态
    @DeleteMapping("/{id}")
    @Operation(summary = "删除动态", description = "通过id删除动态信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Void> deleteDynamic(@PathVariable Long id) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(id) || id < 0L, ErrorCode.PARAMS_ERROR);
        boolean deleted = dynamicService.removeById(id);
        if (deleted) {
            return ResultUtil.ok();
        } else {
            return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    private void initRedisDynamic(Long did) {
        //初始化Dynamic的redis
        System.out.println("初始化id为" + did + "的redis");
        RHyperLogLog<Object> addHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_ADD_KEY + did);
        addHyperLogLog.add("0");
        addHyperLogLog.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        RHyperLogLog<Object> delHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_DEL_KEY + did);
        delHyperLogLog.add("0");
        delHyperLogLog.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        for (int i = 0; i < RedisKeyConstants.SHARD_COUNT; i++) {
            String bitmapKey = RedisKeyConstants.THUMBSUP_KEY + did + ":" + i;
            RBitSet bitSet = redisson.getBitSet(bitmapKey);
            bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }
}
