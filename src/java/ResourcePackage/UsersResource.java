
package ResourcePackage;

import ModelPackage.Group;
import ModelPackage.Message;
import ModelPackage.User;
import ModelPackage.UserDao;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    public String registerNewUser(@FormParam("name") String name, @FormParam("password") String password) {
        return userdao.createUser(name, password);
        //return Response.status(200).entity("addUser is called, name : " +  + ", age : " + ).build();
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
    public User getUserXML(@PathParam("userid") int userid) {
        if(userdao.getUsers().containsKey(userid)){
            return userdao.getUser(userid);
        }else{
            return null;
        }
    }
    
    @PUT
    @Path("/{userid}")
    public Response updateUser(@PathParam("userid") int userid,@FormParam("name") String name) {
        userdao.updateUser(userid, name);
        return Response.status(200).entity("Updated user"+userid+" username to "+name).build();
    }
    
    @DELETE
    @Path("/{userid}")
    public Response deleteUser(@PathParam("userid") int userid) {
        userdao.deleteUser(userid);
        return Response.status(200).entity("User "+userid+" deleted").build();
    }
    
    @GET
    @Path("/{userid}/messages")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Message> getUserMessages(@PathParam("userid") int userid) {
        ArrayList returnarray = new ArrayList<Message>();
        for(Map.Entry<Integer,Message> entry : userdao.getUser(userid).getUsermessages().entrySet()){
            returnarray.add(entry.getValue());
        }        
        return returnarray;
    }
    
    @GET
    @Path("/{userid}/groups")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Group> getUserGroups(@PathParam("userid") int userid) {
        ArrayList returnarray = new ArrayList<Group>();
        for(Map.Entry<Integer,Group> entry : userdao.getUser(userid).getGrouplist().entrySet()){
            returnarray.add(entry.getValue());
        }        
        return returnarray;
    }
    
    @GET
    @Path("/{userid}/name")
    @Produces(MediaType.APPLICATION_XML)
    public String getUserName(@PathParam("userid") int userid) {
        return userdao.getUser(userid).getUsername();
    }
    
    @POST
    @Path("/login")
    public Response loginUser(@FormParam("name") String name, @FormParam("password") String password, @Context HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        boolean authResult = userdao.authenticateUser(name,password);
        if(authResult){
                session.setAttribute("username", name);
                session.setAttribute("id", userdao.getUserByName(name).getUserID());
                session.setAttribute("status", "authenticated");
            return Response.status(200).entity(session.getAttribute("id")).build();
        }else{
            return Response.status(401).entity("Invalid login.").build();
        }
        
    }
    
    @POST
    @Path("/logout")
    public Response logoutUser(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.invalidate();
        return Response.status(200).entity("User logged out").build();
    }
    
}
