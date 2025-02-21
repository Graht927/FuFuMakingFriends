package cn.graht.socializing.chatServer.handler;

import cn.graht.common.constant.ChatServerConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.redisson.Redisson;
import org.redisson.api.RSetCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Resource
    private Redisson redisson;

    private static final int SHARD_COUNT = 100;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] parts = msg.split(":");
        if (parts.length != 3) {
            System.out.println("Invalid message format.");
            return;
        }

        String senderId = parts[0];
        String receiverId = parts[1];
        String content = parts[2];
        // 续期发送者的过期时间
        renewUserExpiry(senderId);

        // 检查接收者是否在线
        if (getOnlineUserNodeId(receiverId)) {
            // 如果接收者在线，将消息发送到对应的节点
            sendMessageToNode(senderId, content);
        } else {
            // 如果接收者离线，存储消息并发送通知
            persistMessage(senderId, receiverId, content);
            sendOfflineNotification(receiverId, content);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String userId = generateUserId();
        setOnlineUser(userId);
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = getUserIdFromContext(ctx); // 从上下文中获取用户ID
        if (userId != null) {
            removeOnlineUser(userId);
            System.out.println("User " + userId + " disconnected.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private String generateUserId() {
        return "User-" + System.nanoTime();
    }


    private String getUserIdFromContext(ChannelHandlerContext ctx) {
        // 从上下文中获取用户ID
        return (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
    }

    private void sendMessageToNode(String senderId, String content) {
        // 将消息发送到对应的节点
        // 可以使用消息队列（如RocketMQ、Kafka）或直接通过网络发送
        System.out.println("Sending message to user : [User " + senderId + "] " + content);
    }

    private void persistMessage(String senderId, String receiverId, String content) {
        String sql = String.format("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES ('%s', '%s', '%s', NOW())",
                senderId, receiverId, content);
        System.out.println("Persisted message: " + sql);
    }

    private void sendOfflineNotification(String receiverId, String content) {
        // 发送离线通知
        System.out.println("Sending offline notification to " + receiverId + ": " + content);
    }

    private void setOnlineUser(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.add(userId, 30, TimeUnit.MINUTES); // 设置过期时间为30分钟
    }

    private void removeOnlineUser(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.remove(userId);
    }

    private boolean getOnlineUserNodeId(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> shardSet = redisson.getSetCache(key);
        return shardSet.contains(userId);
    }

    private int getShardIndex(String userId) {
        return Math.abs(userId.hashCode()) % SHARD_COUNT;
    }

    private void renewUserExpiry(String userId) {
        int shardIndex = getShardIndex(userId);
        String key = ChatServerConstant.ONLINE_USERS_PREFIX + shardIndex;
        RSetCache<String> shardSet = redisson.getSetCache(key);
        shardSet.add(userId, 30, TimeUnit.MINUTES);// 续期过期时间为30分钟



    }


}
