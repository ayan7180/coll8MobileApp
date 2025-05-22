package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Activity allows users to update an existing announcement text, given that it needs a slight update,
 * users can submit changes via a PUT request.
 * Provides functionality for navigation back to the home screen.
 @author Aisha Mohamed
 */

public class updateAnnounceActivity extends AppCompatActivity {

    private Button update;
    private ImageButton homeButton;
    private TextInputEditText makeChanges;
    private String id = "";
    private String url = "http://coms-3090-069.class.las.iastate.edu:8080/announcements/";

    /**
     * Initializes the activity, sets up interface elements, and configures listeners.
     * Handles navigation to the home screen and the updating announcement .
     *
     * Takes care of the PUT request using the provided announcement ID, to update the announcement.
     *
     * @param saved_instance Bundle object containing the activity's previously saved state, if any.
     */

    @Override
    protected void onCreate(Bundle saved_instance) {
        super.onCreate(saved_instance);
        setContentView(R.layout.update_announcement);

        homeButton = findViewById(R.id.homeButton);
        update = findViewById(R.id.updateAnnounce);
        makeChanges = findViewById(R.id.update_intxt);

        Intent intent = getIntent();

        // Check if the intent contains the "id" extra
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id"); // Retrieve the ID from the intent
        } else {
            id = "-1"; // Default value if no ID is provided
        }
        url += id;

        Log.e("URL", "Constructed URL: " + url); // Log the constructed URL

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(updateAnnounceActivity.this, home_classes.class);
                startActivity(intent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_update = makeChanges.getText().toString();

                JSONObject putData = new JSONObject();

                try {
                    putData.putOpt("text", str_update); // Adding data to the JSON object
                } catch (JSONException e) {
                    Log.e("JSONError", "Bad data");
                }

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Volley Response", response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return putData.toString().getBytes(); // Set the request body to the JSON object as bytes
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8"; // Set the content type to JSON
                    }
                };

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                Intent intent = new Intent(updateAnnounceActivity.this, genAnnounceActivity.class);
                startActivity(intent);
            }
        });
    }


}
