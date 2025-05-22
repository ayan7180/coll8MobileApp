package com.example.home_page;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class adds functionality to the layout, manages file downloads,
 * handle WebSocket communications in reference to annotations,
 * and process user interactions with annotations.
 *
 * @author Aisha Mohamed
 */
public class annotateDetailsActivity extends AppCompatActivity implements chatWebSocketListener {

    private static final int REQUEST_READ_STORAGE = 113;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private ImageButton home;
    private TextView fileTitle;
    private Button downloadBtn, saveLineAnnotationButton, addCommentButton;
    private EditText lineAnnotationEditor;
    private LinearLayout annotationContain, linesContain;
    private VideoView videoPlay;
    private String fileType;
    private int fileId;
    private List<String> linesOfText;
    private chatWebSocketManager webSocketManager;
    private Button delete;
    private String user;
    private String fileExt;
    private int dot;
    private String URL = "http://coms-3090-069.class.las.iastate.edu:8080/fileView/";
    private static final String URL_DELETE = "http://coms-3090-069.class.las.iastate.edu:8080/file/";

    private int selectionStart = -1;
    private int selectionEnd = -1;

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views, and configures WebSocket and listeners.
     *
     * @param savedInstanceState Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotate_details);
        checkStoragePermissions();

        Intent intent = getIntent();
        fileType = intent.getStringExtra("fileName");
        fileId = intent.getIntExtra("id", 0);
        user = UserSingleton.getInstance().getUsername();
        Log.d("User", "Username stored in Singleton: " + user);

