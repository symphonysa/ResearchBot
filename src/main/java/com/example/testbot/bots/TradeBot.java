package com.example.testbot.bots;

import com.example.testbot.SymphonyTestConfiguration;
import com.google.api.core.ApiFuture;
import com.google.cloud.Service;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.*;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class TradeBot implements ChatListener, ChatServiceListener, RoomServiceEventListener, RoomEventListener {

    private static TradeBot instance;
    private final Logger logger = LoggerFactory.getLogger(TradeBot.class);
    private SymphonyClient symClient;
    private RoomService roomService;
    SymphonyTestConfiguration config;


    protected TradeBot(SymphonyClient symClient, SymphonyTestConfiguration config) {
        this.symClient=symClient;
        this.config = config;
        init();


    }

    public static TradeBot getInstance(SymphonyClient symClient, SymphonyTestConfiguration config){
        if(instance==null){
            instance = new TradeBot(symClient,config);
        }
        return instance;
    }

    private void init() {

        logger.info("Connections example starting...");

        symClient.getChatService().addListener(this);
        roomService = symClient.getRoomService();
        roomService.addRoomServiceEventListener(this);

    }


    //Chat sessions callback method.
    @Override
    public void onChatMessage(SymMessage message) {
        if (message == null)
            return;
        String email = message.getSymUser().getEmailAddress();
        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());
        SymMessage message2;

        if (message.getMessage().contains("alert")) {


            message2 = new SymMessage();

            message2.setEntityData("{\"summary\": { \"type\": \"com.symphony.fa\", \"version\":  \"1.0\", \"userEmail\":  \""+email+"\" }}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><b><i>Please install the FA application to render this entity.</i></b></div></messageML>");
            try {
                symClient.getMessagesClient().sendMessage(message.getStream(), message2);
            } catch (MessagesException e) {
                logger.error("Failed to send message", e);
            }

        }



    }



    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getStreamId(), chat.getRemoteUsers());
    }

    @Override
    public void onRemovedChat(Chat chat) {

    }


    @Override
    public void onMessage(SymMessage symMessage) {
        logger.info("Message detected from stream: {} from: {} message: {}",
                symMessage.getStreamId(),
                symMessage.getFromUserId(),
                symMessage.getMessage());

        if (symMessage.getMessage().contains("portfolio")) {
            symMessage.setMessage("<messageML>No pending actions by agent, all mandates under threshold.</messageML>");
            Stream stream = new Stream();
            stream.setId(symMessage.getStreamId());

            try {
                symClient.getMessagesClient().sendMessage(stream, symMessage);
            } catch (MessagesException e) {
                logger.error("Failed to send message", e);
            }
        }
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

    public void sendMandateAlertMsg(String email){
        //Creates a Chat session with that will receive the online message.
        Chat chat = new Chat();
        chat.setLocalUser(symClient.getLocalUser());
        Set<SymUser> remoteUsers = new HashSet<>();
        try {
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(email));
            chat.setRemoteUsers(remoteUsers);
            chat.addListener(this);
            chat.setStream(symClient.getStreamsClient().getStream(remoteUsers));

            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);

            SymMessage message2 = new SymMessage();

            message2.setEntityData("{\"summary\": { \"type\": \"com.symphony.fa\", \"version\":  \"1.0\", \"userEmail\":  \""+email+"\" }}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><b><i>Please install the FA application to render this entity.</i></b></div></messageML>");


            symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(remoteUsers), message2);
        } catch (MessagesException e) {
            logger.error("Failed to send message", e);
        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (UsersClientException e) {
            e.printStackTrace();
        }

    }

    public void sendTradeConfirmation(String streamId, String action, String symbol, int numShares, int price){
        Stream stream = new Stream();
        stream.setId(streamId);
        SymMessage message2 = new SymMessage();
        message2.setMessage("<messageML><card accent='tempo-text-color--green'>Trade initiated<br/><hr /><b>"+action+" "+numShares+" <cash tag='"+symbol+"'/> @ "+price+"</b></card></messageML>");

        try {
            symClient.getMessagesClient().sendMessage(stream, message2);
        } catch (MessagesException e) {
            logger.error("Failed to send message", e);
        }
    }

}