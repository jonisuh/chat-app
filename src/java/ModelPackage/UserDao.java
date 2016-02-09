
package ModelPackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;



public class UserDao {
    private TreeMap<Integer,User> allusers;
    //private TreeMap<Integer, String> usernames;
    
    private UserDao(){
        this.allusers = loadUsers();
    }
    
    public static UserDao getInstance() {
       return UserSingleton.INSTANCE;
    }
    private static class UserSingleton {
        private static final UserDao INSTANCE = new UserDao();
    }
    /*
    Saves users to users.ser
    */
    public void saveUsers(){
        try {
                FileOutputStream out = new FileOutputStream("users.ser");
                ObjectOutputStream obout = new ObjectOutputStream(out);
                obout.writeObject(this.allusers);
                obout.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open users.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error writing into file");
                e.printStackTrace();
        }
    }
    /*
    Loads users from user.ser
    */
    public TreeMap loadUsers(){
        TreeMap<Integer,User> usermap = null;
        try {
                FileInputStream in = new FileInputStream("users.ser");
                ObjectInputStream obin = new ObjectInputStream(in);
                usermap = (TreeMap<Integer,User>)obin.readObject();
                obin.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open users.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
                System.out.println("Error reading object");
                e.printStackTrace();
        }
        return usermap;
    }
    /*
    Registers a new user.
    */
    public String createUser(String username, String password){
        boolean validinformation = true;
        String returnstring = "";
        if(username.length() > 16){
            validinformation = false;
            returnstring = "Name is too long.";
        }
        if(!username.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            returnstring = returnstring + "\nName is not alphanumeric";
        }
        if(username.length() < 0 || username == null ||username.equals(" ") ||username.equals("")){
            validinformation = false;
            returnstring = returnstring + "Username is empty.";
        }
        //Checking if username exists
        for(Map.Entry<Integer,User> entry : this.allusers.entrySet()){
            if(entry.getValue().getUsername().equals(username)){
                validinformation = false;
                returnstring = returnstring + "\nName already exists";
            }
        }
        
        if(password.length() > 16){
            validinformation = false;
            returnstring = returnstring + "\nPassword is too long.";
        }
        
        if(password.length() < 0 || password == null ||password.equals(" ") || password.equals("")){
            validinformation = false;
            returnstring = returnstring + "\nPassword is empty.";
        }
        
        if(validinformation){
            int userID = this.allusers.lastKey() + 1;
            String cryptedpassword = encryptPassword(password);
            User u = new User(username,cryptedpassword,userID);
            this.allusers.put(userID,u);
            saveUsers();
            returnstring = "User created with userID: "+userID;
        }
        return returnstring;
    }
    //Get all users
    public TreeMap<Integer, User> getUsers(){
        return this.allusers;
    }
    
    //Get one user
    public User getUser(int userID){
        return allusers.get(userID);
    }
    /*
    Updates user values.
    */
    public void updateUser(int userID, String name){
        allusers.get(userID).setUsername(name);
        saveUsers();
    }
    //Delete user record
    public void deleteUser(int userID){
        allusers.remove(userID);
        saveUsers();
    }
    /*
    Authenticates user login
    */
    public boolean authenticateUser(String username, String password){
        User u = null;
        
        for(Map.Entry<Integer,User> entry : this.allusers.entrySet()){
            if(entry.getValue().getUsername().equals(username)){
                u = entry.getValue();
            }
        }
        if(u != null){
            String cryptedpassword = encryptPassword(password);
            if(u.getPassword().equals(cryptedpassword)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    /*
    Returns user by username
    */
    public User getUserByName(String username){
       for(Map.Entry<Integer,User> entry : this.allusers.entrySet()){
            if(entry.getValue().getUsername().equals(username)){
                return entry.getValue();
            }
        }
       return null;
    }
    /*
    Encryption method used by registration and authentication
    */
    private String encryptPassword(String password){
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
