package person.pluto.websocket.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import person.pluto.websocket.common.WebsocketFormat;

/**
 * <p>
 * 发送消息
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 14:41:56
 */
@Service
public class MessageTemplateServer {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 向指定用户发送消息
     *
     * @param userId
     * @param message
     * @author Pluto
     * @since 2020-03-09 15:33:39
     */
    public void convertAndSendToUser(String userId, String message) {
        simpMessagingTemplate.convertAndSendToUser(userId, WebsocketFormat.USER_RECEIVER_PATH, message);
    }

}
