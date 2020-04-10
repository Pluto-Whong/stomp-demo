package person.pluto.interactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import person.pluto.websocket.server.MessageTemplateServer;

@RestController
public class TestController {

    @Data
    static class TextModel {
        private String toUserId;
        private String textContent;
    }

    @Autowired
    @Lazy
    private MessageTemplateServer messageTemplateServer;

    @MessageMapping("/send/hello")
    public void hello(TextModel model) {
        messageTemplateServer.convertAndSendToUser(model.getToUserId(), model.getTextContent());
    }

}
