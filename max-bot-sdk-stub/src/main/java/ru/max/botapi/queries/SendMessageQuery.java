package ru.max.botapi.queries;

import ru.max.botapi.model.NewMessageBody;

/**
 * Заглушка для SendMessageQuery из Max Bot SDK.
 */
public class SendMessageQuery {

    private Long chatId;
    private NewMessageBody body;

    public SendMessageQuery(Long chatId, NewMessageBody body) {
        this.chatId = chatId;
        this.body = body;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public NewMessageBody getBody() {
        return body;
    }

    public void setBody(NewMessageBody body) {
        this.body = body;
    }
}

