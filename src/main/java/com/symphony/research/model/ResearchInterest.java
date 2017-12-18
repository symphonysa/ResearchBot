package com.symphony.research.model;

import org.bson.types.ObjectId;

public class ResearchInterest {

    private ObjectId id;
    String streamId;
    String entity;
    Long user;
    String type;

    public ResearchInterest() {
    }

    public ResearchInterest(String streamId, String entity, Long user, String type) {
        this.streamId = streamId;
        this.entity = entity;
        this.user = user;
        this.type = type;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
