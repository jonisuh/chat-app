
package ResourcePackage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="messageroot")
public class MessageXML implements Serializable, Comparable<MessageXML> {
    private static final long serialVersionUID = 1L;
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private String timestamp;
    private String username;
    
    public MessageXML(){
        
    }
    
    public MessageXML(int userID, int groupID,int messageID,String username, String message, String timestamp){
        this.userID = userID;
        this.groupID = groupID;
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
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
        return timestamp;
    }

    @XmlElement
    public String getUsername(){
        return username;
    }

    @Override
    public int compareTo(MessageXML msg) {
        int compareID = msg.getMessageID();
        
        return this.messageID-compareID;
    }

   
}
