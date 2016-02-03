
package ModelPackage;
import java.io.Serializable;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<Integer,Group> grouplist;
    private HashMap<Integer,Message> usermessages;
    private String username;
    private String password;
    private int userID;
    
    public User(){
        
    }
    
    public User(String username, String password, int userID){
        this.usermessages = new HashMap<Integer, Message>();
        this.grouplist = new HashMap<Integer, Group>();
        this.username = username;
        this.password = password;
        this.userID = userID;
    }
    
    //GROUP FUNCTIONS
    public HashMap<Integer,Group> getGrouplist() {
        return grouplist;
    }
    public void addGroup(Group grp){
        grouplist.put(grp.getGroupID(),grp);
    }
    public void removeGroup(int groupID){
        grouplist.remove(groupID);
    }
    
    //MESSAGE FUNCTIONS
    public HashMap<Integer,Message> getUsermessages() {
        return usermessages;
    }
    public void addMessage(Message msg){
        usermessages.put(msg.getMessageID(), msg);
    }
    @XmlElement
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }
    @XmlElement
    public int getUserID() {
        return userID;
    }
    
}
