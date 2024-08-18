package com.example.websocketdemo.model;

import lombok.Data;

@Data
public class MessageRequest {
    private String message;
    private String fromUser;
    private String chatroomId;
}
