package backend.Annotations;

import backend.Files.SysFile;
import backend.Files.SysFileRepository;
import backend.Users.User;
import backend.Users.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Kill common exceptions

@ServerEndpoint(value = "/fileView/{username}/{fileID}")
@Controller      // this is needed for this to be an endpoint to springboot
public class AnnotationServer {

    // cannot autowire static directly (instead we do it by the below
    // method
    private static AnnotationRepository annotationRepository;
    private static SysFileRepository sysFileRepo;
    private static UserRepository userRepo;

    @Autowired
    public void setAnnotationRepository(AnnotationRepository repo) {
        annotationRepository = repo;  // we are setting the static variable
    }
    @Autowired
    public void setFileRepository(SysFileRepository repo) {
        sysFileRepo = repo;
    }
    @Autowired
    public void setUserRepository(UserRepository repo){
        userRepo = repo;
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, User> sessionUserMap = new Hashtable<>();
    private static Map<User, Session> userSessionMap = new Hashtable<>();
    private static Map<Session, SysFile> sessionFileMap = new Hashtable<>();


    private final Logger logger = LoggerFactory.getLogger(AnnotationServer.class);

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username,
                       @PathParam("fileID") int fileID)
            throws IOException {

        logger.info("Entered into Open");

        // Authenticity Checks // TODO Make sure this kills the connection
        if(sysFileRepo.findById(fileID).isEmpty()){
            //throw new IOException("Couldn't find file");
            sendMessageToSession("Couldn't find file", session);
            return;
        }
        SysFile parentFile = sysFileRepo.findById(fileID).get();
        if(userRepo.findByUsername(username).isEmpty()){
            //throw new IOException("Couldn't find username");
            sendMessageToSession("Couldn't find file", session);
            return;
        }
        User user = userRepo.findByUsername(username).get();

        // store connecting user information
        sessionUserMap.put(session, user);
        userSessionMap.put(user, session);
        sessionFileMap.put(session, parentFile);

        //Send file annotations to connected User
        sendAllAnnotationsToUser(session);

    }


    /**
     * int startChar, int endChar, String text,
     *                                     Set<Integer> fileReferenceIDs
     * @param session
     * @param jsonString
     * Layout for "text_create" operation:
     *{
     *     "command": "text_create",
     *     "startChar": int,
     *     "endChar": int,
     *     "text": String,
     *     "fileReferenceIDs": [
     *         int,
     *         int,
     *         ...
     *     ],
     *     "isChild": boolean,
     *     "parentAnnotationID": int
     * }
     * Layout for "video_create" operation:
     * {
     *     "command": "video_create",
     *     "startTime": double,
     *     "endTime": double,
     *     "text": String,
     *     "fileReferenceIDs": [
     *          int,
     *          int,
     *          ...
     *     ]
     *     "isChild": boolean,
     *     "parentAnnotationID": int
     * }
     * Layout for "delete" operation:
     * {
     *     "command": "delete",
     *     "id": int
     * }
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session, String jsonString) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(jsonString);
        String command = json.get("command").asText();

        if(command.equals("text_create")){
            onNewTextAnnotation(session, json);
        }
        else if(command.equals("video_create")){
            onNewVideoAnnotation(session, json);
        }
        else if(command.equals("delete")) {
            onDeleteAnnotation(session, json);
        }
        // TODO put

    }

    private void onNewVideoAnnotation(Session session, JsonNode json){
        double startTime = json.get("startChar").asDouble();
        double endTime = json.get("endChar").asDouble();
        String text = json.get("text").asText();
        ArrayNode jsonFileIDs = (ArrayNode)json.get("fileReferenceIDs");
        boolean isChild = json.get("isChild").asBoolean();
        int parentAnnotationID = json.get("parentAnnotationID").asInt();

        User user = sessionUserMap.get(session);
        SysFile file = sessionFileMap.get(session);
        logger.info("Making new annotation from user: " + user.getUsername());

        Set<SysFile> references = new HashSet<>();

        // Populate the references set from the ID set
        for (Iterator<JsonNode> it = jsonFileIDs.elements(); it.hasNext(); ) {
            JsonNode nextJsonID = it.next();
            int nextFileID = nextJsonID.asInt();
            if (sysFileRepo.findById(nextFileID).isEmpty()) {
                //throw new IOException("Could not find reference file by ID " + nextFileID);
                String message = "Could not find reference file by ID " + nextFileID;
                sendMessageToSession(message, session);
                return;
            }
            SysFile nextFile = sysFileRepo.findById(nextFileID).get();
            references.add(nextFile);
        }

        Annotation parentAnnotation = null;
        if(isChild){
            parentAnnotation = annotationRepository.findById(parentAnnotationID).get();
        }

        Annotation newAnnotation = new VideoAnnotation(startTime, endTime, text, references, file, parentAnnotation);
        annotationRepository.save(newAnnotation);

        broadcastAnnotation(newAnnotation);
    }

    /**
     * Deletes an annotation.
     * @param session The current session
     * @param json The json with the id of the annotation to be deleted
     * @throws IOException
     */
    private void onDeleteAnnotation(Session session, JsonNode json) throws IOException {
        int id = json.get("id").asInt();
        annotationRepository.deleteById(id);

        User user = sessionUserMap.get(session);
        String message = "Delete annotation " + id + " by user " + user.getUsername();
        logger.info(message);
        broadcastMessage(message);

    }

