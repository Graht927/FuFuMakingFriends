package cn.graht.socializing.chatServer.handler;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.ChatServerConstant;
import cn.graht.common.enums.ChatTypeEnum;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.CreateGroupMessageDto;
import cn.graht.model.socializing.dtos.CreateMessageDto;
import cn.graht.model.socializing.pojos.GroupChatMember;
import cn.graht.model.socializing.pojos.PrivateChatSession;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.controller.v1.chat.GroupChatController;
import cn.graht.socializing.controller.v1.chat.PrivateChatController;
import cn.graht.socializing.service.GroupChatMemberService;
import cn.graht.socializing.utils.UserToolUtils;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RSetCache;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */
@Slf4j
@Sharable
@Component
@Scope("prototype")
public class ChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private Redisson redisson;

    private static final int SHARD_COUNT = 100;

    private final Map<String, ChannelHandlerContext> userChannels = new ConcurrentHashMap<>();

    @Resource
    private GroupChatMemberService groupChatMemberService;

    @Resource
    private GroupChatController groupChatController;
    @Resource
    private PrivateChatController privateChatController;

    @Resource
    private UserToolUtils userToolUtils;
    private static final AttributeKey<String> USER_ID_ATTR = AttributeKey.valueOf("userId");


    /**
     * 处理接收到的WebSocket消息。
     * 消息格式为：senderId:receiverId:content
     * 根据接收者是否在线，决定将消息直接发送给接收者或存储消息并发送离线通知。
     *
     * @param ctx 通道上下文
     * @param msg 接收到的WebSocket消息帧
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message = msg.text();
        if (StringUtils.startsWith(message,"online:")){
            String[] split = message.split(":");
            if (split.length != 3) return;
            String userId = split[1];
            String channelId = split[2];
            ctx.channel().attr(USER_ID_ATTR).set(userId);
            setOnlineUser(userId,ctx);
            log.info("User registry redis by channel");
            return;
        }
        log.info("Received WebSocket message: {}", message);
        String[] parts = message.split(":");
        if (parts.length != 4) {
            System.out.println("Invalid message format.");
            return;
        }
        // todo 后期把发送者统一改为 当前登录用户
        String senderId = parts[0];
        String type = parts[1];
        String sessionId = parts[2];
        String content = parts[3];
        // 续期发送者的过期时间 [当前登录用户的Id 续期]
        renewUserExpiry(senderId,ctx);
        if (ChatTypeEnum.PRIVATE.getCode().toString().equals(type)) {
            ResultApi<PrivateChatSession> privateSession = privateChatController.getSession(Integer.parseInt(sessionId));
            ThrowUtils.throwIf(Objects.isNull(privateSession) || Objects.isNull(privateSession.getData()), ErrorCode.SYSTEM_ERROR);
            PrivateChatSession p_session = privateSession.getData();
            if (senderId.equals(p_session.getUserId1())) {
                // 给userId2发消息
                CreateMessageDto createMessageDto = new CreateMessageDto();
                createMessageDto.setSessionId(Integer.parseInt(sessionId));
                createMessageDto.setSenderId(senderId);
                createMessageDto.setReceiverId(p_session.getUserId2());
                createMessageDto.setMessageType("text");
                createMessageDto.setContent(content);
                ResultApi<Integer> isAnd = privateChatController.createMessage(createMessageDto);
                ThrowUtils.throwIf(Objects.isNull(isAnd) || isAnd.getData() == -1, ErrorCode.SYSTEM_ERROR);
                if (getOnlineUserNodeId(senderId)) {
                    // 如果接收者在线，将消息发送到对应的节点
                    Map<String,String> param = new HashMap<>();
                    param.put("messageId",isAnd.getData().toString());
                    param.put("senderId",senderId);
                    UserVo userVo1 = userToolUtils.getUserFromCacheOrFeign(senderId);
                    param.put("senderAvatar",userVo1.getAvatarUrl());
                    param.put("message",content);
                    param.put("messageType","text");
                    sendMessageToNode(p_session.getUserId2(), param);
                }
            } else if (senderId.equals(p_session.getUserId2())) {
                // 给userId1发消息
                CreateMessageDto createMessageDto = new CreateMessageDto();
                createMessageDto.setSessionId(Integer.parseInt(sessionId));
                createMessageDto.setSenderId(senderId);
                createMessageDto.setReceiverId(p_session.getUserId1());
                createMessageDto.setMessageType("text");
                createMessageDto.setContent(content);
                ResultApi<Integer> isAnd = privateChatController.createMessage(createMessageDto);
                ThrowUtils.throwIf(Objects.isNull(isAnd) || isAnd.getData() == -1, ErrorCode.SYSTEM_ERROR);
                if (getOnlineUserNodeId(senderId)) {
                    // 如果接收者在线，将消息发送到对应的节点
                    Map<String,String> param = new HashMap<>();
                    param.put("messageId",isAnd.getData().toString());
                    param.put("senderId",senderId);
                    UserVo userVo1 = userToolUtils.getUserFromCacheOrFeign(senderId);
                    param.put("senderAvatar",userVo1.getAvatarUrl());
                    param.put("message",content);
                    param.put("messageType","text");
                    sendMessageToNode(p_session.getUserId1(), param);
                }
            } else {
                log.error("message send fail");
            }
        } else if (ChatTypeEnum.GROUP.getCode().toString().equals(type)) {
            // 写入消息到数据库
            CreateGroupMessageDto createGroupMessageDto = new CreateGroupMessageDto();
            createGroupMessageDto.setGroupId(Integer.parseInt(sessionId));
            createGroupMessageDto.setSenderId(senderId);
            createGroupMessageDto.setMessageType("text");
            createGroupMessageDto.setContent(content);
            ResultApi<Integer> message1 = groupChatController.createMessage(createGroupMessageDto);
            ThrowUtils.throwIf(Objects.isNull(message1) || message1.getData() == -1, ErrorCode.SYSTEM_ERROR);
            LambdaQueryWrapper<GroupChatMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GroupChatMember::getGroupId, sessionId);
            List<GroupChatMember> list = groupChatMemberService.list(queryWrapper);
            list.forEach(groupChatMember -> {
                if (!groupChatMember.getUserId().equals(senderId)) {
                    if (getOnlineUserNodeId(groupChatMember.getUserId())) {
                        UserVo userVo = userToolUtils.getUserFromCacheOrFeign(groupChatMember.getUserId());
                        String senderAvatar = userVo.getAvatarUrl();
                        Map<String,String> param = new HashMap<>();
                        param.put("messageId",message1.getData().toString());
                        param.put("senderId",senderId);
                        UserVo userVo1 = userToolUtils.getUserFromCacheOrFeign(senderId);
                        param.put("senderAvatar",userVo1.getAvatarUrl());
                        param.put("message",content);
                        param.put("messageType","text");
                        sendMessageToNode(groupChatMember.getUserId(), param);
                    }
                }
            });
        }
    }

    /**
     * 当通道激活时，生成一个唯一的用户ID，并将其设置为在线用户。
     *
     * @param ctx 通道上下文
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 获取 userId
        String userId = IdUtil.getSnowflakeNextIdStr();
        if (StringUtils.isNotBlank(userId)) {
            ctx.executor().schedule(() -> {
                Map<String,String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("id", "-1");
                stringStringHashMap.put("content", userId);
                ctx.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(stringStringHashMap)));
            },100,TimeUnit.MILLISECONDS);
        } else {
            ThrowUtils.throwIf(true, ErrorCode.NOT_LOGIN_ERROR);
        }
    }

    /**
     * 当通道不活跃时，从在线用户列表中移除该用户，并打印断开连接的日志。
     *
     * @param ctx 通道上下文
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = getUserIdFromContext(ctx); // 从上下文中获取用户ID
        if (userId != null) {
            removeOnlineUser(userId);
            System.out.println("User " + userId + " disconnected.");
        }
    }

    /**
     * 当通道发生异常时，打印异常堆栈并关闭通道。
     *
     * @param ctx   通道上下文
     * @param cause 异常对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 从通道上下文中获取用户ID。
     *
     * @param ctx 通道上下文
     * @return 用户ID
     */
    private String getUserIdFromContext(ChannelHandlerContext ctx) {
        // 从上下文中获取用户ID
        return ctx.channel().attr(USER_ID_ATTR).get();
    }

    /**
     * 将消息发送到对应的节点。
     * 可以使用消息队列（如RocketMQ、Kafka）或直接通过网络发送。
     *
     * @param receiverId 接收者的用户ID
     * @param param    消息内容
     */
    private void sendMessageToNode(String receiverId, Map<String,String> param) {
        Channel channel = getChannelByUserId(receiverId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(param)));
            log.info("发送消息成功");
        } else {
            System.out.println("Receiver " + receiverId + " is not active.");
        }
    }


    /**
     * 将用户设置为在线状态。
     * 使用Redisson的RSetCache来存储在线用户，并设置过期时间为30分钟。
     *
     * @param userId 用户ID
     */
    private void setOnlineUser(String userId, ChannelHandlerContext ctx) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> setCache = redisson.getSetCache(key);
        setCache.add(userId,30, TimeUnit.MINUTES); // 设置过期时间为30分钟
        userChannels.put(userId, ctx);
    }


    /**
     * 从在线用户列表中移除用户。
     *
     * @param userId 用户ID
     */
    private void removeOnlineUser(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> setCache = redisson.getSetCache(key);
        setCache.remove(userId);
        userChannels.remove(userId);
        log.info("User " + userId + " removed from online users.");
    }

    /**
     * 检查用户是否在线。
     *
     * @param userId 用户ID
     * @return 如果用户在线则返回true，否则返回false
     */
    private boolean getOnlineUserNodeId(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> setCache = redisson.getSetCache(key);
        return setCache.contains(userId) && userChannels.containsKey(userId);
    }

    /**
     * 根据用户ID计算分片索引。
     *
     * @param userId 用户ID
     * @return 分片索引
     */
    private int getShardIndex(String userId) {
        return Math.abs(userId.hashCode()) % SHARD_COUNT + 1;
    }

    /**
     * 续期用户的在线状态过期时间。
     * 使用Redisson的RSetCache来更新用户的过期时间为30分钟。
     *
     * @param userId 用户ID
     */
    private void renewUserExpiry(String userId,ChannelHandlerContext ctx) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> setCache = redisson.getSetCache(key);
        setCache.add(userId, 30, TimeUnit.MINUTES);
    }

    /**
     * 根据用户ID获取通道上下文。
     * 这里假设有一个全局的映射来存储用户的通道上下文。
     *
     * @param userId 用户ID
     * @return 用户的通道上下文
     */
    private Channel getChannelByUserId(String userId) {
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + getShardIndex(userId);
        RSetCache<String> setCache = redisson.getSetCache(key);
        return setCache.contains(userId) && userChannels.containsKey(userId) ? userChannels.get(userId).channel() : null;
    }
}
