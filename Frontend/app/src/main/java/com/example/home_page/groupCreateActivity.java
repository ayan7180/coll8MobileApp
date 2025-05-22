package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class groupCreateActivity extends AppCompatActivity {

    private static final String CREATE_GROUP_URL = "http://coms-3090-069.class.las.iastate.edu:8080/groups/"; // URL for creating a group
    String username = UserSingleton.getInstance().getUsername();

    private EditText groupNameInput;
    private Button createGroupButton;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_creation);

        groupNameInput = findViewById(R.id.group_name_input);
        createGroupButton = findViewById(R.id.create_group);
        homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(groupCreateActivity.this, home_classes.class);
                startActivity(intent);
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameInput.getText().toString().trim();

                if (groupName.isEmpty()) {
                    Toast.makeText(groupCreateActivity.this, "Group Name is required", Toast.LENGTH_SHORT).show();
                } else {
                    createGroup(groupName);
                }
            }
        });
    }

    private void createGroup(String groupName) {
        String URL = CREATE_GROUP_URL + groupName;
        JSONObject requestBody = new JSONObject();
        try {
            // Prepare the JSON request body with the group name
            requestBody.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(groupCreateActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(groupCreateActivity.this, home_classes.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(groupCreateActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(groupCreateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any Volley errors (network issues, etc.)
                        Log.e("VolleyError", "Error: " + error.toString());
                        Toast.makeText(groupCreateActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
