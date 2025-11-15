package ru.max.botapi.model;

/**
 * Заглушка для Message из Max Bot SDK.
 */
public class Message {

    private Long chatId;
    private Long senderId;
    private String text;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

