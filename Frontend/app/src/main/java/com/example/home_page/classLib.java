package com.example.home_page;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity class that displays a list of classes fetched from a remote server.
 * Provides navigation to the home screen and handles server responses.
 * @author AyanAsim*/
public class classLib extends AppCompatActivity {

    private ImageButton homeButton;
    private TextView classesList;
    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/course";

    /**
     * Called when the activity is created.
     * Sets up the user interface and initiates the process to fetch classes.
     * @param savedInstanceState Bundle containing the activity's previous state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_lib);

        homeButton = findViewById(R.id.homeButton);
        classesList = findViewById(R.id.classesLib);

        getClasses();


        homeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles click events for the home button.
             * Starts the home_classes activity.
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(classLib.this, home_classes.class);
                startActivity(intent);

            }
        });
    }
    /**
     * Fetches the list of classes from a server endpoint using a GET request.
     * Parses the JSON response and displays the class abbreviations and course numbers in the TextView.
     */
        private void getClasses () {
            // Create a GET request to retrieve the classes from the server
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, // Use GET method
                    URL_JSON_OBJECT, // The endpoint URL to fetch classes
                    null, // No request body needed for GET
                    new Response.Listener<JSONArray>() {
                        /**
                         * Handles the successful response from the server.
                         * Parses the JSON array response to extract and display class details.
                         * @param response The JSON array response containing class data.
                         */
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                // Initialize a StringBuilder to store the class list
                                StringBuilder classList = new StringBuilder();

                                // Loop through the response array and extract class details
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject classObject = response.getJSONObject(i);
                                    String abbreviation = classObject.getJSONObject("major").getString("abbreviation");
                                    int courseNum = classObject.getInt("courseNum");

                                    // Append each class to the StringBuilder
                                    classList.append(abbreviation).append(" ").append(courseNum).append("\n");
                                }

                                // Display the class list in the TextView
                                classesList.setText(classList.toString());

                            } catch (JSONException e) {
                                Log.e("JSONError", "Error parsing response: " + e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        /**
                         * Handles errors during the Volley request.
                         * Logs the error message.
                         * @param error The VolleyError encountered during the request.
                         */
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error", error.toString());
                            //Toast.makeText(FindClassActivity.this, "Error fetching classes", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Add the request to the Volley queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
        }
    /**
     * Creates a Volley JsonArrayRequest to handle server response for class retrieval.
     * Displays a toast message if the response is null and logs the response otherwise.
     */
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_JSON_OBJECT, null, new Response.Listener<JSONArray>() {
        /**
         * Handles the response from the server.
         * Displays a toast if no classes are found and logs the response if successful.
         * @param response The JSON array response containing class information.
         */
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(classLib.this, "Class not found", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Volley Response", response.toString());
                }
            }
        }, new Response.ErrorListener() {
        /**
         * Handles errors that occur during the Volley request.
         * Logs the error message.
         * @param error The VolleyError encountered during the request.
         */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        });
    }

