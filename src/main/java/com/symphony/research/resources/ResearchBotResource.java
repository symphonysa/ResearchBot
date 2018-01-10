package com.symphony.research.resources;

import com.symphony.research.SymphonyTestConfiguration;
import com.symphony.research.bots.ResearchBot;
import com.symphony.research.model.in.ResearchArticle;
import com.symphony.research.mongo.MongoDBClient;
import com.symphony.research.utils.SymphonyAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UsersClientException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/research")
public class ResearchBotResource {

    private SymphonyTestConfiguration config;
    private SymphonyClient symClient;
    private final Logger LOG = LoggerFactory.getLogger(ResearchBotResource.class);
    private ResearchBot researchBot;

    public ResearchBotResource(SymphonyTestConfiguration config, MongoDBClient mongoDBClient) {
        this.config = config;
        try {
            SymphonyClient symClient = new SymphonyAuth().init(config);
            System.out.println(symClient.getSymAuth().getSessionToken().getToken());
            System.out.println(symClient.getSymAuth().getKeyToken().getToken());
            researchBot = ResearchBot.getInstance(symClient, config, mongoDBClient);

        } catch (Exception e) {
            LOG.error("error", e);
        }
    }

    @POST
    @Path("/article")
    public Response sendIOI(ResearchArticle researchReceived) {
        try {
            researchBot.distributeResearch(researchReceived);
        } catch (UsersClientException e) {
            return Response.status(500).build();
        }

        return Response.status(Response.Status.OK).build();
    }

}
