package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 16/07/2017.
 */

public class MessageItem {
    String title, message;
    long time;

    public MessageItem() {
    }

    public MessageItem(String title, String message, long time) {
        this.title = title;
        this.message = message;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
