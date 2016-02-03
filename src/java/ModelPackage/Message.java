
package ModelPackage;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private Date timestamp;
    
    public Message(){
        
    }
    
    public int getMessageID(){
        return messageID;
    }
}