//        user = "Simon";

        URL = URL + user + "/" + fileId;

        fileTitle = findViewById(R.id.fileTitleTxt);
        downloadBtn = findViewById(R.id.downloadButton);
        saveLineAnnotationButton = findViewById(R.id.saveLineAnnotationButton);
        addCommentButton = findViewById(R.id.addCommentButton);
        lineAnnotationEditor = findViewById(R.id.lineAnnotationEditor);
        annotationContain = findViewById(R.id.annotationContainer);
        linesContain = findViewById(R.id.linesContainer);
        videoPlay = findViewById(R.id.videoPlayer);
        delete = findViewById(R.id.deleteFile);
        home = findViewById(R.id.homeButton);

        chatWebSocketManager.getInstance().connectWebSocket(URL);
        chatWebSocketManager.getInstance().setWebSocketListener(this);


        if (fileType != null) {
            fileTitle.setText(fileType);
            dot = fileType.lastIndexOf('.');
            if (dot != -1) {
                fileExt = fileType.substring(dot + 1).toLowerCase();

                if ("txt".equals(fileExt)) {
                    annotationContain.setVisibility(View.VISIBLE);
                    lineAnnotationEditor.setVisibility(View.VISIBLE);
                    saveLineAnnotationButton.setVisibility(View.VISIBLE);
                } else if ("mp4".equals(fileExt)) {
                    videoPlay.setVisibility(View.VISIBLE);
                    addCommentButton.setVisibility(View.VISIBLE);
                } else {
                    downloadBtn.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("Error", "No extension found in fileType: " + fileType);
            }
        } else {
            Log.e("Error", "fileType is null, cannot proceed with setting visibility.");
        }

        downloadBtn.setOnClickListener(v -> downloadFile(URL, fileType));

        home.setOnClickListener(v -> {
            Intent homeIntent = new Intent(annotateDetailsActivity.this, home_classes.class);
            startActivity(homeIntent);
        });

        delete.setOnClickListener(v -> deleteFile());

        // Configure lineAnnotationEditor for non-editable text with selection tracking
        configureLineAnnotationEditor();
    }

    /**
     * Configures the EditText supplied with text from txt
     * for line annotations and handles selection tracking
     * and disables text editing, so doc cannot be changed.
     */
    private void configureLineAnnotationEditor() {
        lineAnnotationEditor.setInputType(InputType.TYPE_NULL); // Disable editing
        lineAnnotationEditor.setFocusable(false); // Disable keyboard pop-up

        lineAnnotationEditor.setOnTouchListener((v, event) -> {
            lineAnnotationEditor.setFocusableInTouchMode(true); // Re-enable focus on touch
            return false; // Allow standard behavior (text selection)
        });

        lineAnnotationEditor.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Save selection start and end when focus is lost
                selectionStart = lineAnnotationEditor.getSelectionStart();
                selectionEnd = lineAnnotationEditor.getSelectionEnd();
                Log.d("Annotation", "Selection Start: " + selectionStart + ", End: " + selectionEnd);
            }
        });

        saveLineAnnotationButton.setOnClickListener(v -> {
            // Example of using the selected range for annotation
            if (selectionStart >= 0 && selectionEnd >= 0) {
                String selectedText = lineAnnotationEditor.getText()
                        .subSequence(selectionStart, selectionEnd).toString();
                Log.d("Annotation", "Selected Text: " + selectedText);
                Toast.makeText(this, "Annotation saved for selected text.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No text selected for annotation.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if the app has the necessary storage permissions to continue.
     * If not, requests from the user, via the emulator.
     */
    private void checkStoragePermissions() {
        // Check if both READ and WRITE storage permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            Log.d("Permission", "Read and write external storage permissions granted.");
        }
    }



    /**
     * Initiates file download using DownloadManager.
     *
     * @param fileUrl  The URL of the file to download.
     * @param fileName The name of the file to save on the device.
     */
    private void downloadFile(String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Downloading " + fileName);
        request.setDescription("Downloading file...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(this, "Successful download, check Downloads", Toast.LENGTH_SHORT).show();

        Log.d("Download", "Download initiated for file: " + fileName);
    }

    /**
     * Sends a request to delete a file from the server.
     */
    private void deleteFile() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", fileId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL_DELETE,
                response -> Log.d("Upload Success", new String(response.data, StandardCharsets.UTF_8)),
                error -> Log.e("Upload Error", "File upload failed", error)) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] jsonData = postData.toString().getBytes(StandardCharsets.UTF_8);
                params.put("json", new DataPart("data.json", jsonData, "application/json"));
                Log.d("JSON Byte Data", "JSON as Byte Data: " + Arrays.toString(jsonData));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(multipartRequest);
    }

    /**
     * Called when the WebSocket connection is opened.
     *
     * @param handshakedata The handshake data from server.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocketURL", "WebSocket URL: " + URL);
        Log.d("WebSocket", "Connection opened");
        runOnUiThread(() -> Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT).show());
    }

    /**
     * Called when message is received from the WebSocket server.
     *
     * @param message The message is received.
     */
    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Message received: " + message);
        runOnUiThread(() -> {
            if (message.equals("Couldn't find file")) {
                Toast.makeText(this, "Error: File not found on server", Toast.LENGTH_LONG).show();
            } else if (message.equals("Couldn't find username")) {
                Toast.makeText(this, "Error: Username not recognized", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Message from server: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code   The closed code.
     * @param reason The reason for close.
     * @param remote True if the closed via server.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Connection closed: " + reason);
        runOnUiThread(() -> Toast.makeText(this, "Connection closed: " + reason, Toast.LENGTH_SHORT).show());
    }

    /**
     * Called when an error occurs in the WebSocket connection.
     *
     * @param ex The exception informing of the error.
     */
    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error: " + ex.getMessage());
        runOnUiThread(() -> Toast.makeText(this, "WebSocket error: " + ex.getMessage(), Toast.LENGTH_LONG).show());
    }
    
    /**
     * Handles the result of a permission request.
     *
     * @param requestCode  The request code passed to see the permission status.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            // Check if both read and write permissions are granted
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Read and write external storage permissions granted.");
            } else {
                Log.e("Permission", "Permissions denied.");
                Toast.makeText(this, "Permissions denied. Please allow storage access in settings to download files.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}

