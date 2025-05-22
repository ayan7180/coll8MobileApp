package com.example.home_page;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class add functionality to the create class layout.
 * Allowing users to add classes with information such as Course Number, Abbreviation, and Title.
 @author Aisha Mohamed
 */
public class createClassActivity extends AppCompatActivity {

    private TextInputEditText abbreviationInput;
    private TextInputEditText courseNumberInput;
    private TextInputEditText titleInput;
    private Button classCreate;
    private ImageButton homeButton;
    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/course";
    private static final String MAJOR_URL = "http://coms-3090-069.class.las.iastate.edu:8080/major";
    String user;
    String password;

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views, and configures WebSocket and listeners.
     *
     * @param savedInstanceState Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_class);

        abbreviationInput = findViewById(R.id.classification_intxt);
        courseNumberInput = findViewById(R.id.course_intxt);
        titleInput = findViewById(R.id.title_intxt);
        classCreate = findViewById(R.id.create_classbtn);
        homeButton = findViewById(R.id.homeButton);

        user = UserSingleton.getInstance().getUsername();
        password = UserSingleton.getInstance().getPassword();


        // Home button click listener
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(createClassActivity.this, home_classes.class);
                startActivity(intent);

            }
        });

        // Class creation button click listener
        classCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAbbreviation = abbreviationInput.getText().toString();
                String strCourseNum = courseNumberInput.getText().toString();
                String strCourseTitle = titleInput.getText().toString();

                // Prevent empty fields from being submitted
                if (strAbbreviation.isEmpty() || strCourseNum.isEmpty() || strCourseTitle.isEmpty()) {
                    Toast.makeText(createClassActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                }
                createMajor(strAbbreviation, strCourseTitle, user, password);
                createClass(strAbbreviation, strCourseNum, strCourseTitle, user, password);

                Intent intent = new Intent(createClassActivity.this, FindClassActivity.class);
                startActivity(intent);

            }
        });
    }

    /**
     * Sends a POST request using the abbreviation, course Number, and Course Title
     *
     * @param strAbbreviation Passes course abbreviation
     * @param strCourseNum Passes the course number
     * @param strCourseTitle The courses designated title
     */
    private void createClass(String strAbbreviation, String strCourseNum, String strCourseTitle, String user, String pass) {
        JSONObject stub = new JSONObject();
        JSONObject cred = new JSONObject();

        try {
            // Create the credentials JSON
            cred.put("username", user);
            cred.put("password", pass);

            // Create the course details JSON
            stub.put("courseNum", strCourseNum);
            stub.put("abbreviation", strAbbreviation);
            stub.put("title", strCourseTitle);

        } catch (JSONException e) {
            Log.e("JSONError", "Bad data");
            return;
        }

        // Multipart request using VolleyMultipartRequest
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("Upload Success", new String(response.data, StandardCharsets.UTF_8));
                    Toast.makeText(createClassActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Upload Error", "Class creation failed", error);
                    Toast.makeText(createClassActivity.this, "Error creating class", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>(); // Empty as no params are required
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                // Convert the JSON objects to byte arrays
                byte[] credData = cred.toString().getBytes(StandardCharsets.UTF_8);
                byte[] stubData = stub.toString().getBytes(StandardCharsets.UTF_8);

                // Add both JSON objects as separate parts in the multipart request
                params.put("cred", new DataPart("cred.json", credData, "application/json"));
                params.put("stub", new DataPart("stub.json", stubData, "application/json"));

                Log.d("JSON Byte Data", "Credentials JSON Byte Data: " + Arrays.toString(credData));
                Log.d("JSON Byte Data", "Course Stub JSON Byte Data: " + Arrays.toString(stubData));

                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(multipartRequest);
    }


    /**
     * Creates a major, that connects courses to major table
     * @param strAbbreviation Abbreviation as Major
     */
    private void createMajor(String strAbbreviation, String strCourseTitle, String user, String pass) {
        JSONObject stub = new JSONObject();
        JSONObject cred = new JSONObject();

        try {
            // Create the credentials JSON
            cred.put("username", user);
            cred.put("password", pass);

            // Create the major details JSON
            stub.put("abbreviation", strAbbreviation);
            stub.put("title", strCourseTitle);

        } catch (JSONException e) {
            Log.e("JSONError", "Bad data");
            return;
        }

        // Multipart request using VolleyMultipartRequest
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, MAJOR_URL,
                response -> {
                    Log.d("Upload Success", new String(response.data, StandardCharsets.UTF_8));
//                    Toast.makeText(createClassActivity.this, "Major created successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Upload Error", "Major creation failed", error);
                    Toast.makeText(createClassActivity.this, "Error creating major", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>(); // Empty as no params are required
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                // Convert the JSON objects to byte arrays
                byte[] credData = cred.toString().getBytes(StandardCharsets.UTF_8);
                byte[] stubData = stub.toString().getBytes(StandardCharsets.UTF_8);

                // Add both JSON objects as separate parts in the multipart request
                params.put("cred", new DataPart("cred.json", credData, "application/json"));
                params.put("stub", new DataPart("stub.json", stubData, "application/json"));

                Log.d("JSON Byte Data", "Credentials JSON Byte Data: " + Arrays.toString(credData));
                Log.d("JSON Byte Data", "Major Stub JSON Byte Data: " + Arrays.toString(stubData));

                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(multipartRequest);
    }


}
