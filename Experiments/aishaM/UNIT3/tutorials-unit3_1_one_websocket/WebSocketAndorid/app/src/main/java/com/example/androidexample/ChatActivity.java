package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity extends AppCompatActivity implements WebSocketListener{

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;
    private Button home;
    private TextView usertxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String userName = getIntent().getStringExtra("userName");



        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);
        home = (Button) findViewById(R.id.homebtn);
        usertxt = (TextView) findViewById(R.id.usernametxt);

        usertxt.setText("Welcome User: "+userName);

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                String message = msgEtx.getText().toString();
                WebSocketManager.getInstance().sendMessage(message);

                String currentText = msgTv.getText().toString();
                msgTv.setText(currentText + "\n" + userName+ ": "+message);

                // Clear the txtbox
                msgEtx.setText("");
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
        home.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
        });



    }


    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\n"+message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}