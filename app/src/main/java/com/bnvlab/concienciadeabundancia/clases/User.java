package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 23/03/2017.
 */

public class User {
    public static String CHILD = "users";
    String uId, name, secondName, lastName, locale, phone, email, lastNumber, invitationUID, deviceId;
    boolean verified, signInWithEmail, active;

    public User() {
    }

    public User(String name,String secondName, String lastName, String locale, String phone, String email, String invitationUID) {

        this.invitationUID = invitationUID;
        this.name = name;
        this.secondName = secondName;
        this.lastName = lastName;
        this.locale = locale;
        this.phone = phone;
        this.email = email;
        this.verified = false;
        this.signInWithEmail = true;
        this.active = false;
    }
    public User(String name, String lastName, String locale, String phone, String email) {

        this.name = name;
        this.lastName = lastName;
        this.locale = locale;
        this.phone = phone;
        this.email = email;
        this.verified = false;
        this.signInWithEmail = true;
        this.active = false;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(String lastNumber) {
        this.lastNumber = lastNumber;
    }

    public boolean isSignInWithEmail() {
        return signInWithEmail;
    }

    public void setSignInWithEmail(boolean signInWithEmail) {
        this.signInWithEmail = signInWithEmail;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getInvitationUID() {
        return invitationUID;
    }

    public void setInvitationUID(String invitationUID) {
        this.invitationUID = invitationUID;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
