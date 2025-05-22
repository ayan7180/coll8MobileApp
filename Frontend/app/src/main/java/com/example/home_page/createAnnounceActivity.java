package com.example.home_page;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class allows users to create Announcements, using text field such as:
 * The abstract title, and the body containing descriptive information.
 @author Aisha Mohamed
 */

public class createAnnounceActivity extends AppCompatActivity {
    private Button createAnnounce;
    private ImageButton home;
    private TextInputEditText abstractInput;
    private TextInputEditText bodyInput;
    String[] announcements = new String[2];
    private int added = 0;
    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/announcements";


    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views, and configures WebSocket and listeners.
     *
     * @param saved_instance Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate (Bundle saved_instance) {
        super.onCreate(saved_instance);
        setContentView(R.layout.create_announcement);

        home = findViewById(R.id.homeButton);
        abstractInput = findViewById(R.id.Abstract_Input);
        bodyInput = findViewById(R.id.body_Input);
        createAnnounce = findViewById(R.id.create_announcebtn);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(createAnnounceActivity.this, mainActivity.class);
                startActivity(intent);

            }
        });
        createAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String str_abstract = abstractInput.getText().toString();
                String str_body = bodyInput.getText().toString();
                announcements [0] = str_abstract;
                announcements [1] = str_body;
                postData(announcements);

            }
        });


    }


    /**
     * Send a POST request to backend to post information to create an announcement.
     *
     * @param a The Title and Body of the announcement to be created
     */
    private void postData(String[] a) {
        if (a == null || a.length < 2) {
            Toast.makeText(createAnnounceActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new JSONArray and add the values
        JSONArray jsonArray = new JSONArray();

        // Add elements from the array into the JSONArray
        for (String item : a) {
            jsonArray.put(item);
        }

        Log.d("JSON Payload", jsonArray.toString());

        // Use JsonArrayRequest to send the JSON array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                URL_JSON_OBJECT,
                jsonArray, // Use the JSON array as the request body
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Error occurred";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("Volley Error", "Error Response: " + errorResponse);
                            errorMsg = errorResponse; // Capture the error response
                        }
                        Log.e("Volley Error", errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set the content type
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
        Toast.makeText(createAnnounceActivity.this, "Announcement Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(createAnnounceActivity.this, genAnnounceActivity.class);
        startActivity(intent);
    }


}
