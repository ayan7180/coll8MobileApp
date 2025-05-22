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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class manageUsers extends AppCompatActivity {

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/login/all";
    private LinearLayout usersContainer;
    private ImageButton home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_users);



        usersContainer = findViewById(R.id.usersContainer);
        home = findViewById(R.id.homeButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(manageUsers.this, home_classes.class);
                startActivity(intent);

            }
        });

        //gets all users
        getAllUsers();
    }

    private void getAllUsers() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Iterate over the response array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String username = userObject.getString("username");
                                int userID = userObject.getInt("id");

                                addUser(username,userID);
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing response: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error: " + error.toString());
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Dynamically adds a user with a button to the UI.
     * @param username The username to display.
     */
    private void addUser(String username, int userID) {
        //TextView for the username
        TextView userTextView = new TextView(this);
        userTextView.setText(username);
        userTextView.setTextSize(18);
        userTextView.setPadding(10, 10, 10, 10);

        // button for the user
        Button userButton = new Button(this);
        userButton.setText("delete");
        userButton.setTextSize(12);
        userButton.setBackgroundColor(Color.parseColor("#990000"));
        userButton.setTextColor(Color.parseColor("#FFD300"));

        // Set button params
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(10, 10, 10, 10);
        userButton.setLayoutParams(buttonParams);

        // delete user
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(username, userID);

            }
        });

        //updated the xml/UI
        usersContainer.addView(userTextView);
        usersContainer.addView(userButton);
    }

    public void deleteUser(String username, int userId) {
        String url = "http://coms-3090-069.class.las.iastate.edu:8080/login/" + userId;

        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Check response
                            String message = response.getString("message");
                            Log.d("DeleteUser", "Response: " + message);

                            if ("User deleted".equals(message)) {
                                Toast.makeText(getApplicationContext(), "User " + username + " has been deleted", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(manageUsers.this, home_classes.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle user not found
                                Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing response: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error: " + error.toString());
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }


}
