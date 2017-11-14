package com.bnvlab.concienciadeabundancia.clases;

import android.support.annotation.NonNull;

/**
 * Created by Marcos on 07/11/2017.
 */

public class ChatMessageItem implements Comparable<ChatMessageItem>{
    private boolean answer;
    private long time;
    private String text;

    public ChatMessageItem() {
    }

    public ChatMessageItem(boolean answer, long time, String text) {
        this.answer = answer;
        this.time = time;
        this.text = text;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(@NonNull ChatMessageItem o) {
        if (time < o.time)
            return 1;
        else if (time > o.time)
            return -1;

        return 0;
    }
}
