
package com.example.home_page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The loginActivity class handles user login, credential storage, and data retrieval.
 * It interacts with an external API to authenticate users and manages locally stored login credentials.
 */
public class loginAcivity extends AppCompatActivity {

    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/login"; // URL for login API
//    private static final String URL_JSON_OBJECT = "https://d79de652-5487-4120-927e-b5f48a90d1e7.mock.pstmn.io/logindet";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button homeButton;
    private Button rememberMeButton;
    String userType;


    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";



    /**
     * Initializes the login activity, setting up UI elements and loading saved credentials.
     * @param savedInstanceState Bundle containing the activity's previous state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);


        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_btn);
        homeButton = findViewById(R.id.homeB);
        rememberMeButton = findViewById(R.id.rememberMe);

        // Load saved credentials
        loadSavedCredentials();

        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Initiates login on button click, validating input and starting the loginUser method.
             * Redirects to the home_classes activity if login is successful.
             * @param view The login button view.
             */
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.equals("Admin") && password.equals("Admin")){
                    userType = "Admin";

                }else{
                    userType = "user";
                }
                Log.d("user", "UserType" + userType);



                if (!username.isEmpty() && !password.isEmpty()) {
                    // Make a login request with the entered username and password
                    loginUser(username, password);
                    Intent intent = new Intent(loginAcivity.this, home_classes.class);
                    intent.putExtra("USERNAME_KEY", username);
                    intent.putExtra("PASSWORD_KEY", password);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Enter a username and password", Toast.LENGTH_LONG).show();
                }

            }
        });

        rememberMeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Saves user credentials on button click using SharedPreferences.
             * @param view The remember me button view.
             */
            @Override
            public void onClick(View view) {
                saveCredentials();

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Navigates back to the main activity (home page).
             * @param view The home button view.
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginAcivity.this, mainActivity.class);
                startActivity(intent);

            }
        });

        // This is where you can perform other API requests like fetching data
        fetchClassData();
    }
    /**
     * Authenticates the user by sending a POST request with the username and password to the login API.
     * Displays a success message and navigates to the home_classes activity upon successful login.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    private void loginUser(String username, String password) {
        // Create a JSON object for the credentials
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserSingleton.getInstance().setUsername(username);
        UserSingleton.getInstance().setPassword(password);
        UserSingleton.getInstance().setUserType(userType);



        // Send the POST request for login
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL_JSON_OBJECT, loginData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Handle successful login response
                Toast.makeText(loginAcivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Log.d("Login Response", response.toString());

                // Navigate to the next screen or home screen
                Intent intent = new Intent(loginAcivity.this, home_classes.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                Log.e("Login Error", error.toString());
                //Toast.makeText(loginAcivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(loginRequest);
    }
    /**
     * Sends a GET request to retrieve data related to available classes.
     * Displays a toast message if no classes are found, otherwise logs the response.
     */
    private void fetchClassData() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_JSON_OBJECT, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(loginAcivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Volley Response", response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
    /**
     * Saves the entered username and password using SharedPreferences.
     * Ensures credentials are stored locally for future logins.
     */
    private void saveCredentials() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.apply();

            //Toast.makeText(this, "Credentials saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Loads saved username and password values from SharedPreferences.
     * If saved credentials exist, they are pre-filled into the login fields.
     *  @author AyanAsim
     */
    private void loadSavedCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            //Toast.makeText(this, "Credentials loaded!", Toast.LENGTH_SHORT).show();
        }
    }
}
