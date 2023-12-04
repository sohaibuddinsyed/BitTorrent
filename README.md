# BitTorrent

Computer Networks ProjectGroup Name: **Sohaib Uddin Syed's Group**

## Project Members 
-	Sohaib Uddin Syed (57405488)
-	Kshitij Maruti Vejre (83323074)
-	Prashanth Thamminedi (45331532)

## Usage
- Unzip the project and navigate to src directory
- Compile with ```javac *.java```
- Project compiled with ```openjdk 17.0.8.1```
- Execute with ```./compileJava``` on each machine
  
## Project Implementation
1.	peerProcess.java
-	Reads the Common.cfg and PeerInfo.cfg files and sets the values accordingly in a required Data Structure.
-	Makes use of PeerDetails class to store the details of each neighbors.
-	Sets BitField based on the values in Common.cfg file.
-	Creates a FileHandler object which splits the file into a number of pieces if host has a file.
-	Creates PeerClient and PeerServer objects and starts them to establish TCP Connections and to listen incoming Connections respectively.
-	Creates SelectNeighbors and SelectOptNeighbor objects to select k preferred neighbors and 1 optimistically unchoked neighbor for a given period of time. 

2.	PeerDetails.java
-	This file is responsible for storing each peer details as an object with all it’s details like id, port, file availability, bitfield, socket, etc.
-	The values are updated based on the received messages.

3.	PeerClient.java
-	Successfully implemented this file to establish TCP Connections to the peers listed before current peer in PeerInfo.cfg file.
-	Sends a Handshake message and receives handshake from neighbor which will be verified. This will be done by Hanshake.java class.
-	Stores the current socket, Input, and output data streams in Neighbor’s PeerDetails object.
-	Once Hanshake message is verified, the bitfield of the current host will be transmitted.
-	Creates a P2PMessageHandler object to receive and handle all the messages after sending the bitfield.

4.	PeerServer.java
-	Successfully implemented this file to listen for incoming TCP Connections.
-	Receives a handshake which will be verified, and a handshake will be transmitted back to the client.
-	Bitfield of the current host will be transmitted.
-	Creates a P2PMessageHandler object to receive and handle all the messages after sending the bitfield.

5.	Message.java
-	This file is responsible to create a Message object which can be used to build Message byte array for transmitting the message, get the message type by making use of MessageType.java, get the message length or payload.

6.	P2PMessageHandler.java
-	This is one of the main files which handles all the messages received post sending bitfields to Neighbors.
-	Created modules for each Message type and handled all the requirements based on the received message.
-	Makes use of Utils.java to send a message on a given socket's Output stream, check if the host is interested in a given piece index, get the next interested index and to check if host received all the pieces.
-	Whenever a host receives the piece, it uses FileHandler.java to set the piece and to build a file in the end when all the pieces are received.

7.	Logger.java
-	Successfully implemented logger which can be used as an object for each peer and log the respective actions performed in each java file.

8. SelectNeighbors.java and SelectOptNeighbor.java
- SelectNeighbors.java is implemented to select k preferred Neighbors based on the download speed or randomly if choosing for the first time.
- SelectOptNeighbor.java selects 1 optimistically unchoked neighbor randomly on the current choked neighbors that are interested in the host.

