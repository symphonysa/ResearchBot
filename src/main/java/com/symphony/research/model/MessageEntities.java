package com.symphony.research.model;

import java.util.List;

public class MessageEntities {

    List<String> hashtags;
    List<String> cashtags;
    List<String> users;

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getCashtags() {
        return cashtags;
    }

    public void setCashtags(List<String> cashtags) {
        this.cashtags = cashtags;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
