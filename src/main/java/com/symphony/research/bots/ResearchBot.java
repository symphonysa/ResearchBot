package com.symphony.research.bots;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.symphony.research.SymphonyTestConfiguration;
import com.symphony.research.model.MessageEntities;
import com.symphony.research.model.ResearchInterest;
import com.symphony.research.mongo.MongoDBClient;
import com.symphony.research.utils.MessageParser;
import io.swagger.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.*;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

public class ResearchBot implements ChatListener, ChatServiceListener, RoomServiceEventListener, RoomEventListener {

    private static ResearchBot instance;
    private final Logger logger = LoggerFactory.getLogger(ResearchBot.class);
    private SymphonyClient symClient;
    private RoomService roomService;
    SymphonyTestConfiguration config;
    private MongoDBClient mongoDBClient;
    private MessageParser messageParser;

    protected ResearchBot(SymphonyClient symClient, SymphonyTestConfiguration config) {
        this.symClient=symClient;
        this.config = config;
        init();


    }

    public static ResearchBot getInstance(SymphonyClient symClient, SymphonyTestConfiguration config){
        if(instance==null){
            instance = new ResearchBot(symClient,config);
        }
        return instance;
    }

    private void init() {

        logger.info("Connections example starting...");

        symClient.getChatService().addListener(this);
        roomService = symClient.getRoomService();
        roomService.addRoomServiceEventListener(this);

        mongoDBClient = new MongoDBClient(config.getMongoURL());
        //Init connection service.
        ConnectionsService connectionsService = new ConnectionsService(symClient);

        //Optional to auto accept connections.
        connectionsService.setAutoAccept(true);

        this.messageParser = new MessageParser();

    }


