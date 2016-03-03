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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        for (Map.Entry<Integer, Group> entry : groupdao.getGroups().entrySet()) {
            returnarray.add(entry.getValue());
        }
        return returnarray;
    }

    @POST
    @Produces("text/plain")
    public Response createGroup(@FormParam("groupname") String groupname, @FormParam("groupstarterID") int starterID, @Context HttpHeaders headers) {
        if (userdao.getUsers().containsKey(starterID)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if (authResult && myID == starterID) {
                    User groupstarter = userdao.getUser(starterID);
                    int groupID = groupdao.createGroup(groupname, groupstarter);
                    if (groupID != 0) {
                        return Response.status(201).entity(groupID).build();
                    } else {
                        return Response.status(400).entity("Groupname is invalid.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("User not found").build();
        }
    }

    @GET
    @Path("/{groupid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGroupXML(@PathParam("groupid") int groupid, @Context HttpHeaders headers) {

        if (groupdao.getGroups().containsKey(groupid)) {
            return Response.status(200).entity(groupdao.getGroup(groupid)).build();
        } else {
            return Response.status(404).entity("Group not found").build();
        }
    }

    @PUT
    @Path("/{groupid}")
    public Response updateGroup(@PathParam("groupid") int groupid, @FormParam("newname") String newname, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupid)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupid).getGroupAdmins().containsKey(myID)) {
                        String changeGroupname = groupdao.updateGroup(groupid, newname);
                        if (!changeGroupname.equals("Failed")) {
                            return Response.status(200).entity("Groupname changed.").build();
                        } else {
                            return Response.status(400).entity("Groupname is invalid.").build();
                        }
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    @DELETE
    @Path("/{groupid}")
    public Response deleteGroup(@PathParam("groupid") int groupid, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupid)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if (authResult) {
                    if (groupdao.getGroup(groupid).getGroupAdmins().containsKey(myID)) {
                        //Removing the group from all users within the group
                        Group g = groupdao.getGroup(groupid);
                        ArrayList<User> removedusers = new ArrayList<User>();
                        for (Map.Entry<Integer, User> entry : g.getUserlist().entrySet()) {
                            removedusers.add(entry.getValue());
                            //groupdao.removeUserFromGroup(groupid, entry.getValue());
                        }
                        for (User u : removedusers) {
                            groupdao.removeUserFromGroup(groupid, u.getUserID());
                            if (u.getGrouplist().containsKey(groupid)) {
                                System.out.println("Vituiks meni " + u.getUserID() + " " + groupid);
                            }
                        }

                        System.out.println(groupdao.getGroup(groupid).getUserlist().size());
                        return Response.status(200).entity("Group removed").build();
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    //Leave group
    @DELETE
    @Path("/{groupid}/leave")
    public Response leaveGroup(@PathParam("groupid") int groupid, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupid)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupid).getUserlist().containsKey(myID)) {
                        groupdao.removeUserFromGroup(groupid, myID);
                        return Response.status(200).entity("Succesfully left the group.").build();
                    } else {
                        return Response.status(401).entity("You are not part of this group.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    @POST
    @Path("/{groupid}/users")
    public Response addUserToGroup(@PathParam("groupid") int groupID, @FormParam("userID") int userID, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getGroupAdmins().containsKey(myID)) {
                        if (!groupdao.getGroup(groupID).getUserlist().containsKey(userID)) {
                            groupdao.addUserToGroup(groupID, userdao.getUser(userID));
                            return Response.status(200).entity("User " + userID + " added to group " + groupID).build();
                        } else {
                            return Response.status(409).entity("User " + userID + " is already in this group.").build();
                        }
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }

                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("User or group not found.").build();
        }
    }

    @GET
    @Path("/{groupid}/admins")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGroupAdmins(@PathParam("groupid") int groupID, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupID)) {

            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                //Getting current userID
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getUserlist().containsKey(myID)) {
                        Group g = groupdao.getGroup(groupID);
                        ArrayList returnarray = new ArrayList<User>();
                        for (Map.Entry<Integer, User> entry : g.getGroupAdmins().entrySet()) {
                            returnarray.add(entry.getValue());
                        }
                        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(returnarray) {
                        };
                        return Response.status(200).entity(entity).build();

                    } else {
                        return Response.status(401).entity("You are not part of this group.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    @POST
    @Path("/{groupid}/admins")
    public Response addGroupAdmin(@PathParam("groupid") int groupID, @FormParam("userID") int userID, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if (authResult) {
                    if (groupdao.getGroup(groupID).getGroupAdmins().containsKey(myID)) {
                        if (!groupdao.getGroup(groupID).getGroupAdmins().containsKey(userID)) {
                            groupdao.promoteToAdmin(groupID, userID);
                            System.out.println("User promoted");
                            System.out.println(groupdao.getGroup(groupID).getGroupAdmins().keySet());
                            return Response.status(200).entity("User " + userID + " promoted to admin in " + groupID).build();
                        } else {
                            return Response.status(409).entity("User " + userID + " is already an admin").build();
                        }
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("User or group not found.").build();
        }
    }

    @DELETE
    @Path("/{groupid}/admins")
    public Response removeGroupAdmin(@PathParam("groupid") int groupID, @PathParam("userID") int userID, @Context HttpHeaders headers) {
        if (groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();
                if (authResult) {
                    if (groupdao.getGroup(groupID).getGroupAdmins().containsKey(myID)) {
                        if (groupdao.getGroup(groupID).getGroupAdmins().containsKey(userID)) {
                            groupdao.demoteFromADmin(groupID, userdao.getUser(userID));
                            return Response.status(200).entity("User " + userID + " added to group " + groupID).build();
                        } else {
                            return Response.status(409).entity("User " + userID + " is not an admin").build();
                        }
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("User or group not found.").build();
        }
    }

    @GET
    @Path("/{groupid}/users")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGroupUsers(@PathParam("groupid") int groupID, @Context HttpHeaders headers) {

        if (groupdao.getGroups().containsKey(groupID)) {

            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                //Getting current userID
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getUserlist().containsKey(myID)) {
                        Group g = groupdao.getGroup(groupID);
                        ArrayList returnarray = new ArrayList<User>();
                        for (Map.Entry<Integer, User> entry : g.getUserlist().entrySet()) {
                            returnarray.add(entry.getValue());
                        }
                        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(returnarray) {
                        };
                        return Response.status(200).entity(entity).build();

                    } else {
                        return Response.status(401).entity("You are not part of this group.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    
    @DELETE
    @Path("/{groupid}/users/{userid}")
    public Response removeUserFromGroup(@PathParam("groupid") int groupID, @PathParam("userid") int userID, @Context HttpHeaders headers) {

        if (groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)) {
            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getGroupAdmins().containsKey(myID)) {
                        if (groupdao.getGroup(groupID).getUserlist().containsKey(userID)) {
                            groupdao.removeUserFromGroup(groupID, userID);
                            return Response.status(200).entity("User removed from group.").build();
                        } else {
                            return Response.status(409).entity("User " + userID + " is not in this group.").build();
                        }
                    } else {
                        return Response.status(401).entity("You are not a group admin.").build();
                    }

                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("User or group not found.").build();
        }
    }
    /*
    //Useless?
    @GET
    @Path("/{groupid}/users/{userid}")
    @Produces(MediaType.APPLICATION_XML)
    public User getGroupUser(@PathParam("groupid") int groupID, @PathParam("userid") int userID) {
        if (groupdao.getGroups().containsKey(groupID) && userdao.getUsers().containsKey(userID)) {
            return groupdao.getGroup(groupID).getUserlist().get(userID);
        } else {
            return null;
        }
    } */

    @POST
    @Path("/{groupid}/users/{userid}/messages")
    public Response createNewMessage(@PathParam("groupid") int groupID, @PathParam("userid") int userID, @FormParam("message") String msg, @Context HttpHeaders headers) {

        if (userdao.getUsers().containsKey(userID) && groupdao.getGroups().containsKey(groupID)) {
            if (groupdao.getGroup(groupID).getUserlist().containsKey(userID)) {
                if (headers.getRequestHeaders().keySet().contains("authorization")) {
                    String authCredentials = headers.getRequestHeader("authorization").get(0);
                    boolean authResult = userdao.authenticateUser(authCredentials);
                    int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                    if (authResult && myID == userID) {
                        groupdao.createMessage(userID, groupID, msg);
                        return Response.status(201).entity("Message created.").build();

                    } else {
                        return Response.status(401).entity("Authorization failed.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("User is not part of the group.").build();
            }
        } else {
            return Response.status(404).entity("User or group not found.").build();
        }
    }

    @GET
    @Path("/{groupid}/messages")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGroupMessages(@PathParam("groupid") int groupID, @Context HttpHeaders headers) {

        if (groupdao.getGroups().containsKey(groupID)) {

            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                //Getting current userID
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getUserlist().containsKey(myID)) {
                        Group g = groupdao.getGroup(groupID);

                        ArrayList returnarray = new ArrayList<MessageXML>();

                        /*
                            Getting each message object from the message HashMap of the group and then creating a new
                            MessageXML object to represent the message.
                            Each message is stored in a new ArrayList which is then sorted and returned with a 200 ok response
                           
                         */
                        for (Map.Entry<Integer, Message> entry : g.getGroupmessages().entrySet()) {
                            Message msg = entry.getValue();

                            String username = userdao.getUser(msg.getUserID()).getUsername();
                            String message = msg.getMessage();
                            int userID = msg.getUserID();
                            int msggroupID = msg.getGroupID();
                            int messageID = msg.getMessageID();
                            String timestamp = msg.getTimestamp();

                            MessageXML newMsg = new MessageXML(userID, msggroupID, messageID, username, message, timestamp);
                            returnarray.add(newMsg);
                        }
                        //Sorting messages
                        Collections.sort(returnarray);

                        GenericEntity<List<MessageXML>> entity = new GenericEntity<List<MessageXML>>(returnarray) {
                        };
                        return Response.status(200).entity(entity).build();

                    } else {
                        return Response.status(401).entity("You are not part of this group.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }

    @GET
    @Path("/{groupid}/messages/latest")
    public Response getLatestMessage(@PathParam("groupid") int groupID, @Context HttpHeaders headers) {

        if (groupdao.getGroups().containsKey(groupID)) {

            if (headers.getRequestHeaders().keySet().contains("authorization")) {
                String authCredentials = headers.getRequestHeader("authorization").get(0);
                boolean authResult = userdao.authenticateUser(authCredentials);
                //Getting current userID
                int myID = userdao.getUserByName(userdao.decodeUsername(authCredentials)).getUserID();

                if (authResult) {
                    if (groupdao.getGroup(groupID).getUserlist().containsKey(myID)) {
                        Group g = groupdao.getGroup(groupID);
                        int latestID;
                        if (!g.getGroupmessages().isEmpty()) {
                            latestID = Collections.max(g.getGroupmessages().keySet());
                        } else {
                            latestID = 0;
                        }
                        return Response.status(200).entity(latestID).build();

                    } else {
                        return Response.status(401).entity("You are not part of this group.").build();
                    }
                } else {
                    return Response.status(401).entity("Authorization failed.").build();
                }
            } else {
                return Response.status(401).entity("Authorization failed.").build();
            }
        } else {
            return Response.status(404).entity("Group not found.").build();
        }
    }
}
