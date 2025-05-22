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
/**
 * ChatActivity provides a chat interface where users can send and receive messages
 * via WebSocket. This activity connects to a WebSocket server and handles incoming
 * and outgoing messages, as well as WebSocket connection events.
 * @author ayan*/
public class ChatActivity extends AppCompatActivity implements chatWebSocketListener {

    private Button sendBtn;
    private EditText messageEdt;
    private TextView messageTv;
    private ImageButton homeButton;
    private TextView usertxt;

    /**
     * Initializes the chat activity, sets up UI components, retrieves the username from
     * the intent, and establishes a WebSocket connection for real-time messaging.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        String userName = getIntent().getStringExtra("userName");


        /* initialize UI elements */
        sendBtn = findViewById(R.id.sendBtn);
        messageEdt = findViewById(R.id.messageEdt);
        messageTv = findViewById(R.id.tx1);
        homeButton = findViewById(R.id.homeButton);
        usertxt = (TextView) findViewById(R.id.usernametxt);

        usertxt.setText("Welcome User: "+userName);


        /* connect this activity to the websocket instance */
        chatWebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                // send message
                String message = messageEdt.getText().toString();
                chatWebSocketManager.getInstance().sendMessage(message);

                String currentText = messageTv.getText().toString();
                messageTv.setText(currentText + "\n" + userName+ ": "+ message);

                messageEdt.setText("");

            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage());
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, home_classes.class);
                startActivity(intent);

            }
        });
    }







    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

@Override
public void onWebSocketMessage(String message) {
    /**
     * In Android, all UI-related operations must be performed on the main UI thread
     * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
     * is used to post a runnable to the UI thread's message queue, allowing UI updates
     * to occur safely from a background or non-UI thread.
     */
    Log.d("WebSocketMessage", message);
    runOnUiThread(() -> {
        String s = messageTv.getText().toString();
        messageTv.setText(s + "\n"+message);
    });
}

    /**
     * Called when the WebSocket connection is closed. Updates the chat TextView
     * to show a connection closure message.
     * @param code Closure code representing the reason for disconnection.
     * @param reason Textual reason for the WebSocket disconnection.
     * @param remote True if the disconnection was initiated by the server, false if locally.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = messageTv.getText().toString();
            messageTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }
    /**
     * Called when there is an error with the WebSocket connection.
     * @param ex The exception that occurred.
     */
    @Override
    public void onWebSocketError(Exception ex) {}
}