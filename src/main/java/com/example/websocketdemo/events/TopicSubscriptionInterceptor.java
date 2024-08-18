package com.example.websocketdemo.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@Slf4j
public class TopicSubscriptionInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.SEND.equals(accessor.getCommand())){
            log.info("message pre thread, {}: {}", message.hashCode(), Thread.currentThread().getName());
        } else {
            log.info("pre thread, {}: {}", message.hashCode(), Thread.currentThread().getName());
        }
        // fetch userId using accessor.getUser().getPrincipal().getUsername()
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Example check: Deny access to /topic/restricted/** for all users
//            if (destination != null && destination.startsWith("/topic")) {
//                return null;
//            }

            // Additional custom security logic can be implemented here
        }

        return message;

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // Clear context after message processing
//        MessageContextHolder.clearContext();
        log.info("after thread, {}: {}", message.hashCode(), Thread.currentThread().getName());
    }
}
