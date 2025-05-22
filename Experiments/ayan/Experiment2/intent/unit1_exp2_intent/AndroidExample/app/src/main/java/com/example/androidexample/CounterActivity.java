package com.example.androidexample;

import
        androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt; // define number textview variable
    private Button increaseBtn; // define increase button variable
    private Button decreaseBtn; // define decrease button variable
    private Button backBtn;     // define back button variable
    private TextView prevoiusNumTxt; //defines the previous number variable
    private TextView nextNumTxt;  //defines the next number variable

    private int counter = 0;    // counter variable
    private int previousCounter =0; //prevoius counter variable
    private int nextCounter = 0; //next number variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        numberTxt = findViewById(R.id.number);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        decreaseBtn = findViewById(R.id.counter_decrease_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        prevoiusNumTxt = findViewById(R.id.previousNum);
        nextNumTxt = findViewById(R.id.nextNum);

        /* when increase btn is pressed, counter++, reset number textview. Also update the previous and next counter variables */
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCounter = counter;   //store current counter as previous
                counter++;
                numberTxt.setText(String.valueOf(counter));  //setting the current value of the counter
                prevoiusNumTxt.setText("The previous number was: " + previousCounter);
                nextCounter = counter + 1;  //calculate next number without changing the counter
                nextNumTxt.setText("The next number is: " + nextCounter);
            }
        });

        /* when decrease btn is pressed, counter--, reset number textview. Also update the previous and next counter variables */
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCounter = counter; //store current counter as previous
                counter--;
                numberTxt.setText(String.valueOf(counter));
                prevoiusNumTxt.setText("The previous number was: " + previousCounter);
                nextCounter = counter - 1;  //calculate next number without changing counter
                nextNumTxt.setText("The next number is: " + nextCounter);
            }
        });

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("NUM", String.valueOf(counter));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

    }
}