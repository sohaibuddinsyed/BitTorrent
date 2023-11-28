import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class P2PMessageHandler {
    peerProcess host_peer; // Current host
    PeerDetails neighbor_peer; // Neighbor peer to which the current TCP is established

    public P2PMessageHandler(peerProcess host_peer, PeerDetails neighbor_peer) {
        this.host_peer     = host_peer;
        this.neighbor_peer = neighbor_peer;
    }

    // Method to handle BitField Message received from Neighbor
    public void HandleBitFieldMessage(Message message) {
        // Create an empty bitset of same length as payload
        BitSet peer_bitset                 = new BitSet(message.GetMessagePayload().length * 8); // 8 bits in a byte
        // Retrieve the message payload and store it in neighbor_peer object
        byte[] message_payload             = message.GetMessagePayload();
        neighbor_peer.bitfield_piece_index = BitSet.valueOf(message_payload);

        // Set the peer bitfield from bitfield index payload
        for (int i = 0; i < message.GetMessageLength(); i++) {
            // Parse each byte of the bitfield message
            for (int j = 0; j < 8; j++) {
                if ((message_payload[i] & (1 << j)) != 0) {
                    peer_bitset.set(i * 8 + j);
                }
            }
        }
        // Compare with current host's bitfield_piece_index
        BitSet copy = (BitSet) host_peer.host_details.bitfield_piece_index.clone();
        // Below will set the bit fields as one if there's any missing piece available in neighbor
        copy.andNot(peer_bitset);

        // Send Interested if the above result is not empty else send NotInterested message
        byte msg_type = !copy.isEmpty() ? (byte)2 : (byte)3;

        // Make third argument in Message as None and avoid sending third argument?
        Message msg = new Message(0, msg_type, new byte[0]);
        Utils.sendMessage(msg.BuildMessageByteArray(), neighbor_peer.out);
    }

    public void HandleChokeMessage() {
        host_peer.choked_by_neighbors.put(neighbor_peer.peer_id, true);
    }

    public void HandleUnChokeMessage() {
        host_peer.choked_by_neighbors.put(neighbor_peer.peer_id, false);
        // Gets next interested index and sends request message
        int interested_index = Utils.GetInterestIndex(host_peer, neighbor_peer);
        if (interested_index != -1) {
            Message msg = new Message(0, (byte)6,
                    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(interested_index).array());
            Utils.sendMessage(msg.BuildMessageByteArray(), neighbor_peer.out);
            host_peer.requested_indices.add(interested_index);
        }
    }

    public void HandleInterestedMessage() {
        host_peer.neighbors_interested_in_host.put(neighbor_peer.peer_id, true);
    }

    public void HandleNotInterestedMessage() {
        host_peer.neighbors_interested_in_host.put(neighbor_peer.peer_id, false);
    }

    // Handler for 'have' message type
    public void HandleHaveMessage(Message message_received) {
        // Set the bitfield index received from the neighbor_peer
        int bitfield_index = Integer.parseInt(message_received.GetMessagePayload().toString());
        neighbor_peer.bitfield_piece_index.set(bitfield_index);

        // Check if interested
        boolean send_interested = Utils.CheckInterestInIndex(host_peer.host_details, neighbor_peer, bitfield_index);

        byte msg_type = send_interested ? (byte)2 : (byte)3;

        Message msg = new Message(0, msg_type, new byte[0]);
        Utils.sendMessage(msg.BuildMessageByteArray(), neighbor_peer.out);
    }

    public void HandleRequestMessage(Message message_received) {
        if (!host_peer.unchoked_by_host.get(neighbor_peer.peer_id))
            return;
        int requested_index = Integer.parseInt(message_received.GetMessagePayload().toString());
        // Create a class to handle file division into pieces and to pull required piece
    }

    public void HandlePieceMessage() {
        // To-do
        
    }

    public void MessageListener() throws IOException {
        DataInputStream in = neighbor_peer.in;
        MessageType msg_type;
        while (true) {
            // Receive message and retrieve the message type
            Message message_received = new Message((byte[]) in.readAllBytes());
            msg_type = message_received.GetMessageType();

            // Take Action based on message type received
            switch(msg_type) {
                case CHOKE: {
                    HandleChokeMessage();
                    break;
                }
                case UNCHOKE: {
                    HandleUnChokeMessage();
                    break;
                }
                case INTERESTED: {
                    HandleInterestedMessage();
                    break;
                }
                case NOTINTERESTED: {
                    HandleNotInterestedMessage();
                    break;
                }
                case HAVE: {
                    HandleHaveMessage(message_received);
                    break;
                }
                case BITFIELD: {
                    HandleBitFieldMessage(message_received);
                    break;
                }
                case REQUEST: {
                    HandleRequestMessage(message_received);
                    break;
                }
                case PIECE: {
                    HandlePieceMessage();
                    break;
                }
                default: ;
            }
        }
    }
}
