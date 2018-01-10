package com.symphony.research.model.in;

import java.util.List;

public class ResearchArticle {

    private String title;
    private String link;
    private String authorEmail;
    private List<String> hashtags;
    private List<String> cashtags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

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
}
