package cn.graht.test.client;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
@Slf4j
public class ChatClient2 {

    private Session userSession = null;

    public ChatClient2(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Connected to server");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    public void sendMessage(String message) {
        if (userSession != null && userSession.isOpen()) {
            userSession.getAsyncRemote().sendText(message);
        } else {
            System.out.println("Session is not open");
        }
    }

    public static void main(String[] args) {
        URI uri = URI.create("ws://localhost:29999/ws");
        ChatClient2 client = new ChatClient2(uri);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter message (type:receiverId:content) 群聊，则type为2，如果是私聊，则type为1 ,群聊 接收方为会话id前面要加# : ");
            String input = scanner.nextLine();
            System.out.println();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            client.sendMessage(input);
        }
        scanner.close();
    }
}
