package backend;

import backend.CourseLibraries.CourseLibraryStubs;
import backend.CourseLibraries.CourseThumbnail;
import backend.Stubs.*;

import com.beust.ah.A;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import io.restassured.RestAssured;
import io.restassured.response.Response;

//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfiguration.class, Main.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SimonSystemTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestConfiguration testConfiguration;

	@Before
	public void setUp() {
		testConfiguration.setPort(port);
	}

	////////////////////////////
	// Helper Methods
	////////////////////////////

	// Create a user from user parameters
	private Response createUser(String[] userParams) {
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(userParams)
				.when()
				.post("/signup");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		return response;
	}

	// Create a major from stubs
	private Response createMajor(MajorStubs.New majorStub, UserStubs.Credentials adminCred){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.MULTIPART)
				.multiPart("cred", adminCred, "application/json")
				.multiPart("stub", majorStub, "application/json")
				.when()
				.post("/major");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		return response;
	}

	// Create a course from stubs
	private Response createCourse(CourseStubs.New courseStub, UserStubs.Credentials credStub){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.MULTIPART)
				.multiPart("cred", credStub, "application/json")
				.multiPart("stub", courseStub, "application/json")
				.when()
				.post("/course");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		return response;
	}

	// Grab all majors
	private JsonNode grabMajors(){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given().
				header("Content-Type", "text/plain").
				header("charset","utf-8").
				body("").
				when().
				get("/major");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode majorList;
		try{
			majorList = objectMapper.readTree(respString);
			return majorList;
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return null;
		}
	}

	// Grab all courses
	private JsonNode grabCourses(){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given().
				header("Content-Type", "text/plain").
				header("charset","utf-8").
				body("").
				when().
				get("/course");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode courseList;
		try{
			courseList = objectMapper.readTree(respString);
			return courseList;
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return null;
		}

	}

	// Grab all users
	private JsonNode grabUsers(){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given().
				header("Content-Type", "text/plain").
				header("charset","utf-8").
				body("").
				when().
				get("/login/all");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode userList;
		try{
			userList = objectMapper.readTree(respString);
			return userList;
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return null;
		}
	}

	// Grab all files
	private JsonNode grabFiles(String userID){
		Response response;
		int statusCode;
		String respString;

		FileStubs.Search searchStub = new FileStubs.Search();
		searchStub.setCourseID(null);
		searchStub.setUserID(Integer.parseInt(userID));

		response = RestAssured.given()
				.contentType(ContentType.MULTIPART)
				.multiPart("stub", searchStub, "application/json")
				.when()
				.post("/file/search");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode fileList;
		try{
			fileList = objectMapper.readTree(respString);
			return fileList;
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return null;
		}
	}

	// Delete a course
	private void deleteCourse(String courseID, UserStubs.Credentials credStub){
		Response response;
		int statusCode;
		String respString;

		String username = credStub.getUsername();
		String password = credStub.getPassword();

		String delURL = "/course/delete/" + courseID + "/" + username + "/" + password;
		response = RestAssured.given()
				.contentType(ContentType.JSON)
//				.body(credStub)
				.body("")
				.when()
				.post(delURL);

		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		// Does this check really need to happen?
//		try{
//			JsonNode jsonResp = objectMapper.readTree(respString);
//			String delCourseID = jsonResp.get("ID").asText();
//			if(!delCourseID.equals(courseID)){
//				fail("Incorrect course ID deleted");
//			}
//		} catch (JsonProcessingException e) {
//			fail(e.getMessage());
//		}
	}

	// Delete a major
	private void deleteMajor(String courseID, UserStubs.Credentials credStub){
		// TODO
	}


	private UserStubs.Credentials getAdminCred(){
		UserStubs.Credentials adminCred = new UserStubs.Credentials();
		adminCred.setUsername("Admin");
		adminCred.setPassword("Admin");
		return adminCred;
	}

	private UserStubs.Credentials getTestCred(){
		UserStubs.Credentials credStub = new UserStubs.Credentials();
		credStub.setUsername("TestUser");
		credStub.setPassword("Test");
		return credStub;
	}

	private CourseStubs.New getTestCourseStub() {

		CourseStubs.New courseStub = new CourseStubs.New();
		courseStub.setCourseNum(0);
		courseStub.setAbbreviation("TestMaj");
		courseStub.setTitle("Test");
		return courseStub;

	}

	private MajorStubs.New getTestMajorStub(){
		MajorStubs.New majorStub = new MajorStubs.New();
		majorStub.setAbbreviation("TestMaj");
		majorStub.setTitle("Test");
		return majorStub;
	}

	private void createTestUser(){
		UserStubs.Credentials testCred = getTestCred();
		String[] stringArr = {testCred.getUsername(), testCred.getPassword(), "Test", "Test"};

		createUser(stringArr);
	}

	private void createTestMajor(){
		createMajor(getTestMajorStub(), getAdminCred());
	}

	private void createTestFile(){
		// TODO

		FileStubs.New fileMetadata = new FileStubs.New();

		fileMetadata.setFileName("TestFile");

		CourseStubs.New testCourseStub = getTestCourseStub();
		String courseID = findCourseID(testCourseStub.getAbbreviation(), testCourseStub.getCourseNum() + "");
		fileMetadata.setCourseID(Integer.parseInt(courseID));

		UserStubs.Credentials testCred = getTestCred();
		String userID = findUserID(testCred.getUsername());
		fileMetadata.setUserID(Integer.parseInt(userID));

		// TODO Read test file from local file system

		File file;
		String path;
		try {
			// I'm assuming relative pathing starts from the src directory
			String filePath = "../Testing/TXT/upTest.txt";
			file = new File(filePath);
			path = file.getAbsolutePath();
			new FileInputStream(file.getAbsoluteFile()).readAllBytes();
//			fileReader = new FileInputStream(file);
		} catch(Exception e){
			fail(e.getMessage());
			return;
		}

		// TODO Send test file to be created under test user and test course

		Response response;
		int statusCode;
		String respString;

		try{
			response = RestAssured.given()
					.contentType(ContentType.MULTIPART)
					.multiPart("file", file)
					.multiPart("stub", fileMetadata, "application/json")
					.when()
					.post("/file/upload");
			statusCode = response.getStatusCode();
			respString = response.getBody().asString();
		}catch (Exception e){
			fail(e.getMessage());
			return;
		}


		assertEquals(200, statusCode);

		findFileID(userID, "TestFile");

		// TODO Do more checks ?
	}

	private void createTestAnnotations(){
		// TODO

		// TODO Build test annotations

		// TODO Connect to annotation repo


	}

	// Fails if no course ID is found for the given search
	private String findUserID(String givenUsername){
		JsonNode userList = grabUsers();

		boolean foundUser = false;
		String userID = "";
		for(JsonNode userJson : userList){
			String username = userJson.get("username").asText();
			if(givenUsername.equals(username)){
				foundUser = true;
				userID = userJson.get("id").asText();
				break;
			}
		}
		assertTrue(foundUser);

		return userID;
	}

	// Fails if no course ID is found for the given search
	private String findCourseID(String givenAbb, String givenNum){
		JsonNode courseList = grabCourses();

		boolean foundCourse = false;
		String courseID = "";
		for(JsonNode courseJson : courseList){
			String majorAbb = courseJson.get("major").get("abbreviation").asText();
			String courseNum = courseJson.get("courseNum").asText();
			if(
				majorAbb.equals(givenAbb)
				&& courseNum.equals(givenNum)
			){
				foundCourse = true;
				courseID = courseJson.get("id").asText();
				break;
			}
		}
		assertTrue(foundCourse);

		return courseID;
	}

	// Fails if no course ID is found for the given search
	private String findFileID(String userID, String givenFilename){
		JsonNode fileList = grabFiles(userID);

		boolean foundFile = false;
		String fileID = "";
		for(JsonNode fileJson : fileList){
			String filename = fileJson.get("fileName").asText();
			if(givenFilename.equals(filename)){
				foundFile = true;
				fileID = fileJson.get("id").asText();
				break;
			}
		}
		assertTrue(foundFile);

		return fileID;
	}

	private void createTestCourse(){
		createCourse(getTestCourseStub(), getTestCred());
	}

	private void joinCourse(Stubs.UserStubs.AddCourse stub) {
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(stub)
				.when()
				.post("/user/addCourse");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);
	}

	private void createAnnouncement(String courseID, AnnouncementStubs.New stub) {
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(stub)
				.when()
				.post("/announcements/" + courseID);
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);
	}


	private void setPreferences(CourseLibraryStubs.Update stub){
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(stub)
				.when()
				.put("library/updatePreferences");
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);
	}

	private List<CourseThumbnail> grabCourseLibrary(String username) {
		Response response;
		int statusCode;
		String respString;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("")
				.when()
				.get("library/" + username);
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		List<CourseThumbnail> returnList = new ArrayList<>();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode respJson;

		try{
			respJson = objectMapper.readTree(respString);
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return null;
		}

		for(JsonNode thumbJson : respJson){
			try {
				CourseThumbnail curThumb = objectMapper.treeToValue(thumbJson, CourseThumbnail.class);
				returnList.add(curThumb);
			} catch (JsonProcessingException e) {
				fail(e.getMessage());
				return null;
			}
		}

		return returnList;
	}


