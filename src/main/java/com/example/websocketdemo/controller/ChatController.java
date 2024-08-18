package com.example.websocketdemo.controller;

import com.example.websocketdemo.aspect.annotaion.AttachContext;
import com.example.websocketdemo.model.MessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate webSocketMessagingTemplate;

    @MessageMapping("/send.message")
    @AttachContext
    public void sendMessage(@Payload MessageRequest messageRequest,
                            SimpMessageHeaderAccessor headerAccessor) {
        log.info("message, thread: {}", Thread.currentThread().getName());
        String chatRoomId = headerAccessor.getSessionAttributes().get("chatroomId").toString();
        // can also fetch user with headerAccessor.getUser().getPrincipal().getUsername()
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        messageRequest.setFromUser(userId);
        messageRequest.setChatroomId(chatRoomId);

        if(messageRequest.getMessage().startsWith("private")){
            String toUserID = messageRequest.getMessage().split(" ")[0].substring("private".length());
            String message = messageRequest.getMessage().split(" ")[1];
            messageRequest.setMessage(message);
            webSocketMessagingTemplate.convertAndSendToUser(
                    toUserID,
                    "/queue/" + chatRoomId + ".private.messages",
                    messageRequest);

        } else {
//            throw new RuntimeException("Received a message from the topic");

            webSocketMessagingTemplate.convertAndSend(
                    "/topic/" + chatRoomId, //+ ".public.messages",
                    messageRequest);
        }
    }
}
