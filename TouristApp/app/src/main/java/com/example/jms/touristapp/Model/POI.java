package com.example.jms.touristapp.Model;

import java.io.Serializable;

/**
 * Created by JMS on 29/03/2016.
 */
public class POI implements Serializable {

    private int id;
    private String name;
    private String intro;
    private String description;
    private String image;
    private String link;
    private double latitude;
    private double longitude;
    private String address;
    private String phone;
    private String email;
    private boolean favorite;



    public POI(int id, String name, String intro, String description, String image, String link,
               double latitude, double longitude, String address, String phone, String email) {
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.description = description;
        this.image = image;
        this.link = link;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phone = phone;
        this.email = email;
        favorite = false;
    }

    public POI(int id, String name, String intro, String description, String image, String link,
               double latitude, double longitude, String address, String phone, String email,boolean fav){
        this(id,name,intro,description,image,link,latitude,longitude,address,phone,email);
        favorite = fav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
