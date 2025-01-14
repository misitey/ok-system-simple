package cn.iosd.starter.socket.handler;

import cn.iosd.starter.socket.constant.SocketConstants;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 消息事件处理器
 *
 * @author ok1996
 */
@Slf4j
@Component
public class MessageEventHandler {


    /**
     * 添加connect事件，当客户端发起连接时调用
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {
            String sessionId = client.getSessionId().toString();
            String room = client.getHandshakeData().getSingleUrlParam("room");
            joinRoom(client, room, null);
            //可选指定连接微服务名称
            String serviceName = client.getHandshakeData().getSingleUrlParam(SocketConstants.CONNECT_APPLICATION_NAME);
            joinRoom(client, serviceName, SocketConstants.CONNECT_APPLICATION_NAME_ROOM_PREFIX);
            log.info("socket连接成功, sessionId={}", sessionId);
        } else {
            log.error("客户端建立连接失败: SocketIOClient为空");
        }
    }

    /**
     * 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        client.disconnect();
        String sessionId = client.getSessionId().toString();
        log.warn("客户端断开连接, sessionId={}", sessionId);
    }

    /**
     * 连接验证
     *
     * @param client
     * @param ackRequest
     * @param message
     */
    @OnEvent(value = "Hello")
    public void onHelloEvent(SocketIOClient client, AckRequest ackRequest, String message) {
        log.info("hello事件, sessionId={}, message={}", client.getSessionId().toString(), message);
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("您好, netty连接已建立.");
        }
        client.sendEvent("Hello", "您好, Message:" + message);
    }

    /**
     * 将room加入client
     *
     * @param client
     * @param room   多个使用逗号分割
     */
    private void joinRoom(SocketIOClient client, String room, String prefix) {
        if (StringUtils.isNotBlank(room)) {
            String[] rooms = room.split(",");
            for (int i = 0; i < rooms.length; i++) {
                if (!client.getAllRooms().contains(rooms[i])) {
                    if (StringUtils.isEmpty(prefix)) {
                        prefix = "";
                    }
                    log.info("client join room:{}{}", prefix, rooms[i]);
                    client.joinRoom(prefix + rooms[i]);
                }
            }
        }
    }
}
