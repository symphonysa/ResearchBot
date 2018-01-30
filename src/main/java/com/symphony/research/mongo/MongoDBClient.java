package com.symphony.research.mongo;

import com.mongodb.*;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.symphony.research.model.MessageEntities;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Updates.combine;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.symphony.research.model.mongo.ResearchInterest;
import com.symphony.research.model.mongo.ResearchSent;
import com.symphony.research.model.mongo.SectorUser;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.*;

public class MongoDBClient {
    private MongoCollection<ResearchSent> researchSentCollection;
    private MongoCollection<ResearchInterest> researchInterestCollection;
    private MongoCollection<SectorUser> sectorUserCollection;

    public MongoDBClient(String mongoURL) {
        MongoClientURI connectionString = new MongoClientURI(mongoURL);
        MongoClient mongoClient = new MongoClient(connectionString);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = mongoClient.getDatabase("ResearchBot");

        database = database.withCodecRegistry(pojoCodecRegistry);
        researchSentCollection = database.getCollection("ResearchSent", ResearchSent.class);
        researchInterestCollection = database.getCollection("ResearchInterest", ResearchInterest.class);
        sectorUserCollection = database.getCollection("Sector-User", SectorUser.class);

    }

    public List<ResearchInterest> getInterested(MessageEntities messageEntities){
         List<ResearchInterest> researchInterests = new ArrayList<>();
         Set<String> streamIds= new HashSet<>();
         researchInterestCollection.find(combine(in("entity", messageEntities.getHashtags()),eq("type","hashtag"))).forEach(new Block<ResearchInterest>() {
             @Override
             public void apply(final ResearchInterest researchInterest) {
                 if(!streamIds.contains(researchInterest.getStreamId())) {
                     researchInterests.add(researchInterest);
                     streamIds.add(researchInterest.getStreamId());
                 }
             }
         });
        researchInterestCollection.find(combine(in("entity", messageEntities.getCashtags()),eq("type","cashtag"))).forEach(new Block<ResearchInterest>() {
            @Override
            public void apply(final ResearchInterest researchInterest) {
                if(!streamIds.contains(researchInterest.getStreamId())) {
                    researchInterests.add(researchInterest);
                    streamIds.add(researchInterest.getStreamId());
                }
            }
        });
        researchInterestCollection.find(combine(in("entity", messageEntities.getUsers()),eq("type","user"))).forEach(new Block<ResearchInterest>() {
            @Override
            public void apply(final ResearchInterest researchInterest) {
                if(!streamIds.contains(researchInterest.getStreamId())) {
                    researchInterests.add(researchInterest);
                    streamIds.add(researchInterest.getStreamId());
                }
            }
        });
         return researchInterests;
    }

    public boolean registerInterest(MessageEntities messageEntities, String streamId, Long userSubmitted) {
        List<ResearchInterest> interests = new ArrayList<>();
        for (String cashtag: messageEntities.getCashtags()) {
            ResearchInterest interest = researchInterestCollection.find(combine(eq("streamId",streamId), eq("entity", cashtag),eq("type", "cashtag"))).first();
            if( interest == null) {
                ResearchInterest researchInterest = new ResearchInterest(streamId, cashtag, userSubmitted, "cashtag");
                interests.add(researchInterest);
            }
        }
        for (String hashtag: messageEntities.getHashtags()) {
            if(researchInterestCollection.find(combine(eq("streamId",streamId), eq("entity", hashtag),eq("type", "hashtag"))).first() == null) {

                ResearchInterest researchInterest = new ResearchInterest(streamId, hashtag, userSubmitted, "hashtag");
                interests.add(researchInterest);
            }
        }
        for (String user: messageEntities.getUsers()) {
            if(researchInterestCollection.find(combine(eq("streamId",streamId), eq("entity", user),eq("type", "user"))).first() == null) {

                ResearchInterest researchInterest = new ResearchInterest(streamId, user, userSubmitted, "user");
                interests.add(researchInterest);
            }
        }
        if(!interests.isEmpty()) {
            researchInterestCollection.insertMany(interests);
            return true;
        } else {
            return false;
        }
    }

    public void unfollowInterest(MessageEntities messageEntities, String streamId) {
        List<ResearchInterest> interests = new ArrayList<>();
        for (String cashtag: messageEntities.getCashtags()) {
           researchInterestCollection.findOneAndDelete(combine(eq("type","cashtag"),eq("entity", cashtag),eq("streamId",streamId)));
        }
        for (String hashtag: messageEntities.getHashtags()) {
            researchInterestCollection.findOneAndDelete(combine(eq("type","hashtag"),eq("entity", hashtag),eq("streamId",streamId)));
        }
        for (String user: messageEntities.getUsers()) {
            researchInterestCollection.findOneAndDelete(combine(eq("type","user"),eq("entity", user),eq("streamId",streamId)));
        }
    }

    public void researchSent(SymMessage messageSent, String senderEmail, String targetCompany, MessageEntities messageEntities) {
        List<ResearchSent> researchSentList = new ArrayList<>();
        if(messageEntities.getCashtags().size()==0 && messageEntities.getHashtags().size()==0){
            ResearchSent researchSent = new ResearchSent(targetCompany, messageSent.getStreamId(), messageSent.getId(), messageSent.getMessageText(), senderEmail, null);
            researchSentList.add(researchSent);
        }
        for (String keyword: messageEntities.getCashtags()) {
            ResearchSent researchSent = new ResearchSent(targetCompany, messageSent.getStreamId(), messageSent.getId(), messageSent.getMessageText(), senderEmail, keyword);
            researchSentList.add(researchSent);
        }
        for (String keyword: messageEntities.getHashtags()) {
            ResearchSent researchSent = new ResearchSent(targetCompany, messageSent.getStreamId(), messageSent.getId(), messageSent.getMessageText(), senderEmail, keyword);
            researchSentList.add(researchSent);
        }
        researchSentCollection.insertMany(researchSentList);
    }

    public List<ResearchInterest> getStreamInterests(String streamId){
        List<ResearchInterest> researchInterests = new ArrayList<>();
        researchInterestCollection.find(eq("streamId", streamId)).forEach(new Block<ResearchInterest>() {
            @Override
            public void apply(final ResearchInterest researchInterest) {
                researchInterests.add(researchInterest);
            }
        });
        return researchInterests;
    }

    public List<String> getSectorUsers(String sector) {
        List<String> users = new ArrayList<>();
        sectorUserCollection.find(eq("sector", sector)).forEach(new Block<SectorUser>() {
            @Override
            public void apply(final SectorUser sectorUser) {
                users.add(sectorUser.getUserEmail());
            }
        });
        return users;
    }

    public void newSectorUser(SectorUser sectorUser) {
        sectorUserCollection.insertOne(sectorUser);
    }

    public void newResearchInterest(ResearchInterest researchInterest) {
        researchInterestCollection.insertOne(researchInterest);
    }
}