//	private void deleteUser(){
//
//	}

	/**
	 * This test tests creation for users.
	 */
	@Test
	public void userTest(){

		// Create test user //

		createTestUser();

		// Check for user //

		JsonNode userList = grabUsers();

		String userID = "";
		boolean userExists = false;
		for(JsonNode userJson : userList){
			if(userJson.get("username").asText().equals(getTestCred().getUsername())){
				userExists = true;
				userID = userJson.get("id").asText();
				break;
			}
		}

		assertTrue(userExists);

	}

	/**
	 * This tests creation of majors
	 */
	@Test
	public void majorTest(){

		createTestMajor();

		// Check for major //

		JsonNode majorList = grabMajors();

		boolean majorFound = false;
		String majorID;
		for(JsonNode majorJson : majorList){
			String majorAbb = majorJson.get("abbreviation").asText();
			if(majorAbb.equals(getTestMajorStub().getAbbreviation())){
				majorFound = true;
				majorID = majorJson.get("abbreviation").asText();
				break;
			}
		}
		assertTrue(majorFound);

	}

	/**
	 * This tests creation of courses
	 */
	@Test
	public void courseTest(){

		// Create major and user needed for course //

		createTestUser();
		createTestMajor();
		createTestCourse();

		// Check for course //

		JsonNode courseList = grabCourses();

		boolean foundCourse = false;
		String courseID = "";
		for(JsonNode courseJson : courseList){
			String majorAbb = courseJson.get("major").get("abbreviation").asText();
			String courseNum = courseJson.get("courseNum").asText();
			if(
					majorAbb.equals(getTestMajorStub().getAbbreviation())
							&& courseNum.equals(Integer.toString(getTestCourseStub().getCourseNum()))
			){
				foundCourse = true;
				courseID = courseJson.get("id").asText();
				break;
			}
		}

		assertTrue(foundCourse);

	}

	/**
	 * This tests deletion courses
	 */
	@Test
	public void courseDelTest(){

		// Create test objects //

		createTestUser();
		createTestMajor();
		createTestCourse();
		createTestFile();

		CourseStubs.New testCourse = getTestCourseStub();

		String courseID = findCourseID(testCourse.getAbbreviation(), testCourse.getCourseNum() + "");

		deleteCourse(courseID, getTestCred());

	}

	@Test
	public void userDelTest(){

		createTestUser();
		createTestMajor();
		createTestCourse();

		// TODO

	}

	@Test
	public void archiveRestorationTest(){

		createTestUser();
		createTestMajor();
		createTestCourse();
		createTestFile();
		createTestAnnotations();

		CourseStubs.New testCourse = getTestCourseStub();

		String courseID = findCourseID(testCourse.getAbbreviation(), testCourse.getCourseNum() + "");

		deleteCourse(courseID, getTestCred());

		String userID = findUserID(getTestCred().getUsername());

		String getArchiveURL = "/archive/all/" + userID;

		Response response;
		int statusCode;
		String respString;

		FileStubs.Search searchStub = new FileStubs.Search();
		searchStub.setCourseID(null);
		searchStub.setUserID(Integer.parseInt(userID));

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("")
				.when()
				.get(getArchiveURL);
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode archiveList;
		try{
			archiveList = objectMapper.readTree(respString);
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return;
		}

		System.out.println(archiveList.toPrettyString());

		int archiveID = -1;
		boolean archiveFound = false;
		for(JsonNode archive : archiveList){
			String abbreviation = archive.get("abbreviation").asText();
			int courseNum = archive.get("courseNum").asInt();
			System.out.println(abbreviation + courseNum);
			if(
					abbreviation.equals(testCourse.getAbbreviation())
					&& courseNum == testCourse.getCourseNum()
			){
				archiveFound = true;
				archiveID = archive.get("id").asInt();
				break;
			}
		}

		assertTrue(archiveFound);

		String restArchiveURL = "/archive/restore/" + archiveID;

		response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("")
				.when()
				.delete(restArchiveURL);
		statusCode = response.getStatusCode();
		respString = response.getBody().asString();

		assertEquals(200, statusCode);

		try{
			JsonNode returnJson = objectMapper.readTree(respString);
			returnJson.get("id").asInt();
		} catch (JsonProcessingException e) {
			fail(e.getMessage());
			return;
		}

		findCourseID(testCourse.getAbbreviation(), testCourse.getCourseNum() + "");


	}

	/**
	 * This test:
	 * 1. Creates Major, Courses, and User
	 * 2. Joins several courses for the user
	 * 3. Creates several announcements and files for a few courses
	 * 4. Grabs the course library for the user.
	 */
	@Test
	public void courseLibraryTest(){

		// TODO

		// Create one test major, several test users, and several test courses
		createTestMajor();
		createTestUser();
		createTestCourse();

		CourseStubs.New testCourse = getTestCourseStub();

		String courseID = findCourseID(testCourse.getAbbreviation(), testCourse.getCourseNum() + "");

		String userID = findUserID(getTestCred().getUsername());

		// Attempt a join for a user on a course they didn't make
		UserStubs.AddCourse addCourseStub = new UserStubs.AddCourse();
		addCourseStub.setCourseID(Integer.parseInt(courseID));
		addCourseStub.setUserID(Integer.parseInt(userID));
		joinCourse(addCourseStub);

		AnnouncementStubs.New announcementStub = new AnnouncementStubs.New();

		// Create several announcements on that course
		createAnnouncement(courseID, announcementStub);

		CourseLibraryStubs.Update preferenceStub = new CourseLibraryStubs.Update();
		preferenceStub.setUsername(getTestCred().getUsername());
		preferenceStub.setNumFiles(7);
		preferenceStub.setNumAnnouncements(5);
		// Set user preferences
		setPreferences(preferenceStub);

		// Grab the course library for the user, checking for announcements on that thumbnail
		grabCourseLibrary(getTestCred().getUsername());

	}



}
