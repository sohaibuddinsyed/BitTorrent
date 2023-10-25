import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

// Class to maintain Helper Methods that can be used by other classes
public class HelperMethods {

    // To send a message of type byte[] through a socket's DataOutputStream
    static void sendMessage(byte[] msg, DataOutputStream out)
    {
        try{
            //stream write the message
            out.write(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}
