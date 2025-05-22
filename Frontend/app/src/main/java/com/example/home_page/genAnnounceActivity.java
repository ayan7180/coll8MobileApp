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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity gives users a view of all the announcements,
 * while giving the option of selecting announcements to view further details.
 * Provides functionality for navigation back to the home screen.
 *
@author Aisha Mohamed
 */
public class genAnnounceActivity extends AppCompatActivity {
    private ImageButton home;
    private Button addAnnouncement;
    private LinearLayout announcementsContainer;
    private int id = 0;
    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/announcements/all";

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views and listeners.
     *
     * @param saved_instance Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate(Bundle saved_instance) {
        super.onCreate(saved_instance);
        setContentView(R.layout.gen_announce);

        home = findViewById(R.id.homeButton);
        addAnnouncement = findViewById(R.id.add_announcebtn);
        announcementsContainer = findViewById(R.id.announcementsContainer);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(genAnnounceActivity.this, home_classes.class);
                startActivity(intent);

            }
        });

        addAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(genAnnounceActivity.this, createAnnounceActivity.class);
                startActivity(intent);

            }
        });

        getData();
    }

    /**
     * Sends a GET request to get a list of all the announcements in backend server.
     * Adds each announcement to the UI
     */
    private void getData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_JSON_OBJECT,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Iterate through the array of announcements
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject announcement = response.getJSONObject(i);
                                String title = announcement.getString("title");
                                String text = announcement.getString("text");
                                String id = String.valueOf(announcement.getInt("id"));
                                Log.d("Announcement ID", "ID: " + id);
                                // Add the announcement to the UI
                                addAnnouncement(title, text, id); // Pass the id to the addAnnouncement method

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Called to dynamically add announcements to the UI, provided the title of the announcements
     *
     * @param title The title to display to the interface, indicating the announcement
     * @param announcementText Take the announcement details and pass to another activity
     * @param id passes this to details activity to identify the announcement needing to be accessed
     */
    private void addAnnouncement(String title, String announcementText, String id) {
        // Create a TextView for the announcement
        TextView view_announce = new TextView(this);
        view_announce.setText(title);
        view_announce.setTextSize(25);
        view_announce.setPadding(10, 10, 10, 10);

        // Create a "Details" button for the announcement
        Button detailsbtn = new Button(this);
        detailsbtn.setText("Details...");
        detailsbtn.setTextSize(10);
        detailsbtn.setBackgroundColor(Color.parseColor("#990000"));
        detailsbtn.setTextColor(Color.parseColor("#FFD300"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                200,
                100
        );

        params.setMargins(10, 10, 10, 10);

        detailsbtn.setLayoutParams(params);

        // Set the button's click listener to navigate to the details page
        detailsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(genAnnounceActivity.this, announceDetailsActivity.class);
                // Pass the announcement details to the next activity
                intent.putExtra("announcement_text", announcementText);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

        // Add the TextView and Button to the LinearLayout
        announcementsContainer.addView(view_announce);
        announcementsContainer.addView(detailsbtn);
    }
}
