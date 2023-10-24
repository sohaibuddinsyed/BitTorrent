import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class Message {
    private Integer message_length;
    private Byte message_type;
    private byte[] message_payload;

    public Message(int message_length, byte message_type, byte[] message_payload) {
        this.message_length = message_length;
        this.message_type = message_type;
        this.message_payload = message_payload;
    }

    public byte[] BuildMessageByteArray() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                buffer.write(message_length);
                buffer.write(message_type);
                buffer.write(message_payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return buffer.toByteArray();
    }

    public MessageType GetMessageType() {
        switch(message_type) {
            case 0: return MessageType.CHOKE;
            case 1: return MessageType.UNCHOKE;
            case 2: return MessageType.INTERESTED;
            case 3: return MessageType.NOTINTERESTED;
            case 4: return MessageType.HAVE;
            case 5: return MessageType.BITFIELD;
            case 6: return MessageType.REQUEST;
            case 7: return MessageType.PIECE;
            default: return MessageType.UNKNOWN;            
        }
    }

    public boolean HandleBitFieldMessage(BitSet bitfield_piece_index) {
        BitSet peer_bitset = new BitSet(message_payload.length * 8); // 8 bits in a byte
    
        for (int i = 0; i < message_length; i++) {
            for (int j = 0; j < 8; j++) {
                if ((message_payload[i] & (1 << j)) != 0) {
                    peer_bitset.set(i * 8 + j);
                }
            }
        }

        BitSet copy = (BitSet) bitfield_piece_index.clone();
        copy.andNot(peer_bitset);
        return !copy.isEmpty();
    }

    public void HandleChokeMessage() {
        // To-do
    }

    public void HandleUnChokeMessage() {
        // To-do
    }

    public void HandleInterestedMessage() {
        // To-do
    }

    public void HandleNotInterestedMessage() {
        // To-do
    }

    public void HandleHaveMessage() {
        // To-do
    }

    public void HandleRequestMessage() {
        // To-do
    }

    public void HandlePieceMessage() {
        // To-do
    }
}