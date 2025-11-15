package ru.max.botapi.model;

/**
 * Заглушка для NewMessageBody из Max Bot SDK.
 */
public class NewMessageBody {

    private String text;

    public NewMessageBody() {
    }

    public NewMessageBody(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

