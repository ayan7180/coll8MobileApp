package backend;

import backend.Announcements.Announcement;
import backend.Calendar.Event;
import backend.Main;
import backend.Profile.Profile;
import backend.TestConfiguration;
import backend.Users.User;
import io.restassured.http.ContentType;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;// SBv3

//@SpringBootApplication
//@ContextConfiguration(classes = SomaSystemTest.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(classes = {TestConfiguration.class, Main.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SomaSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestConfiguration testConfiguration;

    @Before
    public void setUp() {
        testConfiguration.setPort(port);
        //RestAssured.port = port;
        //RestAssured.baseURI = "http://localhost";
    }

    /**
     * TESTING CALENDAR FEATURE
     */
    @Test
    public void testEvent(){
        //create new event
        String[] event1 = {"Event Class Name", "EventName"};

        //send post request to create event
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(event1)
                .when()
                .post("/event");

        //validate status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        //validate body response
        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Event Class Name", returnObj.get("className"));
            assertEquals("EventName", returnObj.get("event"));
            //assertEquals("testing description", returnObj.get("description"));
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    //testing updating event
//    @Test
//    public void updateEvent() throws JSONException {
//        Event event1 = new Event();
//        event1.setClassName("Original Class Name");
//        event1.setEvent("Original Event Name");
//
//        Response response = RestAssured.given()
//                .contentType(ContentType.JSON)
//                .body(event1)
//                .when()
//                .post("/event");
//        assertEquals(200, response.getStatusCode());
//
//        String returnString = response.getBody().asString();
//        int eventId = new JSONObject(returnString).getInt("id");
//
//        String updatedName = "Updated Name";
//
//        Response updateResponse = RestAssured.given()
//                .contentType(ContentType.JSON)
//                .body(updatedName) // Send new event name
//                .when()
//                .put("/event/" + eventId);
//
//        assertEquals(200, updateResponse.getStatusCode());
//
//        String updateMessage = updateResponse.getBody().asString();
//        assertEquals("event name successfully updated", updateMessage);
//
//        // Step 3: Retrieve the updated event and verify changes
//        Response getResponse = RestAssured.given()
//                .when()
//                .get("/event/" + eventId); // Assuming you have a GET endpoint to retrieve by ID
//        assertEquals(200, getResponse.getStatusCode());
//
//        String getResponseBody = getResponse.getBody().asString();
//        JSONObject retrievedEvent = new JSONObject(getResponseBody);
//        assertEquals(updatedName, retrievedEvent.get("event"));
//    }

//



    /**
     * TESTING ANNOUNCEMENTS FEATURE
     * test the text length the current code can handle 299 characters
     */
    @Test
    public void testCreateAnnouncements(){
        //create new announcement object and populate
        String[] ann1 = {"Test Case Title", "system testing text..."};

        //send post request to create announcement
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(ann1)
                .when()
                .post("/announcements");

        //validate status code- expected vs actual
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        //validate body response
        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Test Case Title", returnObj.get("title"));
            assertEquals("System testing text...", returnObj.get("text"));
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * TESTING USER FEATURE
     */
    public void testUpdateUser(){

    }
    public User createNewUser(){
        User user1 = new User();
        user1.setUsername("Soma");
        user1.setEmailId("soma@gmail.com");
        user1.setPassword("psw1");
        user1.setProfileImage("pfp.png");
        return user1;
    }
    //test user login
    @Test
    public void testCreateUser(){

        String[] user1 = {"Soma", "psw1"};

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user1)
                .when()
                .post("/signup");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Soma", returnObj.get("username"));
           // assertEquals("soma@gmail.com", returnObj.get("emailId"));
            assertEquals("psw1", returnObj.get("password"));
           // assertEquals("pfp.png", returnObj.get("profileImage"));
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


    //test create user profile
    @Test
    public void testCreateProfile(){
        Profile p1 = new Profile(createNewUser());

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(p1)
                .when()
                .post("/profile");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Soma", returnObj.get("name"));
            assertEquals("soma@gmail.com", returnObj.get("email"));
            assertEquals("pfp.png", returnObj.get("profileImage"));
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
