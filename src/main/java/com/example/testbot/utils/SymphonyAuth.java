package com.example.testbot.utils;

        import com.example.testbot.SymphonyTestConfiguration;
        import org.json.simple.JSONObject;
        import org.json.simple.parser.JSONParser;
        import org.json.simple.parser.ParseException;
        import org.symphonyoss.client.SymphonyClient;
        import org.symphonyoss.client.SymphonyClientFactory;
        import org.symphonyoss.client.model.SymAuth;
        import org.symphonyoss.symphony.authenticator.model.Token;
        import org.symphonyoss.symphony.clients.AuthorizationClient;

        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.Calendar;
        import java.util.Date;

/**
 * Created by mike.scannell on 11/7/16.
 */

public class SymphonyAuth {

    public SymphonyClient init(SymphonyTestConfiguration config) throws Exception{

        SymphonyClient symClient;

        symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4);

        //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
        //the same fqdn but different URLs.
        AuthorizationClient authClient = new AuthorizationClient(
                config.getSessionAuthURL(),config.getKeyAuthUrl());


        //Set the local keystores that hold the server CA and client certificates
        authClient.setKeystores(
                config.getLocalKeystorePath(),
                config.getLocalKeystorePassword(),
                config.getBotCertPath(),
                config.getBotCertPassword());

        SymAuth symAuth = new SymAuth();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(config.getTokensFile()));

            JSONObject jsonObject = (JSONObject) obj;
            Long dateTime = (Long) jsonObject.get("date");
            Date date = new Date();
            date.setTime(dateTime);

            if(date.after(addDays(new Date(), -13))){
                Token sessionToken = new Token();
                sessionToken.setName("sessionToken");
                sessionToken.setToken((String) jsonObject.get("sessionToken"));

                Token kmToken = new Token();
                kmToken.setName("keyManagerToken");
                kmToken.setToken((String) jsonObject.get("keyManagerToken"));

                symAuth.setSessionToken(sessionToken);
                symAuth.setKeyToken(kmToken);
            } else {
                symAuth = newAuth(symAuth, authClient, config);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            symAuth = newAuth(symAuth, authClient, config);
        } catch (IOException e) {
            e.printStackTrace();
            symAuth = newAuth(symAuth, authClient, config);
        } catch (ParseException e) {
            symAuth = newAuth(symAuth, authClient, config);
            e.printStackTrace();
        }


        //With a valid SymAuth we can now init our client.
        symClient.init(
                symAuth,
                config.getBotEmailAddress(),
                config.getAgentAPIEndpoint(),
                config.getPodAPIEndpoint()
        );

        return symClient;

    }

    private Date addDays(Date d, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, days);
        d.setTime(c.getTime().getTime());
        return d;
    }

    private SymAuth newAuth(SymAuth symAuth, AuthorizationClient authClient, SymphonyTestConfiguration config) throws Exception {

        //Create a SymAuth which holds both key and session tokens.  This will call the external service.
        symAuth = authClient.authenticate();

        JSONObject obj = new JSONObject();
        obj.put("sessionToken", symAuth.getSessionToken().getToken());
        obj.put("keyManagerToken", symAuth.getKeyToken().getToken());
        obj.put("date", new Date().getTime());

        try (FileWriter file = new FileWriter(config.getTokensFile())) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return symAuth;
    }

}
