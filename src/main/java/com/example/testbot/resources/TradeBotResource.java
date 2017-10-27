package com.example.testbot.resources;

import com.example.testbot.SymphonyTestConfiguration;
import com.example.testbot.bots.TradeBot;
import com.example.testbot.utils.SymphonyAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/tradeBot")
public class TradeBotResource {

    private SymphonyTestConfiguration config;
    private SymphonyClient symClient;
    private final Logger LOG = LoggerFactory.getLogger(TradeBotResource.class);
    private TradeBot tradeBot;

    public TradeBotResource(SymphonyTestConfiguration config) {
        this.config = config;
        try {
            SymphonyClient symClient = new SymphonyAuth().init(config);
            tradeBot = TradeBot.getInstance(symClient, config);

        } catch (Exception e) {
            LOG.error("error", e);
        }
    }


    @GET
    @Path("/sendMandateAlert")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendTradeAlert(@QueryParam("email") String email) {

        try {
            tradeBot.sendMandateAlertMsg(email);

        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return "Alert sent";
    }

    @GET
    @Path("/sendTradeConfirm")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendTradeAlert(@QueryParam("streamId") String streamId, @QueryParam("action") String action,@QueryParam("symbol") String symbol,@QueryParam("numShares") int numShares,@QueryParam("price") int price) {

        try {
            tradeBot.sendTradeConfirmation(streamId, action, symbol, numShares, price);

        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return "Trade alert sent";
    }

}
