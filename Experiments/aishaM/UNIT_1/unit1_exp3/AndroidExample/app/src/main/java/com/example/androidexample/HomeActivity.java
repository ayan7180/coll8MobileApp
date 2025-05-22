package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private Button goHomeButton;
    private TextView newUserTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        newUserTxt = findViewById(R.id.username_txt);
        goHomeButton = findViewById(R.id.home_btn);


        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("USERNAME");
        if(extras == null) {
            newUserTxt.setVisibility(View.INVISIBLE);             // set username text invisible initially
        } else {
            newUserTxt.setText(extras.getString("USERNAME")); // this will come from SignupActivity
        }

        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();;
            }
        });

    }

}
