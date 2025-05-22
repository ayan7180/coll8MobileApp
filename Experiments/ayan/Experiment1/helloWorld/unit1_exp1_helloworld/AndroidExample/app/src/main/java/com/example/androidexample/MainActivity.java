package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView secondmessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        messageText.setText("Hello, my name is Ayan Asim");
        messageText.setTextSize(32);  //set the font size to 32

        // another message box
        secondmessage = findViewById(R.id.second_msg_txt);  //link to message textview in the Main activity XML
        secondmessage.setText("it is nice to meet you");
        secondmessage.setTextSize(16); //set font size to 16



    }
}