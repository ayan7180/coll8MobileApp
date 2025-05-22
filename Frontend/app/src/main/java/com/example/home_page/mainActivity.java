package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

/**
 * The mainActivity class serves as the entry point of the app, providing options for login and sign-up.
 * It initializes UI elements and defines actions to navigate to the login and sign-up screens.
 *
 @author AyanAsim */
public class mainActivity extends AppCompatActivity {

    private static final String URL_JSON_OBJECT = "http://coms-3090-069.class.las.iastate.edu:8080/class";

    private Button loginButton;
    private Button signupButton;

    /**
     * Initializes the activity, setting up the layout and click listeners for navigation buttons.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initalize UI elements */

        loginButton = findViewById(R.id.loginHomeBtn);
        signupButton = findViewById(R.id.signupHomeBtn);
        /**
         * Navigates to the login page when the login button is clicked.
         */
       loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Intent intent = new Intent(mainActivity.this, loginAcivity.class);
               startActivity(intent);

           }
       });
        /**
         * Navigates to the sign-up page when the sign-up button is clicked.
         */
       signupButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(mainActivity.this, SignUpActivity.class);
               startActivity(intent);
           }
       });

    }
}
