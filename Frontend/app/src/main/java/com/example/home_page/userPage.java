package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
/**
 * The userPage class represents the main user interface after login,
 * allowing navigation to settings, class library, personal file repository, home, and class news.
 *  @author AyanAsim
 */
public class userPage extends AppCompatActivity {

    private Button settings;
    private Button classLib;
    private Button personalFileRepo;
    private ImageButton home;
    private Button classNews;
    private Button calendar;
    private Button classArchive;




    /**
     * Initializes the userPage activity, retrieves username and password from intent extras,
     * sets up UI components, and assigns listeners for navigation buttons.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        String username = getIntent().getStringExtra("USERNAME_KEY");
        String password = getIntent().getStringExtra("PASSWORD_KEY");

        // Now you can use this username in userPage as needed
        //Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();

        settings = findViewById(R.id.settingsBtn);
        classLib = findViewById(R.id.classLibBtn);
        personalFileRepo = findViewById(R.id.personalFileRepoBtn);
        home = findViewById(R.id.homeBtn);
        classNews = findViewById(R.id.classNewsButton);
        calendar = findViewById(R.id.calendarButton);
        classArchive = findViewById(R.id.classArchive);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, settings.class);
                intent.putExtra("USERNAME_KEY", username);
                intent.putExtra("PASSWORD_KEY", password);
                startActivity(intent);


            }
        });

        classLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, com.example.home_page.classLib.class );
                startActivity(intent);

            }
        });

        personalFileRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, personalFileRepoActivity.class);
                startActivity(intent);

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, home_classes.class);
                startActivity(intent);

            }
        });

        classNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, classNews.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);

            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, CalendarActivity.class);
                startActivity(intent);

            }
        });

        classArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userPage.this, classArchiveActivity.class);
                startActivity(intent);

            }
        });




    }
}
