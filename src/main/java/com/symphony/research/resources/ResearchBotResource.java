package com.symphony.research.resources;

import com.symphony.research.SymphonyTestConfiguration;
import com.symphony.research.bots.ResearchBot;
import com.symphony.research.utils.SymphonyAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;

import javax.ws.rs.Path;

@Path("/researchBot")
public class ResearchBotResource {

    private SymphonyTestConfiguration config;
    private SymphonyClient symClient;
    private final Logger LOG = LoggerFactory.getLogger(ResearchBotResource.class);
    private ResearchBot researchBot;

    public ResearchBotResource(SymphonyTestConfiguration config) {
        this.config = config;
        try {
            SymphonyClient symClient = new SymphonyAuth().init(config);
            System.out.println(symClient.getSymAuth().getSessionToken().getToken());
            System.out.println(symClient.getSymAuth().getKeyToken().getToken());
            researchBot = ResearchBot.getInstance(symClient, config);

        } catch (Exception e) {
            LOG.error("error", e);
        }
    }


}
