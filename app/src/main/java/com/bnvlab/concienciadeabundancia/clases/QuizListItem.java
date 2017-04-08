package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 08/04/2017.
 */

public class QuizListItem {
    private String title;
    private boolean sent;

    public QuizListItem(String title, boolean sent) {
        this.title = title;
        this.sent = sent;
    }

    public QuizListItem(String title) {

        this.title = title;
        this.sent = false;
    }

    public QuizListItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
