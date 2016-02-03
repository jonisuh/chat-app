/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

/**
 *
 * @author Joni
 */
public class GroupDao {
    private TreeMap<Integer,Group> allgroups;
    private final UserDao userdao;
    //private TreeMap<Integer, String> usernames;
    
    private GroupDao(){
        //this.allgroups = loadGroups();
        this.allgroups = loadGroups();
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
                FileOutputStream out = new FileOutputStream("groups.ser");
                ObjectOutputStream obout = new ObjectOutputStream(out);
                obout.writeObject(this.allgroups);
                obout.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open groups.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error writing into file");
                e.printStackTrace();
        }
    }
    
    private TreeMap<Integer, Group> loadGroups(){
        TreeMap<Integer,Group> groupmap = null;
        try {
                FileInputStream in = new FileInputStream("groups.ser");
                ObjectInputStream obin = new ObjectInputStream(in);
                groupmap = (TreeMap<Integer,Group>)obin.readObject();
                obin.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open groups.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
                System.out.println("Error reading object");
                e.printStackTrace();
        }
        return groupmap;
    }
    /*
    Creates a new group and puts the user who created the group in the admin and user list
    */
    public void createGroup(String groupname, User starter){
        int groupID = allgroups.lastKey() + 1;
        Group g = new Group(groupname, groupID);
        allgroups.put(groupID, g);
        g.addUser(starter);
        g.addAdmin(starter);
        starter.addGroup(g);
        saveGroups();
        userdao.saveUsers();
        //DEBUG
        System.out.println("Group "+g.getGroupName()+" created. User "+starter.getUsername()+" added as admin");
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
}
