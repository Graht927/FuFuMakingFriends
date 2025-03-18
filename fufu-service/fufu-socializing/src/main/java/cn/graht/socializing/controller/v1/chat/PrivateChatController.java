package cn.graht.socializing.controller.v1.chat;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.CreateMessageDto;
import cn.graht.model.socializing.dtos.CreatePrivateSessionDto;
import cn.graht.model.socializing.pojos.PrivateChatMessage;
import cn.graht.model.socializing.pojos.PrivateChatSession;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.service.PrivateChatMessageService;
import cn.graht.socializing.service.PrivateChatSessionService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/privateChat")
@Tag(name = "私聊",description = "私聊")
public class PrivateChatController {
    @Resource
    private PrivateChatSessionService privateChatSessionService;

    @Resource
    private PrivateChatMessageService privateChatMessageService;

    @PostMapping("/session")
    @Operation(summary = "创建私聊会话", description = "创建私聊会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> createSession(@RequestBody CreatePrivateSessionDto session) {
        PrivateChatSession session1 = new PrivateChatSession();
        BeanUtils.copyProperties(session, session1);
        return ResultUtil.ok(privateChatSessionService.save(session1));
    }


    @GetMapping("/session/{id}")
    @Operation(summary = "通过id获取会话", description = "通过Id获取Session")
    @ApiResponse(responseCode = "200", description = "会话信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<PrivateChatSession> getSession(@PathVariable Integer id) {
        return ResultUtil.ok(privateChatSessionService.getById(id));
    }

    @PutMapping("/session/{id}")
    @Operation(summary = "更新session会话", description = "修改会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> updateSession(@PathVariable Integer id, @RequestBody CreatePrivateSessionDto session) {
        PrivateChatSession session1 = new PrivateChatSession();
        BeanUtils.copyProperties(session, session1);
        session1.setId(id);
        return ResultUtil.ok(privateChatSessionService.updateById(session1));
    }

    @DeleteMapping("/session/{id}")
    @Operation(summary = "删除Session会话", description = "删除会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> deleteSession(@PathVariable Integer id) {
        boolean b = privateChatSessionService.removeById(id);
        ThrowUtils.throwIf(!b, ErrorCode.NULL_ERROR);
        LambdaQueryWrapper<PrivateChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateChatMessage::getSessionId, id);
        boolean remove = privateChatMessageService.remove(queryWrapper);
        ThrowUtils.throwIf(!remove, ErrorCode.NULL_ERROR);
        return ResultUtil.ok(true);
    }

    @PostMapping("/message")
    @Operation(summary = "创建私聊消息", description = "创建私聊消息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Integer> createMessage(@RequestBody CreateMessageDto messageDto) {
        PrivateChatMessage message = new PrivateChatMessage();
        BeanUtils.copyProperties(messageDto, message);
        Integer sessionId = message.getSessionId();
        LambdaQueryWrapper<PrivateChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateChatSession::getId, sessionId);
        PrivateChatSession one = privateChatSessionService.getOne(queryWrapper);
        boolean save = privateChatMessageService.save(message);
        one.setLastMessageTime(message.getSendTime());
        privateChatSessionService.update(one,queryWrapper);
        return ResultUtil.ok(message.getId());
    }

    @GetMapping("/message/{id}")
    @Operation(summary = "获取消息信息", description = "消息详情")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<PrivateChatMessage> getMessage(@PathVariable Integer id) {
        return ResultUtil.ok(privateChatMessageService.getById(id));
    }


    @DeleteMapping("/message/{id}")
    @Operation(summary = "删除消息", description = "通过id删除消息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> deleteMessage(@PathVariable Integer id) {
        return ResultUtil.ok(privateChatMessageService.removeById(id));
    }

    @PostMapping("/messages/session/{sessionId}")
    @Operation(summary = "获取消息列表", description = "通过会话获取消息列表")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<List<PrivateChatMessage>> getMessagesBySessionId(@PathVariable Integer sessionId,@RequestBody PageQuery pageQuery) {
        String loginId = (String) StpUtil.getLoginId();
        LambdaQueryWrapper<PrivateChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateChatSession::getId, sessionId);
        PrivateChatSession one = privateChatSessionService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(one), ErrorCode.PARAMS_ERROR);
        Page<PrivateChatMessage> privateChatMessagePage = new Page<>(pageQuery.getPageSize(), pageQuery.getPageNum());
        return ResultUtil.ok(privateChatMessageService.lambdaQuery().eq(PrivateChatMessage::getSessionId, sessionId).page(privateChatMessagePage).getRecords());
    }
}
