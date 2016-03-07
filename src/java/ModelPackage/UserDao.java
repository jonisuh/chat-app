
package ModelPackage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.xml.bind.DatatypeConverter;



public class UserDao {
    private ConcurrentSkipListMap<Integer,User> allusers;
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
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/users.ser");
                File f = new File(url.toURI());
                FileOutputStream out = new FileOutputStream(f);
                ObjectOutputStream obout = new ObjectOutputStream(out);
                obout.writeObject(this.allusers);
                obout.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open users.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error writing into file");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /*
    Loads users from user.ser
    */
    public ConcurrentSkipListMap loadUsers() {
        
        ConcurrentSkipListMap<Integer,User> usermap = null;
        try {
                URL url = this.getClass().getClassLoader().getResource("ModelPackage/users.ser");
                File f = new File(url.toURI());
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream obin = new ObjectInputStream(in);
                usermap = (ConcurrentSkipListMap<Integer,User>)obin.readObject();
                obin.close();
        } catch (FileNotFoundException e) {
                System.out.println("Could not open users.ser");
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("Error reading file");
                return new ConcurrentSkipListMap<Integer,User>();
        } catch (ClassNotFoundException e) {
                System.out.println("Error reading object");
                e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return usermap;
    }
    /*
    Registers a new user.
    */
    public Boolean createUser(String username, String password, String firstname, String lastname,String department, String title, String email){
        boolean validinformation = true;
        
        if(username.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
        if(!username.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("Name is not alphanumeric");
        }
        if(username.length() < 0 || username == null ||username.equals(" ") ||username.equals("")){
            validinformation = false;
           System.out.println("Username is empty.");
        }
        //Checking if username exists
       
        
        for(Map.Entry<Integer,User> entry : this.allusers.entrySet()){
            if(entry.getValue().getUsername().equals(username)){
                validinformation = false;
                System.out.println("Name already exists");
            }
        }
        
        if(password.length() > 16){
            validinformation = false;
            System.out.println("Password is too long.");
        }
        
        if(password.length() < 0 || password == null ||password.equals(" ") || password.equals("")){
            validinformation = false;
            System.out.println("Password is empty.");
        }
        //Validating firstname
        
        if(firstname.length() < 0 || firstname == null ||firstname.equals(" ") ||firstname.equals("")){
            validinformation = false;
            System.out.println("First name is empty.");
        }
        if(!firstname.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("First name is not alphanumeric");
        }
         if(firstname.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
        
         //Validating lastname
        if(lastname.length() < 0 || lastname == null ||lastname.equals(" ") ||lastname.equals("")){
            validinformation = false;
            System.out.println("Last name is empty.");
        }
        if(!lastname.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("Last name is not alphanumeric");
        }
         if(lastname.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
         
          //Validating lastname
        if(department.length() < 0 || department == null ||department.equals(" ") ||department.equals("")){
            validinformation = false;
            System.out.println("Department name is empty.");
        }
        if(!department.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("Department name is not alphanumeric");
        }
         if(department.length() > 20){
            validinformation = false;
            System.out.println("Department is too long.");
        }
         
         //Validating email
         if(email.length() < 0 || email == null ||email.equals(" ") ||email.equals("")){
            validinformation = false;
            System.out.println("Email is empty.");
        }
         if(email.length() > 30){
            validinformation = false;
            System.out.println("Email is too long.");
        }
         
         
        if(validinformation){
            int userID;
            if(this.allusers.isEmpty()){
                userID = 0;
            }else{
                userID = this.allusers.lastKey() + 1;
            }
            String cryptedpassword = encryptPassword(password);
            
            User u = new User(username,cryptedpassword,firstname,lastname,department,title,email,userID);
            this.allusers.put(userID,u);
            saveUsers();
            System.out.println("User created with userID: "+userID);
        }
        return validinformation;
    }
    //Get all users
    public ConcurrentSkipListMap<Integer, User> getUsers(){
        return this.allusers;
    }
    
    //Get one user
    public User getUser(int userID){
        return allusers.get(userID);
    }
    /*
    Updates user values.
    */
    public Boolean updateUser(int userID, String username,String firstname, String lastname,String department, String title, String email){
        User u = allusers.get(userID);
        
        boolean validinformation = true;
        
        if(username.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
        if(!username.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("Name is not alphanumeric");
        }
        if(username.length() < 0 || username == null ||username.equals(" ") ||username.equals("")){
            validinformation = false;
           System.out.println("Username is empty.");
        }
        //Checking if username exists
       
        
        for(Map.Entry<Integer,User> entry : this.allusers.entrySet()){
            if(entry.getValue().getUsername().equals(username) && entry.getValue().getUserID() != userID){
                validinformation = false;
                System.out.println("Name already exists");
            }
        }
        
        //Validating firstname
        
        if(firstname.length() < 0 || firstname == null ||firstname.equals(" ") ||firstname.equals("")){
            validinformation = false;
            System.out.println("First name is empty.");
        }
        if(!firstname.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("First name is not alphanumeric");
        }
         if(firstname.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
        
         //Validating lastname
        if(lastname.length() < 0 || lastname == null ||lastname.equals(" ") ||lastname.equals("")){
            validinformation = false;
            System.out.println("Last name is empty.");
        }
        if(!lastname.matches("^[a-zA-Z0-9]*$")){
            validinformation = false;
            System.out.println("Last name is not alphanumeric");
        }
         if(lastname.length() > 16){
            validinformation = false;
            System.out.println("Name is too long.");
        }
         
         //Validating email
         if(email.length() < 0 || email == null ||email.equals(" ") ||email.equals("")){
            validinformation = false;
            System.out.println("Email is empty.");
        }
         if(email.length() > 30){
            validinformation = false;
            System.out.println("Email is too long.");
        }
         
         
        if(validinformation){
            System.out.println(u.getUsername()+" "+u.getFirstname()+" "+u.getLastname()+" "+u.getEmail());
            u.setUsername(username);
            u.setFirstname(firstname);
            u.setLastname(lastname);
            u.setEmail(email);
            
            System.out.println(u.getUsername()+" "+u.getFirstname()+" "+u.getLastname()+" "+u.getEmail());
            saveUsers();
        }
        return validinformation;
        
    }
    public boolean updatePassword(int userID, String password){
        boolean validpassword = true;
        
        if(password.length() > 16){
            validpassword = false;
            System.out.println("Password is too long.");
        }
        
        if(password.length() < 0 || password == null ||password.equals(" ") || password.equals("")){
            validpassword = false;
            System.out.println("Password is empty.");
        }
        if(validpassword){
            User u = allusers.get(userID);
            String encryptedPassword = encryptPassword(password);
            u.setPassword(encryptedPassword);
        }
        
        return validpassword;
    }
    //Delete user record
    public void deleteUser(int userID){
        allusers.remove(userID);
        saveUsers();
    }
    /*
    Authenticates user login
    */
    
    public boolean authenticateUser(String authCred){
        if(authCred == null){
            return false;
        }

        final String encodedUserPassword = authCred.replaceFirst("Basic"+" ", "");
        
        String userCredentials = null;
        try{
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedUserPassword);
            userCredentials = new String(decodedBytes, "UTF-8");
        }catch(IOException e){
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(userCredentials,":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        
        User u = getUserByName(username);
        if (u == null){
            return false;
        }
        String cryptedPassword = encryptPassword(password);
        boolean authStatus = username.equals(u.getUsername()) && cryptedPassword.equals(u.getPassword());
       // boolean authStatus = "test".equals(username) && "test".equals(password);

        return authStatus;
    }
    
    public String decodeUsername(String encodedUser){
        if(encodedUser == null){
            return null;
        }

        final String encodedUserPassword = encodedUser.replaceFirst("Basic"+" ", "");
        
        String userCredentials = null;
        try{
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedUserPassword);
            userCredentials = new String(decodedBytes, "UTF-8");
        }catch(IOException e){
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(userCredentials,":");
        final String username = tokenizer.nextToken();
        
        return username;
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
