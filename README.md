# P2P-Minecraft-server #

This project aims to create a peer-to-peer Minecraft server structure that can alleviate the stresses and costs of a client-server architecture as well as research solutions to commonly known problems in peer-to-peer architectures.

## Setup ##

This code has 5 different parts that need to be run in order to get the full system working:
* The Minecraft client (currently working with version 1.11.2)
* The client proxy
* The SPS (Spatial Publish/Subscribe)
* The server proxy
* A spigot server (version 1.11.2)

The proxies, SPS and spigot use Maven to compile and manage dependencies.

### Minecraft client ###

The normal Minecraft launcher can be used with a version 1.11.2 profile.

### Client proxy ###

In the root folder of the HerobrineClientProxy, run 'mvn clean install' to create a 'HerobrineProxy.jar' file in the 'target' folder. Change directory to the 'target' folder and run this file in the command line with the following command: 'java -jar Herobrineproxy.jar 127.0.0.1 3000 127.0.0.1 25570'. The first address and port defines where to access the SPS and the second address defines where the Minecraft client needs to connect to in order to connect to the client proxy.

### Server proxy ###
In the root folder of the HerobrineServerProxy, run 'mvn clean install' to create a 'HerobrineProxy.jar' file in the 'target' folder. Change directory to the 'target' folder and run this file in the command line with the following command: 'java -jar Herobrineproxy.jar 127.0.0.1 25565 127.0.0.1 3000'. The first address and port defines where to access the Minecraft server and the second address defines where to access the SPS.

### SPS ###
Open a command prompt and run the command 'node server.js' to start an http SPS server listening on port 3000.

### Spigot server ###
Go to https://www.spigotmc.org/wiki/buildtools/#1-11 and follow the instructions to build and run a Minecraft server for version 1.11.2. The only difference is one needs to run the command 'java -jar BuildTools.jar --rev 1.11.2' to build a server for the correct version.

## Running the test ##
When all the separate parts of the setup are complete and the proxies, SPS and Spigot server are built and running, go into the launched Minecraft client and click 'Multiplayer' and the 'Direct connect'. Type in the address '127.0.0.1:25570' in order for the client to connect to the client proxy. The client will then login and everything will run normally (hopefully).
