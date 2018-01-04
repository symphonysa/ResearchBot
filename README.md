# Reseach Distribution Bot Sample

## Introduction

This example uses the [SymphonyOSS symphony-java-client](https://github.com/symphonyoss/symphony-java-client) and Dropwizard. The Research Distribution Bot receives content from users in the pod it is deployed in and forwards it to external users who have registered their interest to follow specific tags or authors.
## Pre-requisite

Service account with a valid certificate provisioned for external communication

## Overview

* At startup, the Research Distribution Bot is initialized as a Chat and Room listener so the implemented methods respond to the events of the datafeed for the bot.

#### Key Concepts

* `ResearchBot` class drives the bot has all logic associated with ingesting and constructing messages. Changes to the behavior of the bot should be implemented here.

* `MongoDBClient` class retrieves and writes information to a mongo database set as URL parameter your in `sample.yml`. 

* The POJOs for the objects being stored on the database are under the `model` package. `ResearchInterest` stores the tags and users followed by a streamId and `ResearchSent` stores the messages that were sent as a result of an interest, this is useful to correlate to orders received.

* `MessageParser` class retrieves the entities that were sent in a message and classifies them into 3 hashmaps: hashtags, cashtags, and users



## Running This Sample

Set up your config in `sample.yml`. Fill out the following parameters.

        sessionAuthURL: https://your-pod.symphony.com/sessionauth
        keyAuthUrl: https://your-km.symphony.com:8444/keyauth
        localKeystorePath: complete path to your jks keystore
        localKeystorePassword: keystore password
        botCertPath: complete path to your bot's p12 file
        botCertPassword: password
        botEmailAddress: bot.user@example.com
        agentAPIEndpoint: https://your-agent.symphony.com/agent
        podAPIEndpoint: https://your-pod.symphony.com/pod
        
        keyStorePath: 
        keyStorePassword: 

If you are developing a bot that lives within an enterprise pod with on-premise components (KM and Agent) and need a proxy to reach the cloud (your pod) add the following field to your sample.yml file

        proxyURL: url to your internal proxy


To test the application run the following commands.

* To package the example run.

        mvn package

* To run the server run.

        java -jar target/research-bot-1.1.0-SNAPSHOT-sources.jar server sample.yml
        
### Why Dropwizard?
This code can be implemented all from a Main class, for an example of this check out [ChatBot Sample](https://github.com/symphonysa/ChatBotSample), but this example is created as a Dropwizard application so it can be extended to have endpoints to post content from other systems your firm might already use to distribute content. An example will be included in a next phase, but if you want to implement this, start by adding a method on the `ResearchBotResource` class and using the ResearchBot instance to create methods to handle that. 

### Want more info?
Contact Symphony Platform Solutions Team
