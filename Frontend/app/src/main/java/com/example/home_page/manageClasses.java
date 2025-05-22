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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class manageClasses extends AppCompatActivity {

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/course";

    private LinearLayout coursesContainer;
    private ImageButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_courses);
        String user = UserSingleton.getInstance().getUsername();
        String pass = UserSingleton.getInstance().getPassword();

        coursesContainer = findViewById(R.id.coursesContainer);
        home = findViewById(R.id.homeButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(manageClasses.this, home_classes.class);
                startActivity(intent);

            }
        });

        // Get all courses
        getAllCourses(user, pass);
    }

    private void getAllCourses(String user, String pass) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Iterate over the response array of courses
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseObject = response.getJSONObject(i);
                                JSONObject majorObject = courseObject.getJSONObject("major");
                                String abbreviation = majorObject.getString("abbreviation");
                                int courseID = courseObject.getInt("id");
                                int courseNum = courseObject.getInt("courseNum");
                                String title = courseObject.getString("title");

                                // Log the course info for debug purposes!
                                Log.d("Course", "Course: " + abbreviation + " " + courseNum+ ""+ courseID);

                                // Add to UI with a button to delete the course
                                addCourse(courseID, abbreviation, courseNum, title, user, pass);
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
     * Dynamically adds a course with a button to the UI.
     *
     * @param abbreviation The course abbreviation to display.
     * @param courseNum    The course number to display.
     */
    private void addCourse(int courseID, String abbreviation, int courseNum, String title, String user, String pass) {
        // Create a TextView for the course abbreviation and number
        TextView courseTextView = new TextView(this);
        courseTextView.setText(abbreviation + " " + courseNum + ": " + title);
        courseTextView.setTextSize(18);
        courseTextView.setPadding(10, 10, 10, 10);

        // Create a button to delete the course
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setBackgroundColor(Color.parseColor("#990000"));
        deleteButton.setTextColor(Color.parseColor("#FFD300"));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(10, 10, 10, 10);
        deleteButton.setLayoutParams(buttonParams);

        // delete btn
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClass(courseID, user, pass);
//                delURL+= courseID;
                Log.d("DeleteCourse", "Deleting course: " + abbreviation + " " + courseNum);
            }
        });

        // Add the TextView and Button to the coursesContainer (LinearLayout)
        coursesContainer.addView(courseTextView);
        coursesContainer.addView(deleteButton);
    }


    private static final String BOUNDARY = "boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
    // + System.currentTimeMillis()
    private void deleteClass(int courseID, String user, String pass) {
        String delURL = "http://coms-3090-069.class.las.iastate.edu:8080/course/delete/" + courseID + "/" + user + "/" + pass;

        Log.d("Delete Course URL", delURL);

        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.POST,
                delURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Delete Success", response.toString());
                        Toast.makeText(manageClasses.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e("Response Error", responseData);
                        }
                        Toast.makeText(manageClasses.this, "Error deleting class", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }


}

