package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The SignUpActivity class provides an interface for new users to sign up,
 * return to the home screen, or delete an existing account.
 * It validates user input, sends a signup request to the server, and handles errors.

 @author AyanAsim */
public class SignUpActivity extends AppCompatActivity {

    private static final String URL_SIGNUP = "http://coms-3090-069.class.las.iastate.edu:8080/signup";
    private static final String URL_DELETE_ACCOUNT = "http://coms-3090-069.class.las.iastate.edu:8080/login/{id}";
    //private static final String URL_DELETE_ACCOUNT = "https://800869f2-a6cc-47f4-89d4-d0974acfbcc2.mock.pstmn.io/delete";
    //private static final String URL_SIGNUP = "https://5ddcebe4-fb17-4459-aecf-f8a02f0045a3.mock.pstmn.io/userDetails";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPassEditText;
    private Button signupButton;
    private Button homeButton;
    private Button deleteButton;

    /**
     * Initializes the SignUpActivity, sets up UI components, and defines button actions for
     * signing up, returning home, or deleting an account.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        confirmPassEditText = findViewById(R.id.re_enter_pass);
        signupButton = findViewById(R.id.login_btn);
        homeButton = findViewById(R.id.homeB);
        deleteButton = findViewById(R.id.DeleteAccount);

        // Set the signup button listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input from the user
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPassEditText.getText().toString();

                // Validate the input
                if (!username.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 8) {
                        if (password.equals(confirmPassword)) {
                            // Proceed with signup (this method now handles an array of requests)
                            registerUserBatch(username, password);
                        } else {
                            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password must be 8 characters or more", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter a username and password", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Set the home button listener
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, mainActivity.class);
                startActivity(intent);

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                // Proceed with account deletion
                if (!username.isEmpty()) {
                    deleteAccount(username);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a username to delete", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    /**
     * Registers a new user by sending the username and password in a JSON request.
     * Handles success by navigating to the login screen and displays appropriate error messages if registration fails.
     * @param username The username to register.
     * @param password The password associated with the username.
     */
    private void registerUserBatch(String username, String password) {
        // Create a JSONArray to match the expected backend format
        JSONArray jsonRequest = new JSONArray();

        // Add the username and password as strings to the array
        jsonRequest.put(username);
        jsonRequest.put(password);

        // Use StringRequest to handle the request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful signup response
                        Toast.makeText(SignUpActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        Log.d("Signup Response", response);

                        // Navigate to login screen
                        Intent intent = new Intent(SignUpActivity.this, loginAcivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("Signup Error", "Error code: " + error.networkResponse.statusCode + ", Message: " + errorMsg);
                        } else {
                            Log.e("Signup Error", "Unknown error: " + error.toString());
                        }
                        Toast.makeText(SignUpActivity.this, "Signup failed: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                // Convert the JSONArray to a string and send it in the body
                return jsonRequest.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Add the request to the request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Deletes a user account based on the provided username.
     * Sends a delete request to the server and displays the result to the user.
     * @param username The username of the account to be deleted.
     */
    private void deleteAccount(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL_DELETE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SignUpActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                        Log.d("Delete Response", response);

                        // Redirect to the home or login page after deletion
                        Intent intent = new Intent(SignUpActivity.this, mainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("Delete Error", "Error code: " + error.networkResponse.statusCode + ", Message: " + errorMsg);
                        } else {
                            Log.e("Delete Error", "Unknown error: " + error.toString());
                        }
                        Toast.makeText(SignUpActivity.this, "Failed to delete account: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                // Send username in request body for deletion (if required by API)
                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("username", username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return jsonRequest.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Add the request to the request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}

