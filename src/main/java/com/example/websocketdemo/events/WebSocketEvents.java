package com.example.websocketdemo.events;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEvents {

	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		// can fetch userId with event.getUser().getPrincipal().getUsername()
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String chatRoomId = headers.getNativeHeader("chatroomId").get(0);
		String userId = headers.getNativeHeader("userId").get(0);
		headers.getSessionAttributes().put("chatroomId", chatRoomId);
		headers.getSessionAttributes().put("userId", userId);
	}

	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
	}

	@EventListener
	public void handleSubscription(SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String destination = headers.getDestination();
		if(destination.startsWith("/topic/")){
			throw new RuntimeException("cant sub to /topic/");
		}
	}

	@EventListener
	public void handleUnsubscription(SessionUnsubscribeEvent event) {
	}
}
