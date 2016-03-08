/*
This class is purely used for parsing XML in the Groups resource.
The reason that the actual Message object is not parsed as XML is because it has no variable containing the Username because
users may change their username.

For that reason the username is fetched from the user object in the backend before returning it as XML to the client side.
This way a message will always show the current username.
*/

package ResourcePackage;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "messageroot")
public class MessageXML implements Serializable, Comparable<MessageXML> {

    private static final long serialVersionUID = 1L;
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private String timestamp;
    private String username;

    public MessageXML() {

    }

    public MessageXML(int userID, int groupID, int messageID, String username, String message, String timestamp) {
        this.userID = userID;
        this.groupID = groupID;
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
    }

    @XmlElement
    public int getMessageID() {
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
    public String getUsername() {
        return username;
    }

    @Override
    public int compareTo(MessageXML msg) {
        int compareID = msg.getMessageID();

        return this.messageID - compareID;
    }

}
