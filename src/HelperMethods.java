import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class HelperMethods {
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
