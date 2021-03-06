package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 24/03/2017.
 */

public class ConferenceItem {
    String date, duration, gps, info, location, place, title, image;
    boolean freeContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ConferenceItem() {
        date = "";
        duration = "";
        gps = "";
        info = "";
        location = "";
        place = "";
        title = "";
        image = "";
    }

    public ConferenceItem(String date, String duration, String gps, String info, String location, String place, String title, boolean freeContent) {
        this.date = date;
        this.duration = duration;
        this.gps = gps;
        this.info = info;
        this.location = location;
        this.place = place;
        this.title = title;
        this.freeContent = freeContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isFreeContent() {
        return freeContent;
    }

    public void setFreeContent(boolean freeContent) {
        this.freeContent = freeContent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
