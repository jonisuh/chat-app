
package ModelPackage;

import java.io.Serializable;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "group")
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<Integer,User> userlist;
    private HashMap<Integer,Message> groupmessages;
    private int groupID;
    private String groupname;
    private HashMap<Integer,User> groupadmins;
    
    public Group(){
        
    }
    
    public Group(String groupname, int groupID){
        this.userlist = new HashMap<Integer, User>();
        this.groupmessages = new HashMap<Integer, Message>();
        this.groupadmins = new HashMap<Integer, User>();
        this.groupname = groupname;
        this.groupID = groupID;
    }
    
    //USER FUNCTIONS
    public HashMap<Integer,User> getUserlist() {
        return userlist;
    }
    public void addUser(User u){
        userlist.put(u.getUserID(),u);
    }
    public void removeUser(int userID){
        userlist.remove(userID);
    }
    
    //MESSAGE FUNCTIONS
    public HashMap<Integer,Message> getGroupmessages() {
        return groupmessages;
    }
    public void addMessage(Message msg){
        groupmessages.put(msg.getMessageID(),msg);
    }         
    
    //ADMIN FUNCTIONS
    public HashMap<Integer,User> getGroupAdmins() {
        return groupadmins;
    }
    public void addAdmin(User u){
        groupadmins.put(u.getUserID(),u);
    }
    public void removeAdmin(int userID){
        groupadmins.remove(userID);
    }

    @XmlElement
    public String getGroupName() {
        return groupname;
    }

    public void setGroupName(String groupName) {
        this.groupname = groupName;
    }
    @XmlElement
    public int getGroupID() {
        return groupID;
    }

}
