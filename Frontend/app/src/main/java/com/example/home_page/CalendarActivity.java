package com.example.home_page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView date;
    String eventId = "1";
    ImageButton homeButton;
    HashMap<String, String> eventsMap = new HashMap<>(); // To store events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        calendarView = findViewById(R.id.calendarView);
        date = findViewById(R.id.date);
        homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, home_classes.class);
                startActivity(intent);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String todayDate = (month + 1) + "/" + dayOfMonth + "/" + year; // Adjust month indexing
                String eventDetails = eventsMap.get(todayDate);

                // Display the selected date and any event details
                if (eventDetails != null) {
                    date.setText(todayDate + "\n" + eventDetails);
                } else {
                    date.setText(todayDate + "\nNo events for this date.");
                }

                // Show dialog to add or edit an event
                showEventDialog(todayDate, eventDetails);
            }
        });
    }

    private void showEventDialog(String dateKey, String existingEvent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add/Edit Event for " + dateKey);

        final View customView = getLayoutInflater().inflate(R.layout.addeventcalendar, null);
        builder.setView(customView);

        final EditText classInput = customView.findViewById(R.id.class_input);
        final EditText eventInput = customView.findViewById(R.id.event_input);
        Button updateButton = customView.findViewById(R.id.update_button);
        Button deleteButton = customView.findViewById(R.id.delete_button);

        if (existingEvent != null) {
            String[] parts = existingEvent.split("\n");
            if (parts.length == 2) {
                classInput.setText(parts[0].replace("Class: ", ""));
                eventInput.setText(parts[1].replace("Event: ", ""));
            }
        }

        builder.setPositiveButton("Save", (dialogInterface, i) -> {
            String classText = classInput.getText().toString().trim();
            String eventText = eventInput.getText().toString().trim();

            if (!classText.isEmpty() && !eventText.isEmpty()) {
                String fullEvent = "Class: " + classText + "\nEvent: " + eventText;
                eventsMap.put(dateKey, fullEvent);
                date.setText(dateKey + "\n" + fullEvent);
                sendEventToBackend(eventText, classText);
            } else {
                eventsMap.remove(dateKey);
                date.setText(dateKey + "\nNo events for this date.");
            }
        });

        // Update Button Logic
        updateButton.setOnClickListener(v -> {
            String classText = classInput.getText().toString().trim();
            String eventText = eventInput.getText().toString().trim();

            if (!classText.isEmpty() && !eventText.isEmpty()) {
                updateEventInBackend(classText, eventText);
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Button Logic
        deleteButton.setOnClickListener(v -> {
            deleteEventFromBackend();
            eventsMap.remove(dateKey);
            date.setText(dateKey + "\nNo events for this date.");
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void sendEventToBackend(String eventName, String className) {

        String url = "http://coms-3090-069.class.las.iastate.edu:8080/event";

        // Create a JSON array with the className and eventName
        JSONArray eventArray = new JSONArray();
        eventArray.put(className);
        eventArray.put(eventName);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, eventArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle the response
                        Toast.makeText(CalendarActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Toast.makeText(CalendarActivity.this, "Error saving event: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    private void updateEventInBackend(String className, String eventName) {
        String url = "http://coms-3090-069.class.las.iastate.edu:8080/event/" + eventId;

        // Concatenate the className and eventName or only send eventName (as per your backend requirements)
        String updatedEvent = eventName;


        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error updating event: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            public byte[] getBody() {
                return updatedEvent.getBytes(); // Send the event name as the body
            }

            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void deleteEventFromBackend() {

        String url = "http://coms-3090-069.class.las.iastate.edu:8080/event?id=" + eventId; // Replace with dynamic ID

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.DELETE, url, null,
                response -> Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error deleting event: " + error.getMessage(), Toast.LENGTH_LONG).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



}
