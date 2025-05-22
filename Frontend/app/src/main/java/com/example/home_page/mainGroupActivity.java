package com.example.home_page;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;

public class mainGroupActivity extends AppCompatActivity implements chatWebSocketListener {

    private static final String GETURL = "http://coms-3090-069.class.las.iastate.edu:8080/groups";
    private static final String POSTURL = "http://coms-3090-069.class.las.iastate.edu:8080/groups/";

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/";

    private String username;
    private String groupName;
    private ImageButton home;
    private LinearLayout groupsContainer;
    private Button create_group;

    //TODO get url from Soma
    String serverUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_gen);
        username = UserSingleton.getInstance().getUsername();
        String password = UserSingleton.getInstance().getPassword();

        groupsContainer = findViewById(R.id.groupsContainer);
        home = findViewById(R.id.homeButton);
        create_group = findViewById(R.id.create_group);




        get_groups(username, password);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainGroupActivity.this, home_classes.class);
                startActivity(intent);
            }
        });


        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainGroupActivity.this, groupCreateActivity.class);
                startActivity(intent);
            }
        });



        //if button for joining a class is pressed after retrieving all classes
        //websocket opened on another page

        if (chatWebSocketManager.getInstance().isWebSocketConnected()) {
            Intent intent = new Intent(mainGroupActivity.this, groupActivity.class);
            intent.putExtra("userName", username);
            intent.putExtra("group", groupName);

            startActivity(intent);
            finish();
        }

    }

    private void get_groups(String user, String pass){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                GETURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Iterate over the response array of courses
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject groupObject = response.getJSONObject(i);
                                String groupName = groupObject.getString("group");
                                String group = "Join: "+ groupName;

                                Button groupButton = new Button(mainGroupActivity.this);
                                groupButton.setText(group);
                                groupButton.setOnClickListener(v -> {
                                    serverUrl = URL + "chat/" + username + "/" + groupName;
                                    join_group(groupName);
                                });

                                // Add button to the LinearLayout (groupsContainer)
                                groupsContainer.addView(groupButton);

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

    private void join_group(String groupName){
        String URL = POSTURL + groupName;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            // Checks if group is joined successfully
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(mainGroupActivity.this, "Joining " + groupName, Toast.LENGTH_SHORT).show();

                                connectWebsocket(username, groupName);
                            } else {
                                Toast.makeText(mainGroupActivity.this, "Failed to join " + groupName, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("Error", "JSON Parsing Error: " + e.toString());
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

        // Add the request to the Volley queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void connectWebsocket(String username, String groupName){


        // Establish WebSocket connection and set listener
        chatWebSocketManager.getInstance().connectWebSocket(serverUrl);
        chatWebSocketManager.getInstance().setWebSocketListener(this);

        // go to chat activity
        Intent intent = new Intent(mainGroupActivity.this, groupActivity.class);
        intent.putExtra("userName", username);
        intent.putExtra("group", groupName);
        startActivity(intent);
        finish();
    }




    /**
     * Called when the WebSocket connection is opened successfully.
     * @param handshakedata The handshake data from the server.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}
    /**
     * Called when a message is received from the WebSocket server.
     * @param message The message received from the server.
     */
    @Override
    public void onWebSocketMessage(String message) {}
    /**
     * Called when the WebSocket connection is closed.
     * @param code The code indicating the reason for closure.
     * @param reason A description of why the connection was closed.
     * @param remote Indicates if the closure was initiated remotely.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}
    /**
     * Called when an error occurs with the WebSocket connection.
     * @param ex The exception thrown by the WebSocket connection.
     */
    @Override
    public void onWebSocketError(Exception ex) {}

}


//if (chatWebSocketManager.getInstance().isWebSocketConnected()) {
//}
