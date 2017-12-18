# Structured Objects Dropwizard Bot Sample

## Introduction

This example uses the [SymphonyOSS symphony-java-client](https://github.com/symphonyoss/symphony-java-client) and Dropwizard. The Research Distribution Bot receives content from users in the pod it is deployed in and forwards it to external users who have registered their interest to follow specific tags or authors.
## Pre-requisites

1. Service account with a valid certificate for the bot

## Overview

At startup, the Research Distribution Bot is initialized as a Chat and Room listener so the implemented methods respond to the events of the datafeed for the bot.

* The `onChatMessage` method has the code to send a messageML with a structured object. The entity data should indicate the type (this refers to the renderer Extensions API application for the message)
and any additional fields for the object that will be rendered, in this case the sender's email address.


## Running This Sample

Set up your config in `sample.yml`. Fill out the following parameters.

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
        proxyURL:
        mongoURL:
        
        keyStorePath: 
        keyStorePassword: 

To test the application run the following commands.

* To package the example run.

        mvn package

* To run the server run.

        java -jar target/research-bot-1.1.0-SNAPSHOT-sources.jar server sample.yml

