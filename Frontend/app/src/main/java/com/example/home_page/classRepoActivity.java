package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * This class adds functionality to the Class Repo layout,
 * allowing users to view class files as they dynamically appear.
 *
 @author Aisha Mohamed
 */
public class classRepoActivity extends AppCompatActivity {

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/file/search/";

    private ImageButton homeButton;
    private LinearLayout filesContainer;
    private Button classChat;
    private Button groupChat;
    private int courseNum;
    private String abbrev;

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views and listeners.
     *
     * @param savedInstanceState Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_repo);

        // Get course number and abbreviation from the intent
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("abbreviation")) {
                abbrev = intent.getStringExtra("abbreviation");
            }
            if (intent.hasExtra("courseNum")) {
                courseNum = intent.getIntExtra("courseNum", 0);
            }
        }

        // Request files based on course
        getClassFiles(courseNum, abbrev);

        homeButton = findViewById(R.id.homeButton);
        classChat = findViewById(R.id.gotoChatBtn);
        filesContainer = findViewById(R.id.classFilesContainer);

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(classRepoActivity.this, home_classes.class);
            startActivity(homeIntent);
        });

        classChat.setOnClickListener(v -> {
            Intent chatIntent = new Intent(classRepoActivity.this, mainChatActivity.class);
            chatIntent.putExtra("courseNum", courseNum);
            chatIntent.putExtra("abbreviation", abbrev);
            startActivity(chatIntent);
        });
    }

    /**
     *Sends POST request  to retrieve lasses based on the course number and abbreviation
     * Parses Data received to display the information
     *
     * @param courseNum Utilizes the courseNum passed from previous activity
     * @param abbreviation Implements abbreviation, to use for request.
     */
    private void getClassFiles(int courseNum, String abbreviation) {
        JSONObject postData = new JSONObject();
        try {
            JSONObject courseObject = new JSONObject();
            courseObject.put("courseNum", courseNum);
            courseObject.put("abbreviation", abbreviation);
            postData.put("course", courseObject);
            postData.put("user", JSONObject.NULL);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Make the POST request to fetch files
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, URL,
                response -> {
                    try {

                        String responseData = new String(response.data, StandardCharsets.UTF_8);
                        JSONArray filesArray = new JSONArray(responseData);

                        for (int i = 0; i < filesArray.length(); i++) {
                            JSONObject fileObject = filesArray.getJSONObject(i);
                            String title = fileObject.getString("fileName");
                            int fileId = fileObject.getInt("id");

                            Log.d("File ID", "ID: " + fileId);

                            addFile(title, fileId);

                        }

                    } catch (JSONException e) {
                        Log.e("Response Parsing Error", "Error parsing JSON response", e);
                    }
                },
                error -> Log.e("Upload Error", "File upload failed", error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] jsonData = postData.toString().getBytes(StandardCharsets.UTF_8);
                params.put("json", new DataPart("data.json", jsonData, "application/json"));
                Log.d("JSON Byte Data", "JSON as Byte Data: " + new String(jsonData));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    /**
     * Dynamically adds entry for every file, including its title and a button to view the file.
     * The entry is added to the files container with a divider, to separate and differentiate between files.
     *
     * @param title   The title of the file for displaying.
     * @param fileId  The unique ID for each file, passed to the next activity for file details and annotation.
     */

    private void addFile(String title, int fileId) {
        LinearLayout fileEntryLayout = new LinearLayout(this);
        fileEntryLayout.setOrientation(LinearLayout.HORIZONTAL);
        fileEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        fileEntryLayout.setPadding(10, 10, 10, 10);

        // textview for file title
        TextView fileTextView = new TextView(this);
        fileTextView.setText(title);
        fileTextView.setTextSize(16);
        fileTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 to take up remaining space

        // button for details
        Button viewButton = new Button(this);
        viewButton.setText("View File");
        viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(classRepoActivity.this, annotateDetailsActivity.class);
            intent.putExtra("id", fileId);
            startActivity(intent);
        });

        //build layout
        fileEntryLayout.addView(fileTextView);
        fileEntryLayout.addView(viewButton);
        filesContainer.addView(fileEntryLayout);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        filesContainer.addView(divider);
    }
}