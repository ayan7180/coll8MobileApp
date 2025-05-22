package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONObject;
import android.util.Log;


/**
 * This class adds functionality to the General Announcement page,
 * allowing users to view announcement details when a button is pressed on the general page.
 *
 @author Aisha Mohamed
 */
public class announceDetailsActivity extends AppCompatActivity {

    private ImageButton home;
    private TextView details;
    private Button understood;
    private Button update;
    private Button delete;
    private String text;
    private String id;
    private static final String url = "http://coms-3090-069.class.las.iastate.edu:8080/announcements";


    /**
     *
     * @param id The User ID is passed to identify what announcement to delete.
     */
    public void deleteAnnouncement(String id) {
        String url = "http://coms-3090-069.class.las.iastate.edu:8080/announcements/" + id; // Update URL accordingly

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from the server
                        Log.d("Delete Response", response);
                        // Optionally, update the UI or notify the user of success
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("Delete Error", error.toString());
                        // Optionally, notify the user of the error
                    }
                }
        );

        // Add the request to the RequestQueue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views and listeners.
     *
     * @param saved_instance Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate (Bundle saved_instance) {
        super.onCreate(saved_instance);
        setContentView(R.layout.announce_details);

        home = findViewById(R.id.homeButton);
        details = findViewById(R.id.announce_detailstxt);
        understood = findViewById(R.id.finish_announcebtn);
        update = findViewById(R.id.updatebtn);
        delete = findViewById(R.id.delete_announce);
        Intent intent = getIntent();

        // Check if the Intent has extras
        if (intent != null && intent.hasExtra("announcement_text")) {
            text = intent.getStringExtra("announcement_text");
        }
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getStringExtra("id"); // Store the ID for later use
        }
        String user = UserSingleton.getInstance().getUsername();
        Log.d("User", "Username stored in Singleton: " + user);

        details.setText(text);


        //listener for home button
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(announceDetailsActivity.this, home_classes.class);
                startActivity(intent);

            }
        });

        //listener for got it button
        understood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(announceDetailsActivity.this, genAnnounceActivity.class);
                startActivity(intent);

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(announceDetailsActivity.this, updateAnnounceActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnnouncement(id);

                Intent intent = new Intent(announceDetailsActivity.this, genAnnounceActivity.class);
                startActivity(intent);

            }
        });
        
    }

}
