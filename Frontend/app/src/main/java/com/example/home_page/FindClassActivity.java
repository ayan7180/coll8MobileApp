package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity to search and display class information.
 * Allowing users to know what classes exist.
 * Provides functionality for navigation back to the home screen.
 * @author Aisha Mohamed
 */
public class FindClassActivity extends AppCompatActivity{

    private Button addClass;
    private Button home;
    private SearchView searchView;
    private TextView listClasses;
    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/course/search";


    /**
     *Sends a POST request to get class information based on the query.
     *Updates the layout with the class details upon a successful response.
     *
     * @param query An array containing information on class abbreviation and course number
     */
    private void postReq(String[] query) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_JSON_OBJECT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);

                            // Extract the course number and major abbreviation
                            int courseNum = jsonResponse.getInt("courseNum");
                            String abbreviation = jsonResponse.getJSONObject("major").getString("abbreviation");

                            // Display the abbreviation and course number in the TextView
                            String displayText = abbreviation + " " + courseNum;
                            listClasses.setText(displayText);

                        }catch(JSONException e){
                            Log.e("JSONError", "Error parsing response: " + e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Create a JSON object to send in the request body
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("abbreviation", query[0].split(" ")[1]);
                    jsonBody.put("courseNum", query[1].split(" ")[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(); // Convert JSON object to bytes
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set the Content-Type header to application/json
                return headers;
            }
        };

        // Add request to Volley queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
        setContentView(R.layout.find_class);
        ImageButton home = findViewById(R.id.homeButton);
        addClass = findViewById(R.id.add_classbtn);
        searchView = findViewById(R.id.search_class);
        listClasses = findViewById(R.id.getClassestxt);



        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindClassActivity.this, home_classes.class);
                startActivity(intent);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String [] queryArr  = query.split(" ",2);
                if (queryArr.length<2){
                    Toast.makeText(FindClassActivity.this, "Invalid search", Toast.LENGTH_SHORT).show();
                }else {
                    queryArr[0] = "abbreviation " + queryArr[0];
                    queryArr[1] = "courseNum " + queryArr[1];
                }

                if (query.isBlank()){
                    Toast.makeText(FindClassActivity.this, "Enter a valid search", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Talk about how this is used
                postReq(queryArr);

                //output whatever course I received
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //prolly implement as a list, so when the query is changed theres a dropdown of all the classes
                return false;
            }
        });
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindClassActivity.this, createClassActivity.class);
                startActivity(intent);

            }
        });

    }
}
