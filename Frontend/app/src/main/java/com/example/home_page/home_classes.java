package com.example.home_page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The home_classes activity provides options for managing classes and viewing announcements.
 * Fetches the list of available classes and displays them as clickable buttons.
 *  @author AyanAsim
 */
public class home_classes extends AppCompatActivity {

    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/course";
    //private static final String URL_JSON_OBJECT = "https://bd099428-adbc-4479-8338-bb6d88856adf.mock.pstmn.io";



    private Button homeButton;
    private Button clickAnnouncements;
    private Button addClasses;
    private Button deleteClasses;
    private LinearLayout classesLayout;
    private TextView listClasses;
    private Button userPageButton;
    private Button fileButton;
    private Button upload;
    private Button manageU;
    private Button manageC;
    private String userType;
    private Button groupButton;


    /**
     * Initializes the activity, sets up UI elements, and loads classes.
     * @param savedInstanceState Bundle containing the activity's previous state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes_page);


        String username = getIntent().getStringExtra("USERNAME_KEY");
        String password = getIntent().getStringExtra("PASSWORD_KEY");
        userType = UserSingleton.getInstance().getUserType();


        //Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();

        homeButton = findViewById(R.id.homeB);
        clickAnnouncements = findViewById(R.id.clickAnn);
        addClasses = findViewById(R.id.addClasses);
        classesLayout = findViewById(R.id.classes_layout);
        deleteClasses = findViewById(R.id.deleteClasses);
        listClasses = findViewById(R.id.classestxt);
        userPageButton = findViewById(R.id.userPageButton);
        fileButton = findViewById(R.id.viewFiles);
        upload = findViewById(R.id.uploadNotes);
        groupButton = findViewById(R.id.groupsBtn);

        manageC = findViewById(R.id.manageClasses);
        manageU = findViewById(R.id.manageUsers);


        if (userType.equals("Admin")){
            manageC.setVisibility(View.VISIBLE);
            manageU.setVisibility(View.VISIBLE);
        }



        getClasses();  // Call method to load created classes
        /**
         * Navigates to the main activity (home page).
         */
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(home_classes.this, mainActivity.class);
               startActivity(intent);

            }
        });
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, mainGroupActivity.class);
                startActivity(intent);
            }
        });
        /**
         * Navigates to the file repository activity, passing the username.
         */
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, fileRepoActivity.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);

            }
        });

        /**
         * Navigates to the announcements activity.
        */
        clickAnnouncements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, genAnnounceActivity.class);
                startActivity(intent);

            }
        });
        /**
        * Navigates to the announcements activity.
        */
        addClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, createClassActivity.class);
                startActivity(intent);

            }
        });
        /**
        * Navigates to the activity for deleting classes.
        */
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, deleteClassActivity.class);
                startActivity(intent);


            }
        });
        /**
        * Navigates to the user page activity, passing username and password.
        */
        userPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, userPage.class);
                intent.putExtra("USERNAME_KEY", username);
                intent.putExtra("PASSWORD_KEY", password);
                startActivity(intent);

            }
        });
        /**
        * Navigates to the upload activity, passing the username.
        */
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, uploadActivity.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);

            }
        });

        /**
         * Visible to Admin only, takes admin to a page where all users can be seen
         */
        manageU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, manageUsers.class);
                startActivity(intent);

            }
        });

        /**
         * Visible to Admin only, takes admin to a page where all classes can be seen
         */
        manageC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_classes.this, manageClasses.class);
                startActivity(intent);

            }
        });


        }

    /**
     * Fetches the list of classes from the server using a GET request.
     * Creates a button for each class, adding it to the layout and setting click events.
     */
    private void getClasses() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_JSON_OBJECT,
                null,
                new Response.Listener<JSONArray>() {
                    /**
                     * Handles the successful response by creating a button for each class.
                     * @param response The JSON array containing class details.
                     */
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Clear any existing buttons
                            classesLayout.removeAllViews();

                            if (response.length() > 0) {
                                // Hide "No classes listed yet..." text
                                listClasses.setVisibility(View.GONE);
                            } else {
                                listClasses.setVisibility(View.VISIBLE);
                                listClasses.setText("No classes listed yet...");
                            }

                            // Create buttons for each class
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject classObject = response.getJSONObject(i);
                                String abbreviation = classObject.getJSONObject("major").getString("abbreviation");
                                int courseNum = classObject.getInt("courseNum");

                                // Create a Button for each class
                                Button classButton = new Button(home_classes.this);
                                classButton.setText(abbreviation + " " + courseNum);
                                classButton.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                                classButton.setTextColor(getResources().getColor(R.color.white));


                                /**
                                 * Opens the class repository activity when a class button is clicked.
                                 * Passes the course number and abbreviation to the new activity.
                                 * @param view The button view that was clicked.
                                 */
                                classButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(home_classes.this, classRepoActivity.class);
                                        intent.putExtra("courseNum", courseNum);
                                        intent.putExtra("abbreviation", abbreviation);
                                        startActivity(intent);

                                        Toast.makeText(home_classes.this, "Clicked on " + abbreviation + " " + courseNum, Toast.LENGTH_SHORT).show();

                                    }
                                });

                                // Add the button to the classes layout
                                classesLayout.addView(classButton);
                            }

                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing response: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    /**
                     * Handles errors during the request.
                     * Logs the error message.
                     * @param error The VolleyError encountered during the request.
                     */
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
    /**
     * Request object to handle server response for class retrieval.
     * Displays a toast message if no classes are found and logs the response otherwise.
     */
    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_JSON_OBJECT, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response == null){
                        Toast.makeText(home_classes.this, "Class not found",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("Volley Response", response.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                }
            });






        }
