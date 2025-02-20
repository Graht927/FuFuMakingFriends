package cn.graht.socializing.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.dtos.AddCommentsDto;
import cn.graht.model.socializing.dtos.AddThumbsupDto;
import cn.graht.model.socializing.pojos.Comments;
import cn.graht.model.socializing.pojos.Thumbsup;
import cn.graht.model.socializing.vos.ThumbsupVo;
import cn.graht.model.user.pojos.Dynamic;
import cn.hutool.core.collection.CollectionUtil;
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

    private long hashToIndex(String uid, Long did) {
        String combinedKey = did +":"+ uid;
        return Math.abs(combinedKey.hashCode());
    }

    @PostMapping("{cid}")
    @Operation(summary = "通过cid获取点赞信息|分页", description = "通过cid获取点赞信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<ThumbsupVo>> getThubmsUpByCid(@PathVariable Long cid, @RequestBody PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0L, ErrorCode.PARAMS_ERROR);
        List<ThumbsupVo> thubms = thumbsupService.getThubmsUpByCid(cid, pageQuery);
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
        return ResultUtil.ok(true);
    }

    @DeleteMapping("/{uid}/{did}")
    @Operation(summary = "取消点赞", description = "取消点赞")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> deleteThumbsup(@PathVariable Long did , @PathVariable String uid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(did) || did < 0L, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<Thumbsup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Thumbsup::getDynamicId, did)
                .eq(Thumbsup::getUserId, uid);
        boolean remove = thumbsupService.remove(lambdaQueryWrapper);
        ThrowUtils.throwIf(!remove, ErrorCode.SYSTEM_ERROR);
        RHyperLogLog<Object> hyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_DEL_KEY + did);
        hyperLogLog.add(uid);
        hyperLogLog.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //更新bitmap
        String bitmapKey = RedisKeyConstants.getShardedCacheKey(did, uid);
        RBitSet bitSet = redisson.getBitSet(bitmapKey);
        bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        long index = hashToIndex(uid,did);
        bitSet.clear(index);
        return ResultUtil.ok(true);
    }


    //判断这条动态是否已经点赞
    @GetMapping("/{did}/{uid}")
    @Operation(summary = "判断这条动态是否已经点赞", description = "判断这条动态是否已经点赞")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> isThumbsup(@PathVariable Long did, @PathVariable String uid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(did) || did < 0L, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        // 使用BitMap检查点赞状态
        String bitmapKey = RedisKeyConstants.getShardedCacheKey(did, uid);
        RBitSet bitSet = redisson.getBitSet(bitmapKey);
        bitSet.expire(RedisKeyConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        long index = hashToIndex(uid,did);
        boolean isThumbsup = bitSet.get(index);
        if (isThumbsup) {
            return ResultUtil.ok(true);
        }

        Long count = thumbsupService.lambdaQuery()
                .eq(Thumbsup::getDynamicId, did)
                .eq(Thumbsup::getUserId, uid).count();
        if (count == 1) {
            bitSet.set(index);
            return ResultUtil.ok(true);
        }
        return ResultUtil.ok(false);
    }
    @GetMapping("/s/{did}/{uid}")
    @Operation(summary = "获取总赞数", description = "获取总赞数")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Long> getThumbsupCount(@PathVariable Long did,@PathVariable String uid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(did) || did < 0L, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        //直接从redis中拿
        RHyperLogLog<Object> addHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_ADD_KEY + did);
        long addCount = addHyperLogLog.count();
        RHyperLogLog<Object> delHyperLogLog = redisson.getHyperLogLog(RedisKeyConstants.THUMBSUP_DEL_KEY + did);
        long delCount = delHyperLogLog.count();
        ResultApi<Dynamic> dynamicById = userFeignApi.getDynamicById(did);
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
