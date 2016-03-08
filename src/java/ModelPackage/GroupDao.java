/*
 This is the GroupDao class. It is in charge of creating, reading, updating and deleting Group objects.
The class is also in charge of creating and reading message objects.

This class implements the singleton design pattern

GroupDao has two ConcurrentSkipListMaps that contain all the groups in the system and all the messages in the system.
ConcurrentSkipListMap is used for a thread safe collection that acts like a TreeMap.

When the object is initialized, the object makes two method calls to load the saved objects from files and saves them to
the ConcurrentSkipListMaps.

This class also uses an instance of the UserDao, mainly for saving the users to the file whenever a group is updated.

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
import java.util.concurrent.ConcurrentSkipListMap;

public class GroupDao {

    private ConcurrentSkipListMap<Integer, Group> allgroups;
    private ConcurrentSkipListMap<Integer, Message> allmessages;
    private final UserDao userdao;

    private GroupDao() {
        this.allgroups = loadGroups();
        this.allmessages = loadMessages();
        this.userdao = UserDao.getInstance();
    }

    //Singleton
    public static GroupDao getInstance() {
        return GroupSingleton.INSTANCE;
    }

    private static class GroupSingleton {

        private static final GroupDao INSTANCE = new GroupDao();
    }

    //Saves the map containing all groups to a groups.ser file in the modelpackage
    private void saveGroups() {
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Loads the ConcurrentSkipListMap containing all groups from the groups.ser file
    private ConcurrentSkipListMap<Integer, Group> loadGroups() {
        ConcurrentSkipListMap<Integer, Group> groupmap = null;
        try {
            URL url = this.getClass().getClassLoader().getResource("ModelPackage/groups.ser");
            File f = new File(url.toURI());
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream obin = new ObjectInputStream(in);
            groupmap = (ConcurrentSkipListMap<Integer, Group>) obin.readObject();
            obin.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open groups.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file");
            return new ConcurrentSkipListMap<Integer, Group>();
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading object");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return groupmap;
    }

    //Saves the map containing all messages to a messages.ser file in the modelpackage
    private void saveMessages() {
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Loads the ConcurrentSkipListMap containing all messages from the messages.ser file
    private ConcurrentSkipListMap<Integer, Message> loadMessages() {
        ConcurrentSkipListMap<Integer, Message> messagemap = null;
        try {
            URL url = this.getClass().getClassLoader().getResource("ModelPackage/messages.ser");
            File f = new File(url.toURI());
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream obin = new ObjectInputStream(in);
            messagemap = (ConcurrentSkipListMap<Integer, Message>) obin.readObject();
            obin.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open messages.ser");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file");
            return new ConcurrentSkipListMap<Integer, Message>();
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading object");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return messagemap;
    }

    /*
    Creates a new group, adds it to the allgroups map, puts the group started User object in the newly created groups
    user and admin HashMaps, and finally saves the objects to the .ser file
     */
    public int createGroup(String groupname, User starter) {
        if (groupname.length() <= 50 && groupname.length() > 0 && groupname != null && !groupname.equals(" ")) {
            
            int groupID;
            if (this.allgroups.isEmpty()) {
                groupID = 0;
            } else {
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
        } else {
            return 0;
        }

    }

    /*
    Adds a user object into a group objects user HashMap and vice versa, adds the group object into the user objects group HashMap
     */
    public void addUserToGroup(int groupID, User u) {
        Group g = allgroups.get(groupID);
        g.addUser(u);
        u.addGroup(g);

        userdao.saveUsers();
        saveGroups();
    }

    /*
    This method removes an user from a group specified by the groupID and then removes the group from the users group map
    Additionally if the user is an admin in the group it also removes the user from the groups admin map
     */
    public void removeUserFromGroup(int groupID, int userID) {
        Group g = allgroups.get(groupID);
        User user = userdao.getUser(userID);

        g.removeUser(user.getUserID());
        user.removeGroup(g.getGroupID());

        if (g.getGroupAdmins().containsKey(user.getUserID())) {
            g.removeAdmin(user.getUserID());
        }
        saveGroups();
        userdao.saveUsers();
    }

    //Puts a user object in to the groups admin HashMap
    public void promoteToAdmin(int groupID, int userID) {
        Group g = allgroups.get(groupID);
        User u = userdao.getUser(userID);
        if (g.getUserlist().containsKey(userID)) {
            g.addAdmin(u);
        }
        saveGroups();
        userdao.saveUsers();
    }

    //Removes a user from the admin HashMap
    public void demoteFromAdmin(int groupID, User u) {
        Group g = allgroups.get(groupID);
        if (g.getGroupAdmins().containsValue(u)) {
            g.removeAdmin(u.getUserID());
        }
    }

    //Updates an existing group and changes its name
    public String updateGroup(int groupID, String groupname) {
        if (groupname.length() <= 16 && groupname.length() > 0 && groupname != null && !groupname.equals(" ")) {
            Group g = allgroups.get(groupID);
            g.setGroupName(groupname);

            saveGroups();
            userdao.saveUsers();
            return "Groupname updated.";
        } else {
            return "Failed";
        }

    }

    /*
    Return all the groups
     */
    public ConcurrentSkipListMap<Integer, Group> getGroups() {
        return allgroups;
    }

    /*
    Returns a group specified by the group id
     */
    public Group getGroup(int groupID) {
        return allgroups.get(groupID);
    }

    /*
    Creates a new message and saves it into users and a groups history
     */
    public void createMessage(int userID, int groupID, String msg) {
        if (msg.length() <= 500 && msg.length() > 0 && !msg.equals("") && msg != null) {
            //Gets the current date and time
            Date timestamp = new Date();
            int messageID;
            //Calculates the unique messageID
            if (this.allmessages.isEmpty()) {
                messageID = 0;
            } else {
                messageID = allmessages.lastKey() + 1;
            }

            Message message = new Message(userID, groupID, messageID, msg, timestamp);

            userdao.getUser(userID).addMessage(message);
            allgroups.get(groupID).addMessage(message);
            allmessages.put(messageID, message);

            saveGroups();
            userdao.saveUsers();
            saveMessages();
        }
    }

}
