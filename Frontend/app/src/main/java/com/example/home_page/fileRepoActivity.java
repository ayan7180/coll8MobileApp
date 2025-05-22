package com.example.home_page;

import android.content.Intent;
import android.graphics.Color;
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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity creates a file repo,
 * a collection of files that users can access further given they select a file.
 * Provides functionality for navigation back to the home screen.
 @author Aisha Mohamed
 */
public class fileRepoActivity extends AppCompatActivity {
    private ImageButton home;
    private LinearLayout filesContainer;
    private Button allFilesButton;
    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/file/search/";
    private JSONObject postData;
    private String title;
    private String username;

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
        setContentView(R.layout.file_repo);

        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        Intent homeIntent = getIntent();
        username = homeIntent.getStringExtra("USERNAME_KEY");

        requestFile();

        home = findViewById(R.id.homeButton);
        filesContainer = findViewById(R.id.filesContainer);
        allFilesButton = findViewById(R.id.allFilesButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fileRepoActivity.this, home_classes.class);
                startActivity(intent);

            }
        });

        postData = new JSONObject();
        try {
            JSONObject userObject = new JSONObject();
            JSONObject courseObject = new JSONObject();
            userObject.put("username", "Simon"); // Replace "Simon" with actual user data
            postData.put("course", JSONObject.NULL);
            postData.put("user", userObject);
        } catch (JSONException e) {
            Log.e("JSON Error", "Error creating JSON object", e);
        }
    }


    /**
     * Sends a Multipart Request to backend to get files
     *
     */
    private void requestFile() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, URL,
                response -> {
                    try {
                        String responseData = new String(response.data, StandardCharsets.UTF_8);
                        JSONArray filesArray = new JSONArray(responseData);


                        for (int i = 0; i < filesArray.length(); i++) {
                            JSONObject fileObject = filesArray.getJSONObject(i);
                            String filepath = fileObject.getString("filePath");
                            String fileType = fileObject.getString("fileType");
                            int id = fileObject.getInt("id");
                            String fileName = filepath.substring(filepath.lastIndexOf('/') + 1);
                            addFile(fileName, id);
                        }

                    } catch (JSONException e) {
                        Log.e("Response Parsing Error", "Error parsing JSON response", e);
                    }
                },
                error -> Log.e("Upload Error", "File upload failed", error)
            )   {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] jsonData = postData.toString().getBytes(StandardCharsets.UTF_8);
                params.put("json", new DataPart("data.json", jsonData, "application/json"));
                Log.d("JSON Byte Data", "JSON as Byte Data: " + Arrays.toString(jsonData));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    /**
     * Dynamically creates a layout  for the files to go into,
     * with multiple entries and each field includes its title and a button to view and select a file.
     *
     * @param title The title of the file that's going to be displayed in each entry.
     * @param id    The unique ID of each file, passed to the next activity for further use.
     */

    private void addFile(String title, int id) {
        LinearLayout fileEntryLayout = new LinearLayout(this);
        fileEntryLayout.setOrientation(LinearLayout.HORIZONTAL);
        fileEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        fileEntryLayout.setPadding(10, 10, 10, 10);

        // textview to see filename
        TextView fileTitle = new TextView(this);
        fileTitle.setText(title);
        fileTitle.setTextSize(20);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.weight = 0.8f;
        fileTitle.setLayoutParams(titleParams);

        // select button
        Button selectFileBtn = new Button(this);
        selectFileBtn.setText("Select File");
        selectFileBtn.setTextSize(10);
        selectFileBtn.setBackgroundColor(Color.parseColor("#990000"));
        selectFileBtn.setTextColor(Color.parseColor("#FFD300"));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.weight = 0.2f;
        selectFileBtn.setLayoutParams(buttonParams);

        // listener for viewing a file
        selectFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(fileRepoActivity.this, annotateDetailsActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("username", username);
            intent.putExtra("fileName", title);
            startActivity(intent);
        });


        fileEntryLayout.addView(fileTitle);
        fileEntryLayout.addView(selectFileBtn);
        filesContainer.addView(fileEntryLayout);
    }

    /**
     * Clears all files on the interface by clearing the layout of all views
     */
    private void clearFiles() {
        filesContainer.removeAllViews();
    }
}