/*
This is the message data class. 
It contains a unique messageID aswell as the ID integers of the user that sent the message and the group that it was sent to
The instance variable 'message' contains the actual input of the user.
The timestamp is a date object that is created from GroupDao when the message is sent.

The class has a method for returning the date object aswell as the date as a string.

This class does not import javax xml annotations because it's not directly used to parse XML from the REST Resources.
 */
package ModelPackage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private Date timestamp;

    public Message() {

    }

    public Message(int userID, int groupID, int messageID, String message, Date timestamp) {
        this.userID = userID;
        this.groupID = groupID;
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
    }

    //GET METHODS
    public int getMessageID() {
        return messageID;
    }

    public int getUserID() {
        return userID;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getMessage() {
        return message;
    }

    //Returns the timestamp as a string
    public String getTimestamp() {
        String returnstamp = new Timestamp(timestamp.getTime()).toString();
        return returnstamp;
    }

    //Returns the timestamp as timestamp object
    public Timestamp getTimestampObject() {
        return new Timestamp(timestamp.getTime());
    }
}
