# Structured Objects Dropwizard Bot Sample

## Introduction

This example shows a simple Symphony chat bot using the [SymphonyOSS symphony-java-client](https://github.com/symphonyoss/symphony-java-client) and Dropwizard. The Trade Alert Bot is part of a PWM demo that shows the interaction on Symphony between an FA and a client. The bot responds to the keyword 'alert' with a structured object message. Also, since it is a Dropwizard application, endpoints can be configured to trigger messages. 

## Pre-requisites

1. Service account with a valid certificate for the bot
2. Extension API application with a entity renderer for the structured object

## Overview

At startup, the TradeBot is initialized as a Chat and Room listener so the implemented methods respond to the events of the datafeed for the bot.

* The `onChatMessage` method has the code to send a messageML with a structured object. The entity data should indicate the type (this refers to the renderer Extensions API application for the message)
and any additional fields for the object that will be rendered, in this case the sender's email address.

In this sample, the renderer for the structured object uses an iframe that is clickable on the Symphony UI. To bring the conversation back to the chat itself, a button is set up to call an endpoint of this application that then posts a confirmation message to the specified streamId. 

* The `TradeBotResource` includes the endpoints that given certain parameters, trigger the bot to send messages to either 1-1 conversation with a userb by email, or a streamId.

## Running This Sample

Set up your config in `example.yml`. Fill out the following parameters.

        sessionAuthURL: 
        keyAuthUrl: 
        localKeystorePath: 
        localKeystorePassword: 
        botCertPath: 
        botCertPassword: 
        botEmailAddress: 
        agentAPIEndpoint: 
        podAPIEndpoint: 
        userEmailAddress: 
        
        keyStorePath: 
        keyStorePassword: 

To test the application run the following commands.

* To package the example run.

        mvn package

* To run the server run.

        java -jar target/dropwizard-api-test-1.1.0-SNAPSHOT-sources.jar server example.yml

* To hit the endpoint to send a Trade Alert message

	http://localhost:7070/tradeBot/sendTradeAlert?email=[yourtestemail]
