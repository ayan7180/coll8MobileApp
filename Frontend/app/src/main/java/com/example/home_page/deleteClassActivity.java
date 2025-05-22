package com.example.home_page;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class deleteClassActivity extends AppCompatActivity {

    private TextInputEditText abbreviationInput;
    private TextInputEditText courseNumberInput;
    private Button classDelete;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deleteclasses);

        // Initialize views
        abbreviationInput = findViewById(R.id.classification_intxt);
        courseNumberInput = findViewById(R.id.course_intxt);
        classDelete = findViewById(R.id.delete_classbtn);
        homeButton = findViewById(R.id.homeButton);

        // Navigate back to home
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(deleteClassActivity.this, home_classes.class);
                startActivity(intent);
            }
        });

        // Perform delete operation
        classDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAbbreviation = abbreviationInput.getText().toString().trim();
                String strCourseNum = courseNumberInput.getText().toString().trim();

                if (strAbbreviation.isEmpty() || strCourseNum.isEmpty()) {
                    Toast.makeText(deleteClassActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int courseNum = Integer.parseInt(strCourseNum);
                    fetchCourseID(strAbbreviation, courseNum);
                } catch (NumberFormatException e) {
                    Toast.makeText(deleteClassActivity.this, "Invalid course number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCourseID(String abbreviation, int courseNum) {
        String fetchURL = "http://coms-3090-069.class.las.iastate.edu:8080/course";

        JsonArrayRequest fetchRequest = new JsonArrayRequest(
                Request.Method.GET,
                fetchURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject course = response.getJSONObject(i);
                                int id = course.getInt("id");
                                int fetchedCourseNum = course.getInt("courseNum");
                                String fetchedAbbreviation = course.getJSONObject("major").getString("abbreviation");

                                if (fetchedCourseNum == courseNum && fetchedAbbreviation.equalsIgnoreCase(abbreviation)) {
                                    String user = "Admin"; 
                                    String pass = "Admin";
                                    deleteClass(id, user, pass);
                                    return;
                                }
                            }
                            Toast.makeText(deleteClassActivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("Fetch Error", e.getMessage());
                            Toast.makeText(deleteClassActivity.this, "Error parsing course data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e("Fetch Error", responseData);
                        }
                        Toast.makeText(deleteClassActivity.this, "Error fetching courses", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(fetchRequest);
    }

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
                        Toast.makeText(deleteClassActivity.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e("Response Error", responseData);
                        }
                        Toast.makeText(deleteClassActivity.this, "Error deleting class", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(deleteRequest);
    }
}
