package com.bnvlab.concienciadeabundancia.clases;

/**
 * Created by Marcos on 23/03/2017.
 */

public class User {
    public static String CHILD = "users";
    String uId, name, lastName, locale, phone, email;

    public User() {
    }

    public User(String name, String lastName, String locale, String phone, String email, String uId) {

        this.uId = uId;
        this.name = name;
        this.lastName = lastName;
        this.locale = locale;
        this.phone = phone;
        this.email = email;
    }
    public User(String name, String lastName, String locale, String phone, String email) {

        this.name = name;
        this.lastName = lastName;
        this.locale = locale;
        this.phone = phone;
        this.email = email;
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


}
