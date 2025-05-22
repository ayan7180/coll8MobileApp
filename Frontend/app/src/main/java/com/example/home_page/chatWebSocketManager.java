package com.example.home_page;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
/**
 * Manages WebSocket connections for chat functionality. This singleton class handles
 * connecting, sending messages, and disconnecting from a WebSocket server, and notifies
 * a listener of connection events.
 * @author ayana  */
public class chatWebSocketManager {

    private static chatWebSocketManager instance;
    private MyWebSocketClient webSocketClient;
    private chatWebSocketListener webSocketListener;
    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private chatWebSocketManager() {}

    /**
     * Provides the singleton instance of the WebSocket manager.
     * @return The single instance of chatWebSocketManager.
     */
    public static synchronized chatWebSocketManager getInstance() {
        if (instance == null) {
            instance = new chatWebSocketManager();
        }
        return instance;
    }
    /**
     * Sets a WebSocket listener to receive connection events and messages.
     * @param listener The listener that will handle WebSocket events.
     */
    public void setWebSocketListener(chatWebSocketListener listener) {
        this.webSocketListener = listener;
    }
    /**
     * Removes the WebSocket listener, stopping the notification of events.
     */
    public void removeWebSocketListener() {
        this.webSocketListener = null;
    }
    /**
     * Connects to a WebSocket server at the specified URL.
     * @param serverUrl The URL of the WebSocket server to connect to.
     */
    public void connectWebSocket(String serverUrl) {
        try {
            URI serverUri = URI.create(serverUrl);
            webSocketClient = new MyWebSocketClient(serverUri);
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends a message through the WebSocket connection if it is open.
     * @param message The message to send to the WebSocket server.
     */
    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }
    /**
     * Disconnects the WebSocket connection if it is open.
     */
    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
    /**
     * Checks if the WebSocket is currently connected.
     * @return true if the WebSocket is connected, false otherwise.
     */
    public boolean isWebSocketConnected() {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    /**
     * Private inner class representing the WebSocket client.
     * Handles WebSocket events and forwards them to the registered listener.
     */
    private class MyWebSocketClient extends WebSocketClient {
        /**
         * Constructor for MyWebSocketClient.
         * @param serverUri The URI of the WebSocket server.
         */
        private MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        /**
         * Called when the WebSocket connection is opened.
         * @param handshakedata The handshake data from the server.
         */
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("WebSocket", "Connected");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketOpen(handshakedata);
            }
        }
        /**
         * Called when a message is received from the WebSocket server.
         * @param message The message received from the server.
         */
        @Override
        public void onMessage(String message) {
            Log.d("WebSocket", "Received message: " + message);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketMessage(message);
            }
        }
        /**
         * Called when the WebSocket connection is closed.
         * @param code Closure code sent by the server.
         * @param reason Reason for the connection closure.
         * @param remote True if the closure was initiated by the server.
         */
        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("WebSocket", "Closed");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketClose(code, reason, remote);
            }
        }
        /**
         * Called when an error occurs with the WebSocket connection.
         * @param ex The exception that was thrown during the error.
         */
        @Override
        public void onError(Exception ex) {
            Log.d("WebSocket", "Error");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketError(ex);
            }
        }
    }
}