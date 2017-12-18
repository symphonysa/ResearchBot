package com.symphony.research.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.symphony.research.model.MessageEntities;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    public List<String> getCashtags(String message){
        List<String> cashtags = new ArrayList<>();
        return cashtags;
    }

    public List<String> getHashtags(String message){
        List<String> hashtags = new ArrayList<>();
        return hashtags;
    }

    public List<String> getUsers(String message){
        List<String> users = new ArrayList<>();
        return users;
    }
    public List<String> getEntities(String message){
        List<String> entities = new ArrayList<>();
        entities.addAll(getCashtags(message));
        entities.addAll(getHashtags(message));
        entities.addAll(getUsers(message));
        return entities;
    }

    public MessageEntities getMessageEntities(String entityData) {
        JsonParser jsonParser = new JsonParser();
        JsonElement json = jsonParser.parse(entityData);
        JsonObject object = json.getAsJsonObject();
        MessageEntities messageEntities = new MessageEntities();
        List<String> cashtags = new ArrayList<>();
        List<String> hashtags = new ArrayList<>();
        List<String> users = new ArrayList<>();
        for (String key:object.keySet()) {
            JsonElement member = object.get(key);
            JsonObject memberObject = member.getAsJsonObject();
            JsonObject idObject = memberObject.get("id").getAsJsonArray().get(0).getAsJsonObject();
            String value = idObject.get("value").getAsString();
            String type = idObject.get("type").getAsString();
            if(type.contains("ticker")){
                cashtags.add(value.toLowerCase());
            }
            else if(type.contains("hashtag")){
                hashtags.add(value.toLowerCase());
            } else if(type.contains("userId")){
                users.add(value);
            }

        }

        messageEntities.setCashtags(cashtags);
        messageEntities.setHashtags(hashtags);
        messageEntities.setUsers(users);

        return messageEntities;

    }
}
