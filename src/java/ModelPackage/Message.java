
package ModelPackage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="messageroot")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private Date timestamp;
    
    public Message(){
        
    }
    
    public Message(int userID, int groupID,int messageID, String message, Date timestamp){
        this.userID = userID;
        this.groupID = groupID;
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
    }
    @XmlElement
    public int getMessageID(){
        return messageID;
    }
    @XmlElement
    public int getUserID() {
        return userID;
    }
    @XmlElement
    public int getGroupID() {
        return groupID;
    }
    @XmlElement
    public String getMessage() {
        return message;
    }
    @XmlElement
    public String getTimestamp() {
        String returnstamp = new Timestamp(timestamp.getTime()).toString();
        return returnstamp;
    }
    
    public Timestamp getTimestampObject() {
        return new Timestamp(timestamp.getTime());
        
    }
}
