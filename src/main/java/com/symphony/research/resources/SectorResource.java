package com.symphony.research.resources;

import com.symphony.research.SymphonyTestConfiguration;
import com.symphony.research.bots.ResearchBot;
import com.symphony.research.model.ResearchArticle;
import com.symphony.research.model.ResearchInterest;
import com.symphony.research.model.SectorUser;
import com.symphony.research.mongo.MongoDBClient;
import com.symphony.research.utils.SymphonyAuth;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.UsersClientException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/sector")
public class SectorResource {

    private SymphonyTestConfiguration config;
    private MongoDBClient mongoDBClient;

    public SectorResource(SymphonyTestConfiguration config, MongoDBClient mongoDBClient) {
        this.config = config;
        this.mongoDBClient = mongoDBClient;
    }

    @POST
    @Path("/user")
    public Response addUserToSector(SectorUser sectorUser) {
        mongoDBClient.newSectorUser(sectorUser);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/interest")
    public Response addSectorInterest(ResearchInterest researchInterest) {
        mongoDBClient.newResearchInterest(researchInterest);

        return Response.status(Response.Status.OK).build();
    }

}