    private void onNewTextAnnotation(Session session, JsonNode json) throws IOException {
        int startChar = json.get("startChar").asInt();
        int endChar = json.get("endChar").asInt();
        String text = json.get("text").asText();
        ArrayNode jsonFileIDs = (ArrayNode)json.get("fileReferenceIDs");
        boolean isChild = json.get("isChild").asBoolean();
        int parentAnnotationID = json.get("parentAnnotationID").asInt();

        User user = sessionUserMap.get(session);
        SysFile file = sessionFileMap.get(session);
        logger.info("Making new annotation from user: " + user.getUsername());

        Set<SysFile> references = new HashSet<>();

        // Populate the references set from the ID set
        for (Iterator<JsonNode> it = jsonFileIDs.elements(); it.hasNext(); ) {
            JsonNode nextJsonID = it.next();
            int nextFileID = nextJsonID.asInt();
            if (sysFileRepo.findById(nextFileID).isEmpty()) {
                //throw new IOException("Could not find reference file by ID " + nextFileID);
                String message = "Could not find reference file by ID " + nextFileID;
                sendMessageToSession(message, session);
                return;
            }
            SysFile nextFile = sysFileRepo.findById(nextFileID).get();
            references.add(nextFile);
        }

        Annotation parentAnnotation = null;
        if(isChild){
            parentAnnotation = annotationRepository.findById(parentAnnotationID).get();
        }

        Annotation newAnnotation = new TextAnnotation(startChar, endChar, text, references, file, parentAnnotation);
        annotationRepository.save(newAnnotation);

        broadcastAnnotation(newAnnotation);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        // remove the user connection information
        User user = sessionUserMap.get(session);
        sessionUserMap.remove(session);
        userSessionMap.remove(user);

        SysFile file = sessionFileMap.get(session);
        sessionFileMap.remove(session);
    }


    // TODO add more errors?
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }

    // TODO Broadcast new/changed annotation

    // TODO
    private void sendAllAnnotationsToUser(Session session){
        User user = sessionUserMap.get(session);
        SysFile file = sessionFileMap.get(session);
        List<Annotation> annotations = annotationRepository.findAllByFile(file);
        try {
            for (Annotation annotation : annotations) {
                sendAnnotationToUser(user, annotation);
            }
        } catch (Exception e) {
            // TODO
        }
    }

    private void sendAnnotationToUser(User user, Annotation annotation) {
        try{
            String jsonString = annotationToJsonString(annotation);
            userSessionMap.get(user).getBasicRemote().sendText(jsonString);
        } catch(Exception e){
            //throw new Exception("Failed to send object");
            String message = "Failed to send object";
            sendMessageToSession(message, userSessionMap.get(user));
            return;
        }
    }

    private void sendMessageToSession(String message, Session session){
        try{ session.getBasicRemote().sendText(message); }
        catch(Exception e) {} // TODO
    }

    private void broadcastMessage(String message) {
        userSessionMap.forEach( (user, session)->{
            try{ userSessionMap.get(user).getBasicRemote().sendText(message); }
            catch(Exception e) {} // TODO
        });
    }

    private void broadcastAnnotation(Annotation annotation) {

        userSessionMap.forEach( (user, session)->{
            try { sendAnnotationToUser(user, annotation); }
            catch(Exception e) { } //TODO
        });
    }

    private String annotationToJsonString(Annotation annotation) {
        String returnString = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode newJson = objectMapper.createObjectNode(); // objectMapper.readTree( objectMapper.writeValueAsString(annotation) );
            JsonNode baseJson = objectMapper.readTree(objectMapper.writeValueAsString(annotation));
            Iterator<String> it = baseJson.fieldNames();
            while (it.hasNext()) {
                String name = it.next();
                newJson.put(name, baseJson.get(name));
            }

            SysFile file = annotation.getParentFile();
            if (file != null) {
                newJson.put("parentFileID", annotation.getParentFile().getId());
            }
            Annotation parentAnnotation = annotation.getParentAnnotation();
            if (parentAnnotation != null) {
                newJson.put("parentAnnotationID", annotation.getParentAnnotation().getId());
            }
            returnString = newJson.toString();
        } catch (Exception e){
            returnString = "Annotation to JSON conversion failure.";
        }

        return returnString;
    }

}
