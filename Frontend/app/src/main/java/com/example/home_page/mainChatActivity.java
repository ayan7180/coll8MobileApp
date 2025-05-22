package com.example.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;
/**
 * The mainChatActivity class provides a UI for connecting to a chat server via WebSocket.
 * It allows the user to enter a server URL and username, and upon connecting, navigates to the chat screen.
 *  @author AyanAsim
 */
public class mainChatActivity extends AppCompatActivity implements chatWebSocketListener {

    private Button connectButton;
    private EditText serverEdt, usernameEdt;
    private String username;
    /**
     * Initializes the activity, setting up the layout and click listener for the connect button.
     * @param savedInstanceState If non-null, this activity is being re-initialized from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainchatact);

        /* initialize UI elements */
        connectButton = findViewById(R.id.connectBtn);
        serverEdt = findViewById(R.id.serverEdt);
        usernameEdt = findViewById(R.id.usernameEdt);


        /**
         * Listener for the connect button. Connects to the WebSocket server using the entered URL and username,
         * then navigates to the ChatActivity for chat interactions.
         */
        connectButton.setOnClickListener(view -> {
            String serverUrl = serverEdt.getText().toString();
            String username = usernameEdt.getText().toString();

            // Establish WebSocket connection and set listener
            chatWebSocketManager.getInstance().connectWebSocket(serverUrl);
            chatWebSocketManager.getInstance().setWebSocketListener(this);

            // go to chat activity
            Intent intent = new Intent(mainChatActivity.this, ChatActivity.class);
            intent.putExtra("userName", username);
            startActivity(intent);
        });
    }


    /**
     * Called when the WebSocket connection is opened successfully.
     * @param handshakedata The handshake data from the server.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}
    /**
     * Called when a message is received from the WebSocket server.
     * @param message The message received from the server.
     */
    @Override
    public void onWebSocketMessage(String message) {}
    /**
     * Called when the WebSocket connection is closed.
     * @param code The code indicating the reason for closure.
     * @param reason A description of why the connection was closed.
     * @param remote Indicates if the closure was initiated remotely.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}
    /**
     * Called when an error occurs with the WebSocket connection.
     * @param ex The exception thrown by the WebSocket connection.
     */
    @Override
    public void onWebSocketError(Exception ex) {}
}
