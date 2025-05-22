package com.example.home_page;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

/**
 * The settings class provides an interface for users to view and modify their settings.
 * It allows changing the theme to dark mode, updating profile pictures, and navigating to different screens.
 *  @author AyanAsim
 */
public class settings extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton button;
    Button backButton;
    Button darkMode;
    Boolean isNightModeOn;
    ImageButton homeButton;
    TextView usernameText;
    TextView passwordText;



    /**
     * Initializes the settings activity, sets up UI elements, and defines click actions for buttons.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingspage);
        String username = getIntent().getStringExtra("USERNAME_KEY");
        String password = getIntent().getStringExtra("PASSWORD_KEY");

        usernameText = findViewById(R.id.username);
        usernameText.setText("Username:  " + username);

        passwordText = findViewById(R.id.password);
        passwordText.setText("Password: " + password);

        darkMode = findViewById(R.id.darkModeButton);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            isNightModeOn = false;
            darkMode.setText("Enable dark mode");
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            isNightModeOn = true;
            darkMode.setText("Disable dark mode");
            
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.black)));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.black)));
        }

        imageView = findViewById(R.id.pictureWidget);
        button = findViewById(R.id.cameraIcon);
        backButton = findViewById(R.id.backButton);
        homeButton = findViewById(R.id.homeButton);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(settings.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();


            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this, home_classes.class);
                startActivity(intent);

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this, userPage.class);
                startActivity(intent);

            }
        });

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkMode.setText("Enable dark mode");
                    isNightModeOn = false;
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    isNightModeOn = true;
                    darkMode.setText("Disable dark mode");
                }


            }
        });


    }
    /**
     * Handles the result of the image picker activity.
     * Sets the chosen image to the image view.
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode The result code returned by the child activity.
     * @param data The data returned as an Intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageView.setImageURI(uri);
    }
}
