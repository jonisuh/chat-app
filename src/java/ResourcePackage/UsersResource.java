
package ResourcePackage;

import ModelPackage.Group;
import ModelPackage.Message;
import ModelPackage.User;
import ModelPackage.UserDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Users")
public class UsersResource {
    private final UserDao userdao;
    @Context
    private UriInfo context;

    public UsersResource() {
        userdao = UserDao.getInstance();
    }

    
    @POST
    @Produces("text/plain")
    public Response registerNewUser(@FormParam("name") String name, @FormParam("password") String password
            , @FormParam("firstname") String firstname, @FormParam("lastname") String lastname,  @FormParam("department") String department, @FormParam("title") String title,@FormParam("email") String email) {
        boolean userCreationResult = userdao.createUser(name, password,firstname,lastname,department,title,email);
        
        if(userCreationResult){
            return Response.status(200).entity("New user created").build(); 
        }else{
            return Response.status(400).entity("User couldn't be created because of invalid data").build(); 
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<User> getUsersXML() {
        ArrayList returnarray = new ArrayList<User>();
        for(Map.Entry<Integer,User> entry : userdao.getUsers().entrySet()){
            returnarray.add(entry.getValue());
        }        
        return returnarray;
    }
    
    @GET
    @Path("/{userid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserXML(@PathParam("userid") int userid) {
        
        if(userdao.getUsers().containsKey(userid)){
           return Response.status(200).entity(userdao.getUser(userid)).build();  
        }else{
            return Response.status(404).entity("User not found").build();
        }
    }
    
    @PUT
    @Path("/{userid}")
    public Response updateUser(@PathParam("userid") int userid,@FormParam("username") String username,
            @FormParam("firstname") String firstname,@FormParam("lastname") String lastname, @FormParam("department") String department,@FormParam("title") String title, @FormParam("email") String email, @Context HttpHeaders headers) {
        System.out.println("test");
        if(userdao.getUsers().containsKey(userid)){
            if(headers.getRequestHeaders().keySet().contains("authorization")){
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                
                if(authResult && myID == userid){
                    System.out.println("NEw values"+userid+" "+username+" "+firstname+" "+lastname+" "+email);
                    boolean updateStatus = userdao.updateUser(userid, username,firstname,lastname,department,title,email);
                    User u = userdao.getUser(userid);
                    System.out.println("gr: "+u.getUsername()+" "+u.getFirstname()+" "+u.getLastname()+" "+u.getEmail());
                    if(updateStatus){
                        return Response.status(200).entity("Updated user"+userid).build();
                    }else{
                        return Response.status(400).entity("Values were invalid.").build();
                    }
                }else{
                   return Response.status(401).entity("Authorization failed.").build(); 
                }
            }else{
                return Response.status(401).entity("Authorization failed.").build();
            }
        }else{
           return Response.status(404).entity("User not found").build();
        }
    }
    @PUT
    @Path("/{userid}/password")
    public Response updateUserPassword(@PathParam("userid") int userid,@FormParam("password") String password, @Context HttpHeaders headers) {
        
        if(userdao.getUsers().containsKey(userid)){
            if(headers.getRequestHeaders().keySet().contains("authorization")){
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                
                if(authResult && myID == userid){
                    
                    boolean updateStatus = userdao.updatePassword(userid, password);
                    if(updateStatus){
                        return Response.status(200).entity("Updated user"+userid+" password.").build();
                    }else{
                        return Response.status(400).entity("New password was invalid.").build();
                    }
                }else{
                   return Response.status(401).entity("Authorization failed.").build(); 
                }
            }else{
                return Response.status(401).entity("Authorization failed.").build();
            }
        }else{
           return Response.status(404).entity("User not found").build();
        }
    }
    
    @DELETE
    @Path("/{userid}")
    public Response deleteUser(@PathParam("userid") int userid, @Context HttpHeaders headers) {
       if(userdao.getUsers().containsKey(userid)){
            if(headers.getRequestHeaders().keySet().contains("authorization")){
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if(authResult && myID == userid){
                    userdao.deleteUser(userid);
                    return Response.status(200).entity("User "+userid+" deleted").build(); 
                }else{
                   return Response.status(401).entity("Authorization failed.").build(); 
                }
            }else{
                return Response.status(401).entity("Authorization failed.").build();
            }
        }else{
            return Response.status(404).entity("User not found").build();
        }   
    }
    
    @GET
    @Path("/{userid}/messages")
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserMessages(@PathParam("userid") int userid, @Context HttpHeaders headers) {
        if(userdao.getUsers().containsKey(userid)){
            if(headers.getRequestHeaders().keySet().contains("authorization")){
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if(authResult && myID == userid){
                    ArrayList returnarray = new ArrayList<Message>();
                    for(Map.Entry<Integer,Message> entry : userdao.getUser(userid).getUsermessages().entrySet()){
                        returnarray.add(entry.getValue());
                    }      
                    
                    //Making the returnarray a generic entity so that it can be passed to client with the http response          
                    GenericEntity<List<Message>> entity = new GenericEntity<List<Message>>(returnarray) {};
                    return Response.status(200).entity(entity).build(); 
                    
                }else{
                   return Response.status(401).entity("Authorization failed.").build(); 
                }
            }else{
                return Response.status(401).entity("Authorization failed.").build();
            }
        }else{
            return Response.status(404).entity("User not found").build();
        } 
        
    }
    
    @GET
    @Path("/{userid}/groups")
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserGroups(@PathParam("userid") int userid, @Context HttpHeaders headers) {
        
         if(userdao.getUsers().containsKey(userid)){
            if(headers.getRequestHeaders().keySet().contains("authorization")){
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if(authResult && myID == userid){ 
                    ArrayList returnarray = new ArrayList<Group>();
                    for(Map.Entry<Integer,Group> entry : userdao.getUser(userid).getGrouplist().entrySet()){
                        returnarray.add(entry.getValue());
                    }
                    //Making the returnarray a generic entity so that it can be passed to client with the http response
                    
                    GenericEntity<List<Group>> entity = new GenericEntity<List<Group>>(returnarray) {};
                  
                    return Response.status(200).entity(entity).build(); 
                    
                }else{
                   return Response.status(401).entity("Authorization failed.").build(); 
                }
            }else{
                return Response.status(401).entity("Authorization failed.").build();
            }
        }else{
            return Response.status(404).entity("User not found").build();
        }    
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_XML)
    public Response loginUser(@Context HttpHeaders headers) {
        String authCredentials = headers.getRequestHeader("authorization").get(0);
        
        boolean authResult = userdao.authenticateUser(authCredentials);
        if(authResult){
            int userID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
            ReturnInformation returninfo = new ReturnInformation(userID,authCredentials);
            return Response.status(200).entity(returninfo).build();
        }else{
            return Response.status(401).entity("Invalid login.").build();
        }
        
    }
}
