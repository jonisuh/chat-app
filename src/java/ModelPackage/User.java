/*
This is the User Data class. It contains information about the user as instance variables.

The class has a HashMap that contains all the groups the user is part of as well as a HashMap that contains
all the messages the user has sent.

Every instance variable that describes the user information apart from the userID can be changed with set methods.

This class also imports javax xml annotations because it is used in UsersResource to parse XML.

 */
package ModelPackage;

import java.io.Serializable;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<Integer, Group> grouplist;
    private HashMap<Integer, Message> usermessages;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String department;
    private String title;
    private String email;
    private int userID;

    public User() {

    }

    public User(String username, String password, String firstname, String lastname, String department, String title, String email, int userID) {
        this.usermessages = new HashMap<Integer, Message>();
        this.grouplist = new HashMap<Integer, Group>();
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.department = department;
        this.title = title;
        this.email = email;
        this.userID = userID;
    }

    //Group functions
    public HashMap<Integer, Group> getGrouplist() {
        return grouplist;
    }

    public void addGroup(Group grp) {
        grouplist.put(grp.getGroupID(), grp);
    }

    public void removeGroup(int groupID) {
        grouplist.remove(groupID);
    }

    //Message functions
    public HashMap<Integer, Message> getUsermessages() {
        return usermessages;
    }

    public void addMessage(Message msg) {
        usermessages.put(msg.getMessageID(), msg);
    }

    //GET / SET Methods
    @XmlElement
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String fname) {
        this.firstname = fname;
    }

    @XmlElement
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @XmlElement
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    //Password uses XmlTransient annotation because we dont want to parse it as XML
    @XmlTransient
    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement
    public int getUserID() {
        return userID;
    }

}
