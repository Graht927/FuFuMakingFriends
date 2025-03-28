package cn.graht.socializing.controller.v1.chat;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.CreateGroupChatMemberDto;
import cn.graht.model.socializing.dtos.CreateGroupMessageDto;
import cn.graht.model.socializing.dtos.CreateGroupSessionDto;
import cn.graht.model.socializing.dtos.EditGroupChatMemberDto;
import cn.graht.model.socializing.pojos.GroupChatMember;
import cn.graht.model.socializing.pojos.GroupChatMessage;
import cn.graht.model.socializing.pojos.GroupChatSession;
import cn.graht.model.socializing.vos.MessageVo;
import cn.graht.model.socializing.vos.SessionVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.service.GroupChatMemberService;
import cn.graht.socializing.service.GroupChatMessageService;
import cn.graht.socializing.service.GroupChatSessionService;
import cn.graht.socializing.utils.UserToolUtils;
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
@RequestMapping("/v1/groupChat")
@Tag(name = "群聊",description = "群聊")
public class GroupChatController {
    @Resource
    private GroupChatSessionService groupChatSessionService;

    @Resource
    private GroupChatMemberService groupChatMemberService;

    @Resource
    private GroupChatMessageService groupChatMessageService;

    @Resource
    private UserToolUtils userToolUtils;

    @PostMapping("/session")
    @Operation(summary = "创建群聊会话", description = "创建群聊会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    //todo 分布式事务
    public ResultApi<Integer> createSession(@RequestBody CreateGroupSessionDto sessionDto) {
        GroupChatSession session = new GroupChatSession();
        BeanUtils.copyProperties(sessionDto, session);
        boolean save = groupChatSessionService.save(session);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        CreateGroupChatMemberDto createGroupChatMemberDto = new CreateGroupChatMemberDto();
        createGroupChatMemberDto.setGroupId(session.getId());
        createGroupChatMemberDto.setUserId(StpUtil.getLoginId().toString());
        createGroupChatMemberDto.setRole(2);
        addMember(createGroupChatMemberDto);
        return ResultUtil.ok(session.getId());
    }
    @GetMapping("/session/all/{userId}")
    @Operation(summary = "获取用户所有会话", description = "获取用户会话列表")
    @ApiResponse(responseCode = "200", description = "SessionVos")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<SessionVo>> getSessionList(@PathVariable String userId) {
        LambdaQueryWrapper<GroupChatMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupChatMember::getUserId, userId);
        List<SessionVo> list = groupChatMemberService.selectListByUserId(userId);
        return ResultUtil.ok(list);
    }

    @GetMapping("/session/{id}")
    @Operation(summary = "获取群聊会话", description = "通过id获取群聊会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<GroupChatSession> getSession(@PathVariable Integer id) {
        return ResultUtil.ok(groupChatSessionService.getById(id));
    }

    @PutMapping("/session/{id}")
    @Operation(summary = "修改群聊会话", description = "修改群聊会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> updateSession(@PathVariable Integer id, @RequestBody CreateGroupSessionDto sessionDto) {
        GroupChatSession session = new GroupChatSession();
        BeanUtils.copyProperties(sessionDto, session);
        session.setId(id);
        return ResultUtil.ok(groupChatSessionService.updateById(session));
    }

    @DeleteMapping("/session/{id}")
    @Operation(summary = "删除群聊会话", description = "删除群聊会话")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> deleteSession(@PathVariable Long id) {
        return ResultUtil.ok(groupChatSessionService.removeById(id));
    }

