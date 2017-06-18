package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 08/04/2017.
 */

public class TrainingItem {
    private String title;
    private boolean complete;
    private boolean finished;
    private boolean free;
    private String require;
    private int index;
    private String video = "";

    public TrainingItem() {
        this.title = "";
        this.require = "";
        this.index = 0;
    }

    public TrainingItem(String title) {
        this.title = title;
        this.require = "";
    }

    public TrainingItem(String title, boolean complete) {
        this.title = title;
        this.complete = complete;
        this.require = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
