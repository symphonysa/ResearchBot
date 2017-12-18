package com.symphony.research.model;

import org.bson.types.ObjectId;

import java.util.Date;

public class ResearchSent {

    ObjectId id;
    String targetCompany;
    String targetStreamId;
    String messageId;
    String messageContent;
    Date dateSent;
    String senderEmail;
    String keyword;

    public ResearchSent() {
    }

    public ResearchSent(String targetCompany, String targetStreamId, String messageId, String messageContent, String senderEmail, String keyword) {
        this.targetCompany = targetCompany;
        this.targetStreamId = targetStreamId;
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.senderEmail = senderEmail;
        this.keyword = keyword;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public String getTargetStreamId() {
        return targetStreamId;
    }

    public void setTargetStreamId(String targetStreamId) {
        this.targetStreamId = targetStreamId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
