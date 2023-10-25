# BitTorrent

Computer Networks ProjectGroup Name: **Sohaib Uddin Syed's Group**

## Project Members 
-	Sohaib Uddin Syed (57405488)
-	Kshitij Maruti Vejre (83323074)
-	Prashanth Thamminedi (45331532)

## Deliverables
- 809 lines of java code

## Usage
- Unzip the project and navigate to src directory
- Compile with ```javac *.java```
- Project compiled with ```openjdk 17.0.8.1```
  
## Project Implementation (until now)
1.	peerProcess.java
-	Reads the Common.cfg and PeerInfo.cfg files and sets the values accordingly in a required Data Structure.
-	Sets BitField based on the values in Common.cfg file.
-	Creates PeerClient and PeerServer objects and starts them to establish TCP Connections and to listen incoming Connections respectively.

2.	PeerDetails.java
-	This file is responsible for storing each peer details as an object with all it’s details like id, port, file availability, bitfield, socket, etc

3.	PeerClient.java
-	Successfully implemented this file to establish TCP Connections to the peers listed before current peer in PeerInfo.cfg file.
-	Sends Handshake message and receives handshake which will be verified. This will be done by Hanshake.java class.
-	Stores the current socket, Input, and output data streams in Neighbor’s PeerDetails object.
-	Once Hanshake message is verified, the bitfield of the current host will be transmitted.
-	Creates a P2PMessageHandler object to receive and handle all the messages after sending the bitfield.

4.	PeerServer.java
-	Successfully implemented this file to listen for incoming TCP Connections.
-	Receives a handshake which will be verified, and a handshake will be transmitted to the client.
-	Bitfield of the current host will be transmitted.
-	Creates a P2PMessageHandler object to receive and handle all the messages after sending the bitfield.




5.	Message.java
-	This file is responsible to create a Message object which can be used to build Message byte array for transmitting the message, get the message type by making use of MessageType.java, get the message length or payload.

6.	P2PMessageHandler.java
-	This is one of the main files which handles all the messages post sending bitfields to Neighbors.
-	Created modules for each Message type and successfully implemented HandleBitFieldMessage() which receives the bitfield, parses it and finds out if neighbor has any interesting piece and sends interested or not interested message respectively.

7.	Logger.java
-	Successfully implemented logger which can be used as an object for each peer and log the respective actions performed in each java file.

