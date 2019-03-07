package com.ericliu.chatbox.service.dto;

import java.util.Date;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/7
 */
public class Message {
    private String body;
    private MessageType messageType;
    private Date date;

    private User sender;
    private User receiver;

    public String getBody() {
        return body;
    }

    public Message setBody(String body) {
        this.body = body;
        return this;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Message setMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Message setDate(Date date) {
        this.date = date;
        return this;
    }

    public User getSender() {
        return sender;
    }

    public Message setSender(User sender) {
        this.sender = sender;
        return this;
    }

    public User getReceiver() {
        return receiver;
    }

    public Message setReceiver(User receiver) {
        this.receiver = receiver;
        return this;
    }
}
