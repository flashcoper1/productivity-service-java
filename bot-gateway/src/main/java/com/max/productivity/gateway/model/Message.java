package com.max.productivity.gateway.model;

/**
 * Модель сообщения от пользователя бота.
 */
public class Message {

    private User from;
    private MessageBody body;

    public Message() {
    }

    public Message(User from, MessageBody body) {
        this.from = from;
        this.body = body;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }
}

