package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
/**
 * The personalFileRepoActivity class provides an interface for viewing files related to a specific person.
 * Users can view files from a server and navigate back to the home screen.
 *  @author AyanAsim
 */
public class personalFileRepoActivity extends AppCompatActivity {

    private ImageButton home;
    private TextView filesList;
    private Button fetchFilesButton;

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/file/search";
    //private static final String URL = "https://095041f7-7ed3-487d-8916-8a5dbf9ac3eb.mock.pstmn.io";
    /**
     * Initializes the activity, setting up the layout and click listeners for UI elements.
     * @param SavedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.personal_file_repo);

        home = findViewById(R.id.homeButton);
        filesList = findViewById(R.id.filesLib);
        fetchFilesButton = findViewById(R.id.fetchFilesButton);




        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(personalFileRepoActivity.this, home_classes.class);
                startActivity(intent);
            }
        });
        // Set listener for fetchFilesButton
        fetchFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int courseNum = 288;
                String abbreviation = "CPRE";

                // Call getClassFiles to fetch files for the course
                getClassFiles(courseNum, abbreviation);
            }
        });

    }
    /**
     * Sends a POST request to fetch files related to a specific course.
     * Populates the filesList TextView with file details if the request is successful.
     * @param courseNum The course number.
     * @param abbreviation The course abbreviation.
     */
    private void getClassFiles(int courseNum, String abbreviation) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("courseID", courseNum);
            postData.put("userID", JSONObject.NULL);
            Log.d("Request Payload", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, URL,
                response -> {
                    try {
                        String responseData = new String(response.data, StandardCharsets.UTF_8);
                        Log.d("Raw Response", "Response Data: " + responseData);

                        if (responseData == null || responseData.trim().isEmpty()) {
                            Log.e("Response Error", "Empty response from server");
                            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray filesArray = new JSONArray(responseData);
                        filesList.setText("");

                        for (int i = 0; i < filesArray.length(); i++) {
                            JSONObject fileObject = filesArray.getJSONObject(i);
                            String title = fileObject.getString("fileName");
                            int fileId = fileObject.getInt("id");

                            Log.d("File ID", "ID: " + fileId);
                            filesList.append("File ID: " + fileId + "\n");
                            filesList.append("File Name: " + title + "\n\n");
                        }

                    } catch (JSONException e) {
                        Log.e("Response Parsing Error", "Error parsing JSON response", e);
                    }
                },
                error -> {
                    Log.e("Volley Error", "Request failed", error);
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("Volley Error Response", errorData);
                        Log.e("Status Code", "Code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(this, "Error fetching files", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] jsonData = postData.toString().getBytes(StandardCharsets.UTF_8);
                params.put("stub", new DataPart("stub.json", jsonData, "application/json"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }


    /**
     * Creates a JsonArrayRequest for fetching file data (alternative method).
     * Displays a message if no files are found.
     */
    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, null, new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            if (response == null) {
                Toast.makeText(personalFileRepoActivity.this, "file not found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Volley Response", response.toString());
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("Volley Error", error.toString());
        }
    });


}
