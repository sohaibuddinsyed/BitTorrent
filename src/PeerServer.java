import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PeerServer extends Thread {

    private int peer_id, peer_port;
    private HashMap<Integer, PeerDetails> neighbors_list;
    private ArrayList<Integer> previous_neighbors_ids;
    private BitSet bitfield_piece_index;
    private Logger logger;

    public PeerServer(int peer_id, int peer_port, HashMap<Integer, PeerDetails>  neighbors_list, ArrayList<Integer>  previous_neighbors_ids, BitSet bitfield_piece_index, Logger logger) {
        this.peer_id                = peer_id;
        this.peer_port = peer_port;
        this.neighbors_list         = neighbors_list;
        this.previous_neighbors_ids = previous_neighbors_ids;
        this.bitfield_piece_index   = bitfield_piece_index;
        this.logger = logger;
    }
    public void run() {
        System.out.println("The server is running.");
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(peer_port);
            while(true) {
                new Handler(listener.accept(), peer_id, logger, bitfield_piece_index).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                listener.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private byte[] hand_shake_rcv;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client
        private Socket connection;
        private DataInputStream in;	//stream read from the socket
        private DataOutputStream out;    //stream write to the socket
        private int peer_id;
        private Logger logger;
        private BitSet bitfield_piece_index;

        public Handler(Socket connection, int peer_id, Logger logger, BitSet bitfield_piece_index) {
            this.connection = connection;
            this.peer_id    = peer_id;
            this.hand_shake_rcv = new byte[32];
            logger = this.logger;
        }

        public void run() {
            try{
                //initialize Input and Output streams
                out = new DataOutputStream(connection.getOutputStream());
                out.flush();
                in = new DataInputStream(connection.getInputStream());
                try{
                    while(true)
                    {
                        // Receive handshake from client
                        int msg_len = in.read(hand_shake_rcv);
                        System.out.println(msg_len + "bytes HS recvd at server");
                        String client_peer_id = new String(hand_shake_rcv).substring(28);
                        logger.log("is connected from Peer " + client_peer_id);
                        
                        HandShake.VerifyHandShakeMessage(hand_shake_rcv);

                        // Send handshake to client
                        HandShake hand_shake_msg = new HandShake(Integer.parseInt(client_peer_id));
                        sendMessage(hand_shake_msg.BuildHandshakeMessage());
                        System.out.println("Server send handshake");

                        // Receive bitfield message from client
                        Message bit_field_rcv = new Message(0, (byte)5, in.readAllBytes());

                        // Send bitfield to client
                        Message bit_field_message = new Message(bitfield_piece_index.size()/8, (byte)5, bitfield_piece_index.toByteArray());
                        System.out.println(bit_field_message.BuildMessageByteArray());
                        sendMessage(bit_field_message.BuildMessageByteArray());

                        // boolean interested = P2PMessageHandler.HandleBitFieldMessage(bitfield_piece_index);

                        // Send interested or not interested message
                        // if (interested) {
                        //     Message interested_msg = new Message(0, (byte)2, new byte[0]);
                        //     sendMessage(interested_msg.BuildMessageByteArray());
                        // } else {
                        //     Message not_interested_msg = new Message(0, (byte)2, new byte[0]);
                        //     sendMessage(not_interested_msg.BuildMessageByteArray());
                        // }
                    }
                }
                catch(Exception classnot){
                    System.err.println("Data received in unknown format");
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client ");
            }
            finally{
                //Close connections
                try{
                    in.close();
                    out.close();
                    connection.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client ");
                }
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg)
        {
            try{
                out.writeBytes(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client ");
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

        void sendMessage(byte[] msg)
        {
            try{
                //stream write the message
                //writeObject or just write?
                out.write(msg);
                out.flush();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

}
