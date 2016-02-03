/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ResourcePackage;

import ModelPackage.Group;
import ModelPackage.GroupDao;
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

@Path("Groups")
public class GroupsResource {
    private final GroupDao groupdao;
    private final UserDao userdao;
    @Context
    private UriInfo context;

    public GroupsResource() {
        groupdao = GroupDao.getInstance();
        userdao = UserDao.getInstance();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Group> getGroupsXML() {
        ArrayList returnarray = new ArrayList<Group>();
        for(Map.Entry<Integer,Group> entry : groupdao.getGroups().entrySet()){
            returnarray.add(entry.getValue());
        }        
        return returnarray;
    }
    
    @POST
    @Produces("text/plain")
    public String createGroup(@FormParam("groupname") String groupname,@FormParam("groupstarterID") int starterID) {
        if(userdao.getUsers().containsKey(starterID)){
            User groupstarter = userdao.getUser(starterID);
            groupdao.createGroup(groupname, groupstarter);
            return "Group created";
        }else{
            return "No user with that ID exists.";
        }
        //return Response.status(200).entity("addUser is called, name : " +  + ", age : " + ).build();
    }
    
    @GET
    @Path("/{groupid}")
    @Produces(MediaType.APPLICATION_XML)
    public Group getGroupXML(@PathParam("groupid") int groupid) {
        if(groupdao.getGroups().containsKey(groupid)){
            return groupdao.getGroup(groupid);
        }else{
            return null;
        }
    }
    
    @PUT
    @Path("/{groupid}")
    public Response updateGroup(@PathParam("groupid") int groupid) {
        return null;
        //TODO
    }
    
    @DELETE
    @Path("/{groupid}")
    public Response deleteGroup(@PathParam("groupid") int groupid) {
        return null;
        //TODO
    }
    
    @POST
    @Path("/{groupid}/users")
    public Response addUserToGroup(@PathParam("groupid") int groupID, @FormParam("userID") int userID) {
        if(groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)){
            if(!groupdao.getGroup(groupID).getUserlist().containsKey(userID)){
                groupdao.addUserToGroup(groupID, userdao.getUser(userID));
                return Response.status(200).entity("User "+userID+" added to group "+groupID).build();
            }else{
                return Response.status(409).entity("User "+userID+" is already in this group.").build(); 
            }
        }else{
            return Response.status(400).entity("Invalid group or user ID").build(); 
        }
    }
    
    @GET
    @Path("/{groupid}/users")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<User> getGroupUsers(@PathParam("groupid") int groupID) {
        if(groupdao.getGroups().containsKey(groupID)){
            Group g = groupdao.getGroup(groupID);
            ArrayList returnarray = new ArrayList<User>();
            for(Map.Entry<Integer,User> entry : g.getUserlist().entrySet()){
                returnarray.add(entry.getValue());
            }        
            //return Response.status(200).entity(returnarray).build();
            return returnarray;
        }else{
            //return Response.status(400).entity("Invalid group ID").build();
            return null;
        }
    }
    @DELETE
    @Path("/{groupid}/users/{userid}")
    public Response removeUserFromGroup(@PathParam("groupid") int groupID, @PathParam("userid") int userID) {
        if(groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)){
            groupdao.removeUserFromGroup(groupID,userdao.getUser(userID));
            return Response.status(200).entity("User removed from group.").build();
        }else{
            return Response.status(400).entity("Invalid group or user ID").build(); 
        }
    }
    @GET
    @Path("/{groupid}/users/{userid}")
    @Produces(MediaType.APPLICATION_XML)
    public User getGroupUser(@PathParam("groupid") int groupID, @PathParam("userid") int userID) {
        if(groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)){
            return groupdao.getGroup(groupID).getUserlist().get(userID);
        }else{
            return null;
        }
    }
    
    @POST
    @Path("/{groupid}/users/{userid}/messages")
    public Response createNewMessage(@PathParam("groupid") int groupID, @PathParam("userid") int userID, @Context HttpServletRequest request,@FormParam("message") String msg) {
       HttpSession session = request.getSession(true);
       if(!session.isNew()){
        System.out.println(session.getAttribute("id"));

        int sessionID = (Integer) session.getAttribute("id");

        if(groupdao.getGroup(groupID).getUserlist().containsKey(userID)){
         if(sessionID == userID){
             groupdao.createMessage(userID, groupID, msg);
             return Response.status(200).entity("Created a message.").build();  
         }else{
             return Response.status(401).entity("You are not logged in as this user.").build(); 
         }
        }else{
            return Response.status(401).entity("User does not belong in the group.").build(); 
        }
       }else{
           return Response.status(401).entity("User not logged in.").build(); 
       }
    }
    
    @GET
    @Path("/{groupid}/messages")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Message> getGroupMessages(@PathParam("groupid") int groupID, @Context HttpServletRequest request) {
       HttpSession session = request.getSession(true);
       if(!session.isNew()){
        System.out.println(session.getAttribute("id"));
        int sessionID = (Integer) session.getAttribute("id");

        if(groupdao.getGroup(groupID).getUserlist().containsKey(sessionID)){
             Group g = groupdao.getGroup(groupID);
             ArrayList returnarray = new ArrayList<Message>();
             for(Map.Entry<Integer,Message> entry : g.getGroupmessages().entrySet()){
                 returnarray.add(entry.getValue());
             }        
             //return Response.status(200).entity(returnarray).build();
             return returnarray;

        }else{
            return null;
        }
       }else{
           return null;
       }
    }
}
