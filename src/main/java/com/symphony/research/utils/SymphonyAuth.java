package com.symphony.research.utils;

import com.symphony.research.SymphonyTestConfiguration;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.AuthenticationClient;

import javax.ws.rs.client.Client;


public class SymphonyAuth {

    public SymphonyClient init(SymphonyTestConfiguration config) throws Exception{

        SymphonyClient symClient;

        symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.V4);

        AuthenticationClient authClient;
//        //Proxy config example
        if(config.getProxyURL() != null){
            ClientConfig proxyClientConfig = new ClientConfig();
            proxyClientConfig.connectorProvider(new ApacheConnectorProvider());
            proxyClientConfig.property(ClientProperties.PROXY_URI, config.getProxyURL());
            Client proxyHttpClient = CustomHttpClient.getClient(config.getBotCertPath(),config.getBotCertPassword(),config.getLocalKeystorePath(),config.getLocalKeystorePassword(),proxyClientConfig);

            Client localHttpClient = CustomHttpClient.getClient(config.getBotCertPath(),config.getBotCertPassword(),config.getLocalKeystorePath(),config.getLocalKeystorePassword());

            authClient = new AuthenticationClient(config.getSessionAuthURL(), config.getKeyAuthUrl(),proxyHttpClient,localHttpClient);
            symClient.setAgentHttpClient(localHttpClient);
            symClient.setPodHttpClient(proxyHttpClient);
        }
        else{
            authClient = new AuthenticationClient(config.getSessionAuthURL(), config.getKeyAuthUrl());

        }




        //Set the local keystores that hold the server CA and client certificates
        authClient.setKeystores(
                config.getLocalKeystorePath(),
                config.getLocalKeystorePassword(),
                config.getBotCertPath(),
                config.getBotCertPassword());

        SymAuth symAuth = authClient.authenticate();


        //Set agent and pod clients if custom are needed


        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(false);
        symphonyClientConfig.set(SymphonyClientConfigID.AGENT_URL, config.getAgentAPIEndpoint());
        symphonyClientConfig.set(SymphonyClientConfigID.POD_URL,config.getPodAPIEndpoint());
        symphonyClientConfig.set(SymphonyClientConfigID.USER_EMAIL, config.getUserEmailAddress());

        symClient.init(symAuth,symphonyClientConfig);

        return symClient;
    }
}
