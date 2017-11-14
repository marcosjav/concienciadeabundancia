package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 05/11/2017.
 */

public class SentUser {
    private String key;
    private boolean checked;

    public SentUser() {
    }

    public SentUser(String key, boolean checked) {
        this.key = key;
        this.checked = checked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
