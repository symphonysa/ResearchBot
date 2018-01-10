package com.symphony.research.model;

import org.bson.types.ObjectId;

public class SectorUser {

    private ObjectId id;
    private String sector;
    private String userEmail;

    public SectorUser() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
