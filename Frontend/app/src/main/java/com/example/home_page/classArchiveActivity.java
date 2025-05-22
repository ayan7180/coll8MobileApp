package com.example.home_page;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class classArchiveActivity extends AppCompatActivity {

    private RecyclerView archiveRecyclerView;
    private ArchiveAdapter archiveAdapter;
    private List<Archive> archiveList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classarchive);

        // Initialize RecyclerView
        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter and attach to RecyclerView
        archiveAdapter = new ArchiveAdapter(archiveList, this::restoreArchive);
        archiveRecyclerView.setAdapter(archiveAdapter);

        // Fetch archives for a specific user
        int userId = 8;
        fetchArchives(userId);

        // Restore button (optional global action)
        Button restoreButton = findViewById(R.id.restoreButton);
        restoreButton.setOnClickListener(v -> {
            Toast.makeText(this, "Select an archive to restore", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchArchives(int userId) {
        String url = "http://coms-3090-069.class.las.iastate.edu:8080/archive/all/" + userId;
        System.out.println(url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::handleArchivesResponse,
                error -> Toast.makeText(
                        classArchiveActivity.this,
                        "Failed to fetch archives: " + error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        requestQueue.add(request);
    }

    private void handleArchivesResponse(JSONArray response) {
        try {
            archiveList.clear(); // Clear any existing data
            for (int i = 0; i < response.length(); i++) {
                JSONObject archive = response.getJSONObject(i);

                // Parse archive data
                int id = archive.getInt("id");
                String abbreviation = archive.getString("abbreviation"); // Replace with actual key for display
                int courseNum = archive.getInt("courseNum");
                String title = abbreviation + " " + courseNum; // Combine data for display

                // Add to archive list
                archiveList.add(new Archive(id, title));
            }

            // Notify adapter of the data change
            archiveAdapter.notifyDataSetChanged();

            Toast.makeText(
                    this,
                    "Fetched " + response.length() + " archives.",
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Failed to parse archives: " + e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void restoreArchive(int archiveId) {
        String url = "http://coms-3090-069.class.las.iastate.edu:8080/archive/restore/" + archiveId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    // Handle success
                    Toast.makeText(
                            this,
                            "Archive restored successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                    if (response != null) {
                        Log.d("RestoreArchive", "Response: " + response.toString());
                    } else {
                        Log.d("RestoreArchive", "Response: null (no content returned by the server)");
                    }
                },
                error -> {
                    // Handle errors and plain string responses
                    if (error.networkResponse != null) {
                        String responseData = new String(error.networkResponse.data);
                        if (responseData != null && !responseData.trim().isEmpty()) {
                            if (isPlainText(responseData)) {
                                Toast.makeText(
                                        this,
                                        responseData,
                                        Toast.LENGTH_LONG
                                ).show();
                                Log.e("RestoreArchive", "Error Response (Plain Text): " + responseData);
                            } else {
                                Toast.makeText(
                                        this,
                                        "Failed to restore archive. Unexpected server response.",
                                        Toast.LENGTH_LONG
                                ).show();
                                Log.e("RestoreArchive", "Error Response (JSON Expected): " + responseData);
                            }
                        } else {
                            Toast.makeText(
                                    this,
                                    "Failed to restore archive. Empty server response.",
                                    Toast.LENGTH_LONG
                            ).show();
                            Log.e("RestoreArchive", "Empty server response.");
                        }
                    } else {
                        Toast.makeText(
                                this,
                                "No response from server.",
                                Toast.LENGTH_LONG
                        ).show();
                        Log.e("RestoreArchive", "Error - No response from server: " + error.toString());
                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String responseData = new String(response.data);
                    if (isPlainText(responseData)) {
                        // Treat plain text as success
                        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        // JSON response, parse as normal
                        return super.parseNetworkResponse(response);
                    }
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        requestQueue.add(request);
    }

    private boolean isPlainText(String response) {
        try {
            new JSONObject(response); // Try parsing as JSON
            return false; // It's JSON, not plain text
        } catch (Exception ignored) {
            return true; // It's plain text
        }
    }

}