    // 群聊成员相关接口
    @PostMapping("/member")
    @Operation(summary = "添加成员", description = "添加成员")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> addMember(@RequestBody CreateGroupChatMemberDto memberDto) {
        LambdaQueryWrapper<GroupChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupChatSession::getId, memberDto.getGroupId());
        GroupChatSession one = groupChatSessionService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(one), ErrorCode.PARAMS_ERROR);
        GroupChatMember member = new GroupChatMember();
        BeanUtils.copyProperties(memberDto, member);
        boolean save = groupChatMemberService.save(member);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        one.setMemberCount(one.getMemberCount() + 1);
        boolean update = groupChatSessionService.update(one, queryWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return ResultUtil.ok(true);
    }

    @GetMapping("/member/{id}")
    @Operation(summary = "获取成员信息", description = "获取成员信息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<GroupChatMember> getMember(@PathVariable Integer id) {
        return ResultUtil.ok(groupChatMemberService.getById(id));
    }

    @PutMapping("/member/{id}")
    @Operation(summary = "修改成员信息", description = "修改成员信息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> updateMember(@PathVariable Integer id, @RequestBody EditGroupChatMemberDto memberDto) {
        GroupChatMember member = new GroupChatMember();
        BeanUtils.copyProperties(memberDto, member);
        member.setId(id);
        return ResultUtil.ok(groupChatMemberService.updateById(member));
    }

    @DeleteMapping("/member/{id}")
    @Operation(summary = "退出群聊", description = "退出群聊")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> deleteMember(@PathVariable Integer id) {
        return ResultUtil.ok(groupChatMemberService.removeById(id));
    }

    @GetMapping("/members/session/{sessionId}")
    @Operation(summary = "获取全部成员", description = "通过会话id获取全部成员")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<GroupChatMember>> getMembersBySessionId(@PathVariable Integer sessionId) {
        return ResultUtil.ok(groupChatMemberService.lambdaQuery().eq(GroupChatMember::getGroupId, sessionId).list());
    }

    // 群聊消息相关接口

    @PostMapping("/message")
    @Operation(summary = "创建群聊消息", description = "创建群聊消息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Integer> createMessage(@RequestBody CreateGroupMessageDto messageDto) {
        GroupChatMessage message = new GroupChatMessage();
        BeanUtils.copyProperties(messageDto, message);
        boolean save = groupChatMessageService.save(message);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        GroupChatMessage byId = groupChatMessageService.getById(message.getId());
        LambdaQueryWrapper<GroupChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupChatSession::getId, messageDto.getGroupId());
        GroupChatSession one = groupChatSessionService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(one), ErrorCode.PARAMS_ERROR);
        one.setLastMessageTime(byId.getSendTime());
        boolean update = groupChatSessionService.update(one, queryWrapper);
        if (!update) return ResultUtil.ok(-1);
        return ResultUtil.ok(message.getId());
    }

    @GetMapping("/message/{id}")
    @Operation(summary = "获取群聊消息", description = "获取群聊消息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<GroupChatMessage> getMessage(@PathVariable Integer id) {
        return ResultUtil.ok(groupChatMessageService.getById(id));
    }


    @DeleteMapping("/message/{id}")
    @Operation(summary = "撤销消息", description = "撤销消息")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> deleteMessage(@PathVariable Integer id) {
        return ResultUtil.ok(groupChatMessageService.removeById(id));
    }

    @PostMapping("/messages/session/{sessionId}")
    @Operation(summary = "获取群聊消息列表", description = "获取群聊消息列表")
    @ApiResponse(responseCode = "200", description = "true")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<MessageVo>> getMessagesBySessionId(@PathVariable Integer sessionId, @RequestBody PageQuery pageQuery) {
        String loginId = (String) StpUtil.getLoginId();
        List<GroupChatMember> list = groupChatMemberService.lambdaQuery().eq(GroupChatMember::getGroupId, sessionId).list();
        ThrowUtils.throwIf(list.stream().noneMatch(groupChatMember -> groupChatMember.getUserId().equals(loginId)),ErrorCode.NO_AUTH);
        Page<GroupChatMessage> groupChatMessagePage = new Page<>(pageQuery.getPageSize(), pageQuery.getPageNum());
        List<GroupChatMessage> records = groupChatMessageService.lambdaQuery().eq(GroupChatMessage::getGroupId, sessionId).orderByDesc(GroupChatMessage::getSendTime)
                .page(groupChatMessagePage).getRecords();
        List<MessageVo> res = records.stream().map(t -> {
            MessageVo messageVo = new MessageVo();
            messageVo.setId(t.getId());
            messageVo.setMessage(t.getContent());
            UserVo user = userToolUtils.getUserFromCacheOrFeign(t.getSenderId());
            messageVo.setSenderAvatar(user.getAvatarUrl());
            messageVo.setIsSelf(t.getSenderId().equals(loginId));
            messageVo.setSendTime(t.getSendTime());
            return messageVo;
        }).toList();
        return ResultUtil.ok(res);
    }



}