    @Override
    public void onChatMessage(SymMessage message) {
            processNewMessage(message);
        }



    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStreamId(), chat.getRemoteUsers());
    }

    @Override
    public void onRemovedChat(Chat chat) {

    }


    @Override
    public void onMessage(SymMessage symMessage) {
        processNewMessage(symMessage);
    }

    @Override
    public void onNewRoom(Room room) {
    }

    @Override
    public void onRoomMessage(SymMessage symMessage) {

    }

    @Override
    public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {

    }

    @Override
    public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {

    }

    @Override
    public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {

    }

    @Override
    public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {

    }

    @Override
    public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {

    }

    @Override
    public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {
    }

    @Override
    public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {

    }

    @Override
    public void onSymRoomCreated(SymRoomCreated symRoomCreated) {

    }

    private void processNewMessage (SymMessage message) {
        if (message == null)
            return;
        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());
        boolean isExternal = false;
        try {
            isExternal = symClient.getStreamsClient().getStreamAttributes(message.getStreamId()).getCrossPod();
        } catch (StreamsException e) {
            e.printStackTrace();
        }

        if (message.getMessage().toLowerCase().contains("newresearch") | message.getMessage().toLowerCase().contains("follow") | message.getMessage().toLowerCase().contains("help")) {

            MessageEntities messageEntities = messageParser.getMessageEntities(message.getEntityData());

            if (message.getMessage().toLowerCase().contains("newresearch") & !isExternal) {
                messageEntities.getUsers().clear();
                messageEntities.getUsers().add(message.getSymUser().getId().toString());
                messageEntities.getHashtags().remove("newresearch");
                List<ResearchInterest> researchInterests = mongoDBClient.getInterested(messageEntities);
                String presentationML = message.getMessage();
                int endoftag = presentationML.indexOf(">");
                String start = presentationML.substring(0, endoftag + 1);
                String resultmessage = start.concat("<br/><br/> Research from <span class=\"entity\" data-entity-id=\"mentionAdded\">@" + message.getSymUser().getDisplayName() + "</span>: <br/> ");
                String end = presentationML.substring(endoftag + 1, presentationML.length());
                resultmessage = resultmessage.concat(end);
                String data = message.getEntityData();
                JsonParser jsonParser = new JsonParser();
                JsonElement json = jsonParser.parse(data);
                JsonObject object = json.getAsJsonObject();
                JsonObject mentionValue = jsonParser.parse("{\"type\":\"com.symphony.user.mention\",\"version\":\"1.0\",\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"" + message.getSymUser().getId() + "\"}]}").getAsJsonObject();
                object.add("mentionAdded", mentionValue);
                String resultdata = object.toString();


                SymMessage researchmessage = new SymMessage();
                researchmessage.setMessage(resultmessage);
                researchmessage.setEntityData(resultdata);
                List<SymAttachmentInfo> attachments= new ArrayList<>();
                try {
                for (SymAttachmentInfo attachment: message.getAttachments()) {
                    byte[] attachmentData = symClient.getAttachmentsClient().getAttachmentData(attachment,message);
                    String basename = FilenameUtils.getBaseName(attachment.getName());
                    String extension = "."+FilenameUtils.getExtension(attachment.getName());
                    File tempFile = File.createTempFile(basename,extension);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(attachmentData);
                    researchmessage.setAttachment(tempFile);
                    //attachments.add(symClient.getAttachmentsClient().postAttachment(attachment.getName(),tempFile));
                }
                //researchmessage.setAttachments(attachments);
                for (ResearchInterest interest : researchInterests) {
                    Stream stream = new Stream();
                    stream.setId(interest.getStreamId());


                        SymMessage messageSent = symClient.getMessagesClient().sendMessage(stream, researchmessage);
                        String targetCompany = symClient.getUsersClient().getUserFromId(interest.getUser()).getCompany();
                        mongoDBClient.researchSent(messageSent,message.getSymUser().getEmailAddress(),targetCompany, messageEntities);

                }
                } catch (MessagesException e) {
                    e.printStackTrace();
                } catch (UsersClientException e) {
                    e.printStackTrace();
                } catch (AttachmentsException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (message.getMessageText().toLowerCase().contains("#follow") & isExternal) {
                messageEntities.getHashtags().remove("follow");
                
                boolean done = mongoDBClient.registerInterest(messageEntities, message.getStreamId(), message.getSymUser().getId());
                if(done) {
                    Stream stream = new Stream();
                    stream.setId(message.getStreamId());
                    SymMessage followmessage = new SymMessage();
                    followmessage.setMessage("<messageML>Interests registered</messageML>");
                    try {
                        symClient.getMessagesClient().sendMessage(stream, followmessage);
                    } catch (MessagesException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Stream stream = new Stream();
                    stream.setId(message.getStreamId());
                    SymMessage followmessage = new SymMessage();
                    followmessage.setMessage("<messageML>Nothing new to follow</messageML>");
                    try {
                        symClient.getMessagesClient().sendMessage(stream, followmessage);
                    } catch (MessagesException e) {
                        e.printStackTrace();
                    }
                }

            } else if (message.getMessageText().toLowerCase().contains("#unfollow") & isExternal) {
                messageEntities.getHashtags().remove("unfollow");
                mongoDBClient.unfollowInterest(messageEntities, message.getStreamId());

                Stream stream = new Stream();
                stream.setId(message.getStreamId());
                SymMessage unfollowmessage = new SymMessage();
                unfollowmessage.setMessage("<messageML>Un-follow successful</messageML>");
                try {
                    symClient.getMessagesClient().sendMessage(stream, unfollowmessage);
                } catch (MessagesException e) {
                    e.printStackTrace();
                }

            } else if (message.getMessageText().toLowerCase().contains("#help") & isExternal) {
                messageEntities.getHashtags().remove("help");
                List <ResearchInterest> researchInterests = mongoDBClient.getStreamInterests(message.getStreamId());

                Stream stream = new Stream();
                stream.setId(message.getStreamId());
                SymMessage unfollowmessage = new SymMessage();
                String messageContent= "<messageML><br/><br/>";
                if(!researchInterests.isEmpty()){
                    messageContent = messageContent.concat("You are following:<br/><ul>");
                    for (ResearchInterest interest : researchInterests) {
                        if (interest.getType().equals("cashtag")) {
                            messageContent = messageContent.concat("<li><cash tag=\"" + interest.getEntity() + "\"/></li>");
                        } else if (interest.getType().equals("hashtag")) {
                            messageContent = messageContent.concat("<li><hash tag=\"" + interest.getEntity() + "\"/></li>");
                        } else if (interest.getType().equals("user")) {
                            messageContent = messageContent.concat("<li><mention uid=\"" + interest.getEntity() + "\"/></li>");
                        }
                    }
                    messageContent = messageContent.concat("</ul><br/>");
                }
                messageContent = messageContent.concat("To manage your followed keywords and authors use <hash tag=\"follow\"/> and <hash tag=\"unfollow\"/> followed by any keywords or authors that you want to follow/un-follow</messageML>");
                unfollowmessage.setMessage(messageContent);
                try {
                    symClient.getMessagesClient().sendMessage(stream, unfollowmessage);
                } catch (MessagesException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
