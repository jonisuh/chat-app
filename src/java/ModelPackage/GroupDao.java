/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Joni
 */
public class GroupDao {
    private TreeMap<Integer,Group> allgroups;
    private TreeMap<Integer,Message> allmessages;
    private final UserDao userdao;
    
    
    private GroupDao(){
        /*
        this.allgroups = loadGroups();
        this.allmessages = loadMessages();
        */
        this.allgroups = loadGroups();
        this.allmessages = loadMessages();
        this.userdao = UserDao.getInstance();
    }
    
    public static GroupDao getInstance() {
       return GroupSingleton.INSTANCE;
    }
    private static class GroupSingleton {
        private static final GroupDao INSTANCE = new GroupDao();
    }

    private void saveGroups(){
        try {
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/groups.ser");
                File f = new File(url.toURI());
                FileOutputStream out = new FileOutputStream(f);
                ObjectOutputStream obout = new ObjectOutputStream(out);
                obout.writeObject(this.allgroups);
                obout.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open groups.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error writing into file");
                e.printStackTrace();
        }catch (URISyntaxException e) {
                 e.printStackTrace();
        }
    }
    
    private TreeMap<Integer, Group> loadGroups(){
        TreeMap<Integer,Group> groupmap = null;
        try {
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/groups.ser");
                File f = new File(url.toURI());
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream obin = new ObjectInputStream(in);
                groupmap = (TreeMap<Integer,Group>)obin.readObject();
                obin.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open groups.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
                return new TreeMap<Integer,Group>();
        } catch (ClassNotFoundException e) {
                System.out.println("Error reading object");
                e.printStackTrace();
        }catch (URISyntaxException e) {
                 e.printStackTrace();
        }
        return groupmap;
    }
    private void saveMessages(){
        try {
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/messages.ser");
                File f = new File(url.toURI());
                FileOutputStream out = new FileOutputStream(f);
                ObjectOutputStream obout = new ObjectOutputStream(out);
                obout.writeObject(this.allmessages);
                obout.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open messages.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error writing into file");
                e.printStackTrace();
        } catch (URISyntaxException e) {
                 e.printStackTrace();
        }
    }
    
    private TreeMap<Integer, Message> loadMessages(){
        TreeMap<Integer,Message> messagemap = null;
        try {
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/messages.ser");
                File f = new File(url.toURI());
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream obin = new ObjectInputStream(in);
                messagemap = (TreeMap<Integer,Message>)obin.readObject();
                obin.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open messages.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
                return new TreeMap<Integer,Message>();
        } catch (ClassNotFoundException e) {
                System.out.println("Error reading object");
                e.printStackTrace();
        }catch (URISyntaxException e) {
                 e.printStackTrace();
        }
        return messagemap;
    }
    
    /*
    Creates a new group and puts the user who created the group in the admin and user list
    */
    public int createGroup(String groupname, User starter){
        if(groupname.length() <= 16 && groupname.length() > 0 && groupname != null && !groupname.equals(" ")){
            System.out.println("New group created");
            int groupID ;
            if(this.allgroups.isEmpty()){
                groupID = 0;
            }else{
                groupID = allgroups.lastKey() + 1;
            }
            Group g = new Group(groupname, groupID);
            allgroups.put(groupID, g);
            g.addUser(starter);
            g.addAdmin(starter);
            starter.addGroup(g);
            saveGroups();
            userdao.saveUsers();
            return groupID;
        }else{
            return 0;
        }
        
    }
    /*
    Adds user to groups userlist and the group to users grouplist
    */
    public void addUserToGroup(int groupID, User u){
        Group g = allgroups.get(groupID);
        g.addUser(u);
        u.addGroup(g);
        
        userdao.saveUsers();
        saveGroups();
    }
    /*
    This method removes a user from a group specified by the groupID and then removes the group from the users group list
    Additionally if the user is an admin in the group it also removes the user from the groups admin list
    */
    public void removeUserFromGroup(int groupID, User u){
       Group g = allgroups.get(groupID);
       g.removeUser(u.getUserID());
       u.removeGroup(g.getGroupID());
       if(g.getGroupAdmins().containsKey(u.getUserID())){
           g.removeAdmin(u.getUserID());
       }
       saveGroups();
       userdao.saveUsers();
    }
    
    public void promoteToAdmin(int groupID, User u){
        Group g = allgroups.get(groupID);
        if(g.getUserlist().containsValue(u)){
            g.addAdmin(u);
        }
        saveGroups();
        userdao.saveUsers();
    }
    public void demoteFromADmin(int groupID, User u){
        Group g = allgroups.get(groupID);
        if(g.getGroupAdmins().containsValue(u)){
            g.removeAdmin(u.getUserID());
        }
    }
    public String updateGroup(int groupID, String groupname){
        if(groupname.length() <= 16 && groupname.length() > 0 && groupname != null && !groupname.equals(" ")){
            Group g = allgroups.get(groupID);
            g.setGroupName(groupname);
            
            saveGroups();
            userdao.saveUsers();
            return "Groupname updated.";
        }else{
            return "Failed";
        }
        
    }
    /*
    Return all the groups
    */
    public TreeMap<Integer, Group> getGroups(){
        
        return allgroups;
    }
    /*
    Returns a group specified by the group id
    */
    public Group getGroup(int groupID){
        return allgroups.get(groupID);
    }
    /*
    Creates a new message and saves it into users and a groups history
    */
    public void createMessage(int userID, int groupID, String msg){
        if(msg.length() <= 500 && msg.length() > 0 && !msg.equals("") && msg != null){
            Date timestamp = new Date();
            int messageID;
            if(this.allmessages.isEmpty()){
                messageID = 0;
            }else{
                messageID = allmessages.lastKey() + 1;
            }
            
        
            
            Message message = new Message(userID, groupID, messageID, msg, timestamp);

            userdao.getUser(userID).addMessage(message);
            allgroups.get(groupID).addMessage(message);
            allmessages.put(messageID, message);
            System.out.println("Created message "+message.getMessageID()+" into group "+message.getGroupID()+" by user "+message.getUserID()+" MSG: "+message.getMessage());

            saveGroups();
            userdao.saveUsers();
            saveMessages();
        }
    }
    /*
    public HashMap<Integer, Message> getGroupMessages(int groupID){
        return allgroups.get(groupID).getGroupmessages();
    }
    */
}
