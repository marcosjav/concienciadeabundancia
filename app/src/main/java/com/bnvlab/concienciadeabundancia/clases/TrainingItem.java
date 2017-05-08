package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 08/04/2017.
 */

public class TrainingItem {
    private String title;
    private boolean complete;
    private boolean finished;

    public TrainingItem() {
    }

    public TrainingItem(String title) {
        this.title = title;
    }

    public TrainingItem(String title, boolean complete) {
        this.title = title;
        this.complete = complete;
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
}
