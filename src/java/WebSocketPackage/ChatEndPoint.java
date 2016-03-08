/*
This is the WebSocket endpoint that users connect to from the user interface when they enter a group.
The class does not interact with other classes in the the project, it simply passes a message containing the group ID to users
that have connected to this endpoint.

*/
package WebSocketPackage;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/chatendpoint")
public class ChatEndPoint {
    
    private static Set<Session> wsSessions = Collections.synchronizedSet(new HashSet<Session>());
    
    //Sends a message to all sessions connected to this endpoint
    @OnMessage
    public void onMessage(String message) throws IOException, EncodeException {
        for (Session wsSession : wsSessions) {
            wsSession.getBasicRemote().sendObject(message);
        }
    }
    
    //Adds the session to the session set when a user connects
    @OnOpen
    public void onOpen(Session wsSession) {
        wsSessions.add(wsSession);
    }
    
    //Removes the session from the session set when connection is closed
    @OnClose
    public void onClose(Session wsSession) {
        wsSessions.remove(wsSession);
    }
}
