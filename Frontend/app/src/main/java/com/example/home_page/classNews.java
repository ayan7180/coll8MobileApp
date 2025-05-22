package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
 * Activity class that displays news related to classes.
 * Retrieves class announcements and recent files from a server.
 * Author: Ayan Asim
 */
public class classNews extends AppCompatActivity {
    private ImageButton home;
    private TextView newsList;
    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/library/Simon";

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets up an onClick listener for the home button.
     * @param savedInstanceState Bundle containing the activity's previous state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classnews_page);
        String username = getIntent().getStringExtra("USERNAME_KEY");

        home = findViewById(R.id.homeButton);
        newsList = findViewById(R.id.newsLib);

        home.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the home button.
             * Starts the home_classes activity.
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(classNews.this, home_classes.class);
                startActivity(intent);

            }
        });

        getClassNews();
    }

    /**
     * Fetches class news from the server as a JSON array and displays announcements and recent files.
     * Uses a Volley JsonArrayRequest to retrieve data and handle responses.
     */
    private void getClassNews() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_JSON_OBJECT,
                null,
                new Response.Listener<JSONArray>() {
                    /**
                     * Handles the successful response from the server.
                     * Parses the JSON response to display announcements and recent files in the newsList TextView.
                     * @param response The JSON array response containing class news.
                     */
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Check if response has at least one element
                            if (response.length() == 0) {
                                newsList.setText("No news available right now.");
                                return;
                            }

                            // Assuming the first object in the array is the one of interest
                            JSONObject courseThumbnail = response.getJSONObject(0);

                            // Extract course information
                            JSONObject course = courseThumbnail.optJSONObject("course");
                            String courseNum = course != null ? String.valueOf(course.optInt("courseNum", 0)) : "N/A";
                            String title = course != null ? course.optString("title", "N/A") : "N/A";
                            String abbreviation = course != null ? course.optJSONObject("major").optString("abbreviation", "N/A") : "N/A";
                            String courseTitle = course != null ? course.optJSONObject("major").optString("title", "null") : "null";

                            // Extract recent announcements
                            JSONArray announcementsArray = courseThumbnail.optJSONArray("recentAnnouncements");
                            StringBuilder announcements = new StringBuilder();
                            if (announcementsArray != null && announcementsArray.length() > 0) {
                                for (int i = 0; i < announcementsArray.length(); i++) {
                                    String announcement = announcementsArray.optString(i);
                                    announcements.append("- ").append(announcement).append("\n");
                                }
                            } else {
                                announcements.append("No recent announcements.\n");
                            }

                            // Extract recent files
                            JSONArray filesArray = courseThumbnail.optJSONArray("recentFiles");
                            StringBuilder files = new StringBuilder();
                            if (filesArray != null && filesArray.length() > 0) {
                                for (int i = 0; i < filesArray.length(); i++) {
                                    JSONObject fileObject = filesArray.optJSONObject(i);
                                    if (fileObject != null) {
                                        String fileName = fileObject.optString("fileName", "Unnamed File");
                                        String filePath = fileObject.optString("filePath", "Unknown Path");
                                        files.append("- ").append(fileName).append(" (").append(filePath).append(")").append("\n");
                                    }
                                }
                            } else {
                                files.append("No recent files.\n");
                            }

                            int numAnnouncements = courseThumbnail.optInt("numAnnouncements", 0);
                            int numFiles = courseThumbnail.optInt("numFiles", 0);

                            // Format all data into a single string
                            StringBuilder newsContent = new StringBuilder();
                            newsContent.append("CourseNum: ").append(courseNum).append("\n")
                                    .append("Title: ").append(title).append("\n")
                                    .append("Abbreviation: ").append(abbreviation).append("\n")
                                    .append("Title: ").append(courseTitle).append("\n\n")
                                    .append("Recent Announcements (").append(numAnnouncements).append("):\n")
                                    .append(announcements.toString()).append("\n")
                                    .append("Recent Files (").append(numFiles).append("):\n")
                                    .append(files.toString());

                            // Display the formatted information in the TextView
                            newsList.setText(newsContent.toString());

                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing response: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    /**
                     * Handles errors that occur during the Volley request.
                     * Logs the error message.
                     * @param error The VolleyError encountered during the request.
                     */
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        newsList.setText("Error fetching news. Please try again later.");
                    }
                }
        );

        // Add the request to the Volley queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}
