package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;

public class groupActivity extends AppCompatActivity implements chatWebSocketListener {

    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/";
    private String username;
    private String groupName;
    private ImageButton home;
    private String abbreviation;
    private int courseNum;
    private EditText messageInput;
    private Button sendButton;
    private TextView conversationText;
    private TextView displayGName;
    private String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupchat);

        username = UserSingleton.getInstance().getUsername();

        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        conversationText = findViewById(R.id.tx1);
        displayGName = findViewById(R.id.display_group);

        Intent intent = getIntent();
        if (intent != null) {
            groupName = intent.getStringExtra("group");
        }

        displayGName.setText("Welcome " + username + ", to " + groupName+"!");
        serverUrl = URL+"chat/"+username+"/"+groupName;

        //websocket setup
        chatWebSocketManager.getInstance().setWebSocketListener(this);
        chatWebSocketManager.getInstance().connectWebSocket(serverUrl);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(groupActivity.this, home_classes.class);
                startActivity(intent);
            }
        });
        sendButton.setOnClickListener(v -> {
            try {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    chatWebSocketManager.getInstance().sendMessage(message);
                    String currentText = conversationText.getText().toString();
                    conversationText.setText(currentText + "\n" + username + ": " + message);
                    messageInput.setText("");
                }
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connection opened");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocketMessage", message);
        runOnUiThread(() -> {
            String currentText = conversationText.getText().toString();
            conversationText.setText(currentText + "\n" + message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String currentText = conversationText.getText().toString();
            conversationText.setText(currentText + "\n---\nConnection closed by " + closedBy + "\nReason: " + reason);
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocketError", ex.getMessage());
    }
}
