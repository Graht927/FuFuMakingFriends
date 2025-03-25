package cn.graht.socializing.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.dtos.AddThumbsupDto;
import cn.graht.model.socializing.pojos.Thumbsup;
import cn.graht.model.socializing.vos.ThumbsupVo;
import cn.graht.model.user.dtos.EditDynamicDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.common.enums.NoticeType;
import cn.graht.socializing.event.FuFuEventEnum;
import cn.graht.socializing.event.FuFuEventPublisher;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RHyperLogLog;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import cn.graht.socializing.service.ThumbsupService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/thumbsUp")
@Tag(name = "点赞信息", description = "点赞信息")
public class ThumbsupController {
    @Resource
    private ThumbsupService thumbsupService;
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private Redisson redisson;
    @Resource
    private FuFuEventPublisher fuFuEventPublisher;

    private long hashToIndex(String uid, Long did) {
        String combinedKey = did +":"+ uid;
        return Math.abs(combinedKey.hashCode());
    }

    @PostMapping("{dynamicId}")
    @Operation(summary = "通过dynamicId获取点赞信息|分页", description = "通过cid获取点赞信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<ThumbsupVo>> getThubmsUpByCid(@PathVariable Long dynamicId, @RequestBody PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicId) || dynamicId < 0L, ErrorCode.PARAMS_ERROR);
        List<ThumbsupVo> thubms = thumbsupService.getThubmsUpByCid(dynamicId, pageQuery);
        return ResultUtil.ok(thubms);
    }

    @PostMapping
    @Operation(summary = "点赞", description = "点赞")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> addThumbsup(@RequestBody AddThumbsupDto addThumbsupDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(addThumbsupDto), ErrorCode.PARAMS_ERROR);
        Thumbsup thumbsup = new Thumbsup();
        BeanUtils.copyProperties(addThumbsupDto, thumbsup);
        boolean save = thumbsupService.save(thumbsup);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        RHyperLogLog<Object> hyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_ADD_KEY + addThumbsupDto.getDynamicId());
        hyperLogLog.add(addThumbsupDto.getUserId());
        hyperLogLog.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        // 更新BitMap
        String bitmapKey = RedisKeyConstants.getShardedCacheKey(thumbsup.getDynamicId(), thumbsup.getUserId());
        RBitSet bitSet = redisson.getBitSet(bitmapKey);
        bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        long index = hashToIndex(thumbsup.getUserId(), thumbsup.getDynamicId());
        bitSet.set(index);
        fuFuEventPublisher.doStuffAndPublishAnEvent(FuFuEventEnum.DYNAMIC_NOTICE.getValue(),
                Map.of("type", NoticeType.THUMBS_UP.getValue(),
                        "userId2",thumbsup.getUserId(),"dynamicId",thumbsup.getDynamicId().toString()));
        //更新 动态的likeCount
        EditDynamicDto editDynamicDto = new EditDynamicDto();
        Dynamic dynamic = userFeignApi.getDynamicById(thumbsup.getDynamicId()).getData();
        editDynamicDto.setId(dynamic.getId());
        editDynamicDto.setLikeCount(dynamic.getLikeCount() + 1);
        userFeignApi.updateDynamic(thumbsup.getDynamicId(), editDynamicDto);
        return ResultUtil.ok(true);
    }

    @DeleteMapping("/{uid}/{dynamicId}")
    @Operation(summary = "取消点赞", description = "取消点赞")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> deleteThumbsup(@PathVariable Long dynamicId , @PathVariable String uid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicId) || dynamicId < 0L, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<Thumbsup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Thumbsup::getDynamicId, dynamicId)
                .eq(Thumbsup::getUserId, uid);
        boolean remove = thumbsupService.remove(lambdaQueryWrapper);
        ThrowUtils.throwIf(!remove, ErrorCode.SYSTEM_ERROR);
        RHyperLogLog<Object> hyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_DEL_KEY + dynamicId);
        hyperLogLog.add(uid);
        hyperLogLog.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //更新bitmap
        String bitmapKey = RedisKeyConstants.getShardedCacheKey(dynamicId, uid);
        RBitSet bitSet = redisson.getBitSet(bitmapKey);
        bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        long index = hashToIndex(uid,dynamicId);
        bitSet.clear(index);
        //更新 动态的likeCount
        EditDynamicDto editDynamicDto = new EditDynamicDto();
        Dynamic dynamic = userFeignApi.getDynamicById(dynamicId).getData();
        editDynamicDto.setId(dynamic.getId());
        editDynamicDto.setLikeCount(dynamic.getLikeCount() - 1);
        userFeignApi.updateDynamic(dynamicId, editDynamicDto);
        return ResultUtil.ok(true);
    }


    //判断这条动态是否已经点赞
    @GetMapping("/{dynamicId}/{uid}")
    @Operation(summary = "判断这条动态是否已经点赞", description = "判断这条动态是否已经点赞")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> isThumbsup(@PathVariable Long dynamicId, @PathVariable String uid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicId) || dynamicId < 0L, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        // 使用BitMap检查点赞状态
        String bitmapKey = RedisKeyConstants.getShardedCacheKey(dynamicId, uid);
        RBitSet bitSet = redisson.getBitSet(bitmapKey);
        bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        long index = hashToIndex(uid,dynamicId);
        boolean isThumbsup = bitSet.get(index);
        if (isThumbsup) {
            return ResultUtil.ok(true);
        }

        Long count = thumbsupService.lambdaQuery()
                .eq(Thumbsup::getDynamicId, dynamicId)
                .eq(Thumbsup::getUserId, uid).count();
        if (count == 1) {
            bitSet.set(index);
            return ResultUtil.ok(true);
        }
        return ResultUtil.ok(false);
    }
    @GetMapping("/s/{dynamicId}")
    @Operation(summary = "获取总赞数", description = "获取总赞数")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Long> getThumbsupCount(@PathVariable Long dynamicId) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicId) || dynamicId < 0L, ErrorCode.PARAMS_ERROR);
        //直接从redis中拿
        RHyperLogLog<Object> addHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_ADD_KEY + dynamicId);
        long addCount = addHyperLogLog.count();
        RHyperLogLog<Object> delHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_DEL_KEY + dynamicId);
        long delCount = delHyperLogLog.count();
        ResultApi<Dynamic> dynamicById = userFeignApi.getDynamicById(dynamicId);
        long dbCount = 0;
        Dynamic data = dynamicById.getData();
        if (!ObjectUtils.isEmpty(data)){
            dbCount = data.getLikeCount();
        }
        long count = addCount - delCount + dbCount;
        //todo 发送异步mq对数据对齐
        return ResultUtil.ok(count);
    }
}
