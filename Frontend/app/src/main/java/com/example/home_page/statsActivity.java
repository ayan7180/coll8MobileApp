package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class gives users an update on their status, on how many files uploaded and annotations made
 * Provides functionality for navigation back to the home screen.
 *
 @author Aisha Mohamed
 */
public class statsActivity extends AppCompatActivity {

    private ImageButton homeButton;
    private TextView files;
    private TextView annotate;
    private Button moveOnBtn;
    private String fileType;
    private int fileId;
    private String user;


    private int uploadCount;
    private int annotationCount = 0;

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views and listeners.
     *
     * Takes updated counts and displays it on the provided layout
     *
     * @param savedInstanceState Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_page);

        homeButton = findViewById(R.id.homeButton);
        files = findViewById(R.id.filesUploadedText);
        annotate = findViewById(R.id.annotationsText);
        moveOnBtn = findViewById(R.id.moveOnButton);


        Intent intent = getIntent();
        uploadCount = intent.getIntExtra("files uploaded", 0);
        String title = intent.getStringExtra("Title");
        user = intent.getStringExtra("username");

        // update TextViews with the current counts
        files.setText(uploadCount + " Files Uploaded!");
        annotate.setText(annotationCount + " Annotations!");


        homeButton.setOnClickListener(v -> {
            Intent homeintent = new Intent(statsActivity.this, home_classes.class);
            startActivity(homeintent);
        });

        moveOnBtn.setOnClickListener(v -> {
            Intent leaveintent = new Intent(statsActivity.this, fileRepoActivity.class);
            intent.putExtra("Title", title);
            intent.putExtra("username", user);

            startActivity(leaveintent);
        });

    }

}
