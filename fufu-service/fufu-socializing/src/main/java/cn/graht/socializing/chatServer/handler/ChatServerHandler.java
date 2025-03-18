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
import cn.graht.socializing.controller.v1.chat.GroupChatController;
import cn.graht.socializing.controller.v1.chat.PrivateChatController;
import cn.graht.socializing.service.GroupChatMemberService;
import cn.hutool.core.util.IdUtil;
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
        String[] parts = message.split(":");
        if (parts.length != 4) {
            System.out.println("Invalid message format.");
            return;
        }
        //todo 后期把发送者统一改为 当前登录用户
        String senderId = parts[0];
        String type = parts[1];
        String sessionId = parts[2];
        String content = parts[3];
        // 续期发送者的过期时间 [当前登录用户的Id 续期]
        renewUserExpiry(senderId);
        if (ChatTypeEnum.PRIVATE.getCode().toString().equals(type)) {
            ResultApi<PrivateChatSession> privateSession = privateChatController.getSession(Integer.parseInt(sessionId));
            ThrowUtils.throwIf(Objects.isNull(privateSession) || Objects.isNull(privateSession.getData()), ErrorCode.SYSTEM_ERROR);
            PrivateChatSession p_session = privateSession.getData();
            if (senderId.equals(p_session.getUserId1())) {
                //给userId2发消息
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
                    sendMessageToNode(isAnd.getData(), p_session.getUserId2(), content);
                }
            } else if (senderId.equals(p_session.getUserId2())) {
                //给userId1发消息
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
                    sendMessageToNode(isAnd.getData(), p_session.getUserId1(), content);
                }
            } else {
                log.error("message send fail");
            }
        } else if (ChatTypeEnum.GROUP.getCode().toString().equals(type)) {
            //写入消息到数据库
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
                        sendMessageToNode(message1.getData(), groupChatMember.getUserId(), content);
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
//        String userId = (String) StpUtil.getLoginId();
        //随机生成一个uid
        String userId = IdUtil.getSnowflakeNextIdStr();
        log.info("User " + userId + " connected.");
        if (StringUtils.isNotBlank(userId)) {
            setOnlineUser(userId, ctx);
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
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
        return (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
    }

    /**
     * 将消息发送到对应的节点。
     * 可以使用消息队列（如RocketMQ、Kafka）或直接通过网络发送。
     *
     * @param receiverId 接收者的用户ID
     * @param content    消息内容
     */
    private void sendMessageToNode(Integer messageId, String receiverId, String content) {
        Channel channel = getChannelByUserId(receiverId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame("消息Id:" + messageId + "消息内容: " + content));
        } else {
            System.out.println("Receiver " + receiverId + " is not active.");
        }
    }

    /**
     * 将消息持久化到数据库。
     *
     * @param senderId   发送者的用户ID
     * @param receiverId 接收者的用户ID
     * @param content    消息内容
     */
    private void persistMessage(String senderId, String receiverId, String content) {
        String sql = String.format("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES ('%s', '%s', '%s', NOW())",
                senderId, receiverId, content);
        System.out.println("Persisted message: " + sql);
    }

    /**
     * 发送离线通知给接收者。
     *
     * @param receiverId 接收者的用户ID
     * @param content    消息内容
     */
    private void sendOfflineNotification(String receiverId, String content) {
        // 发送离线通知
        System.out.println("Sending offline notification to " + receiverId + ": " + content);
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
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.add(userId, 30, TimeUnit.MINUTES); // 设置过期时间为30分钟
        //将通道上下文存储到内存映射表中
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
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.remove(userId);

        //从内存映射表中移除对应的通道上下文
        userChannels.remove(userId);
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
        RSetCache<String> shardSet = redisson.getSetCache(key);
        return shardSet.contains(userId);
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
    private void renewUserExpiry(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.add(userId, 10, TimeUnit.MINUTES); // 续期过期时间为30分钟
    }

    /**
     * 根据用户ID获取通道上下文。
     * 这里假设有一个全局的映射来存储用户的通道上下文。
     *
     * @param userId 用户ID
     * @return 用户的通道上下文
     */
    private Channel getChannelByUserId(String userId) {
        return userChannels.get(userId) != null ? userChannels.get(userId).channel() : null;
    }
}
