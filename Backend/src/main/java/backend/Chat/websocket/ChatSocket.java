package backend.Chat.websocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import backend.Chat.websocket.*;
/**
 * @author Soma Germano
 * this class handles the websocket operations
 */
@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}")  // this is Websocket url
public class ChatSocket {

    private static Map<String, List<Session>> groupMap = new ConcurrentHashMap<>();
    private static Map<String, List<String>> memberMap = new ConcurrentHashMap<>();


    //private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    // cannot autowire static directly (instead we do it by the below
    // method
    private static MessageRepository msgRepo;
    private static GroupRepository grpRepo;
    /**
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     * @param repo
     */
    @Autowired
    public void setMessageRepository(MessageRepository repo) {
        msgRepo = repo;  // we are setting the static variable
    }

    @Autowired
    public void setGroupRespository(GroupRepository repo){
        grpRepo = repo;
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    /**
     * invoked when a new websocket connection is established
     * @param session
     * @param username
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username)
            throws IOException {

        logger.info("Entered into Open");

        // store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        //Send chat history to the newly connected user
        sendMessageToPArticularUser(username, getChatHistory());

        // broadcast that new user joined
        String message = "User:" + username + " has Joined the Chat";
        broadcast(message);
    }

    /**
     * handles incoming messages from the websocket client
     * @param session
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        // Direct message to a user using the format "@username <message>"
        if (message.startsWith("@")) {
            String destUsername = message.split(" ")[0].substring(1);

            // send the message to the sender and receiver
            sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
            sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

        }

        else if(message.startsWith("/createGroup ")){
            String groupName = message.split(" ", 2)[1];
            groupMap.putIfAbsent(groupName, new ArrayList<>());
            sendMessageToPArticularUser(username, "Group '" + groupName + "' created.");
            Group grp1 = new Group();
            grp1.setGroupName(groupName);
            grp1.incrementMemberCount();
            grpRepo.save(grp1);
        }
        else if(message.startsWith("/joinGroup ")){
            String groupName = message.split(" ", 2)[1];
            if (groupMap.containsKey(groupName)) {
                groupMap.get(groupName).add(session);

                sendMessageToPArticularUser(username, "Joined group '" + groupName + "'.");
            } else {
                sendMessageToPArticularUser(username, "Group '" + groupName + "' does not exist.");
            }
        }
//        else if(message.startsWith("/joinGroup ")){
//            String groupName = message.split(" ", 2)[1];
//            //String username = sessionUsernameMap.get(session); // Fetch username from session
//
//            groupMap.putIfAbsent(groupName, new ArrayList<>());
//            if (!memberMap.get(groupName).contains(username)) {
//                memberMap.get(groupName).add(username);
//                sendMessageToPArticularUser(username, "You joined the group '" + groupName + "'.");
//            } else {
//                sendMessageToPArticularUser(username, "You are already in the group '" + groupName + "'.");
//            }
//        }

        else if(message.startsWith("/sendToGroup ")){
//            String[] parts = message.split(" ", 3);
//            if (parts.length < 3) return;
//
//            String groupName = parts[1];
//            String groupMessage = parts[2];
//            String sender = sessionUsernameMap.get(session);
//
//            if (memberMap.containsKey(groupName)) {
//                for (String member : memberMap.get(groupName)) {
//                    Session memberSession = usernameSessionMap.get(member);
//                    if (memberSession != null && memberSession.isOpen()) {
//                        memberSession.getBasicRemote().sendText(sender + " (Group " + groupName + "): " + groupMessage);
//                    }
//                }
//            } else {
//                sendMessageToPArticularUser(sender, "Group '" + groupName + "' does not exist.");
//            }
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) return;

            String groupName = parts[1];
            String groupMessage = parts[2];
            if (groupMap.containsKey(groupName)) {
                for (Session groupSession : groupMap.get(groupName)) {
                    groupSession.getBasicRemote().sendText(username + " (Group " + groupName + "): " + groupMessage);
                }
            } else if (message.equals("/getGroups")) {
                List<String> groupNames = getGroups();
                sendMessageToPArticularUser(username, "Groups: " + String.join(", ", groupNames));
            }
            else {
                sendMessageToPArticularUser(username, "Group '" + groupName + "' does not exist.");
            }
        }

        else { // broadcast
            broadcast(username + ": " + message);
        }

        // Saving chat history to repository
        msgRepo.save(new Message(username, message));
        //grpRepo.save();
    }

    /**
     * invoked when the websocket connection is closed
     * @param session
     * @throws IOException
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");
        List<String> groups = getGroups();
        logger.info("Current groups: "+groups);

        // remove the user connection information
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        groupMap.values().forEach(group -> group.remove(session));

        // broadcase that the user disconnected
        String message = username + " disconnected";
        broadcast(message);

//        String username = sessionUsernameMap.get(session);
//        sessionUsernameMap.remove(session);
//        usernameSessionMap.remove(username);
//
//        groupMap.values().forEach(members -> members.remove(username));
//
//        broadcast(username + " has disconnected.");
    }

    /**
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }

    /**
     * allows one user to dm another user
     * @param username
     * @param message
     */
    private void sendMessageToPArticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    /**
     * broadcasts a message to all users in the chat
     * @param message
     */
    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            }
            catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }

        });

    }




    /**
     * gets the chat history from the repository
     * @return
     */
    private String getChatHistory() {
        List<Message> messages = msgRepo.findAll();

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if(messages != null && messages.size() != 0) {
            for (Message message : messages) {
                sb.append(message.getUserName() + ": " + message.getContent() + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * this function should return a list of created groups
     * @return
     * usage:
     * List<String></> groups = getGroups();
     * logger.info("Curent groups: "+groups);
     * this will return a [Group1Name, Group2Name,...]
     */
    static List<String> getGroups(){
       return new ArrayList<>(groupMap.keySet());
    }

    public static Map<String, List<Session>> getGroupMap() {
        return groupMap;
    }

    public static void createGroup(String groupName) {
        groupMap.putIfAbsent(groupName, new ArrayList<>());
    }

    public static List<String> getGroupMembers(String groupName) {
        return memberMap.getOrDefault(groupName, new ArrayList<>());
    }



} // end of Class
