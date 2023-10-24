import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class PeerClient extends Thread{

    private int peer_id;
    private HashMap<Integer, PeerDetails> neighbors_list;
    private ArrayList<Integer> previous_neighbors_ids;
    private BitSet bitfield_piece_index;
    private Logger logger;

    public PeerClient(int peer_id, HashMap<Integer, PeerDetails>  neighbors_list, ArrayList<Integer>  previous_neighbors_ids, BitSet bitfield_piece_index, Logger logger) {
        this.peer_id                = peer_id;
        this.neighbors_list         = neighbors_list;
        this.previous_neighbors_ids = previous_neighbors_ids;
        this.bitfield_piece_index   = bitfield_piece_index;
        this.logger = logger;
    }

    public void run() {
        // Creating Client object for all the neighbors that requires TCP Connection
        for (int id: previous_neighbors_ids) {
            new Client(neighbors_list.get(id)).start();
        }
    }

    public class Client extends Thread{
        Socket requestSocket;           //socket connect to the server
        DataOutputStream out;         //stream write to the socket
        DataInputStream in;          //stream read from the socket
        String message;                //message send to the server
        String MESSAGE;                //capitalized message read from the server
        PeerDetails peer_details;

        public Client(PeerDetails peer_details) {
            this.peer_details = peer_details;
        }

        public void run() {
            try {
                //create a socket to connect to the server
                System.out.println("Client is running" + peer_details.peer_id);
                requestSocket       = new Socket(peer_details.hostname, peer_details.peer_port);
                peer_details.socket = requestSocket;
                logger.log("makes a connection to Peer " + peer_details.peer_id);

                //initialize inputStream and outputStream
                out = new DataOutputStream(requestSocket.getOutputStream());
                out.flush();
                in = new DataInputStream(requestSocket.getInputStream());

                HandShake hand_shake = new HandShake(peer_details.peer_id);
                sendMessage(hand_shake.BuildHandshakeMessage());

                byte[] hand_shake_rcv = new byte[32];
                in.read(hand_shake_rcv);

                while (true) {
                    // HandShake message received and verified
                    if (hand_shake.VerifyHandShakeMessage(hand_shake_rcv, peer_details.peer_id))
                        break;
                }

                System.out.println(bitfield_piece_index.length());

                Message bit_field_message = new Message(bitfield_piece_index.size()/8, (byte)5, bitfield_piece_index.toByteArray());
                System.out.println(bit_field_message.BuildMessageByteArray());
                sendMessage(bit_field_message.BuildMessageByteArray());

                System.out.println("Bitfield sent");

                Message bit_field_rcv = new Message(0, (byte)5, in.readAllBytes());
                boolean interested = bit_field_rcv.HandleBitFieldMessage(bitfield_piece_index);

                System.out.println("Bitfield recvd");

                if (interested) {
                    Message interested_msg = new Message(0, (byte)2, new byte[0]);
                    sendMessage(interested_msg.BuildMessageByteArray());
                } else {
                    Message not_interested_msg = new Message(0, (byte)2, new byte[0]);
                    sendMessage(not_interested_msg.BuildMessageByteArray());
                }

            } catch (ConnectException e) {
                System.err.println("Connection refused. You need to initiate a server first.");
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                //Close connections
                try {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        //send a message to the output stream
        void sendMessage(String msg)
        {
            try{
                //stream write the message
                out.writeBytes(msg);
                out.flush();
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
