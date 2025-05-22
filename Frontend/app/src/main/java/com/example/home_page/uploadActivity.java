package com.example.home_page;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class gives users access to uploading files given the needed information:
 * the Course Abbreviation and number, followed by file.type
 @author Aisha Mohamed
 */
public class uploadActivity extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;
    private static final String URL = "http://coms-3090-069.class.las.iastate.edu:8080/file/upload";
    private Uri fileUri;
    private int uploadCount = 0;
    private int num;

    private Button insertFile;
    private ImageButton homeButton;
    private TextView fileName;
    private Button submit;
    private EditText courseAbbrev;
    private EditText courseNum;
    private EditText fileTitle;

    /**
     * Called when the intent is set to this activity.
     * Sets up the initial state of the activity,
     * initializes views and listeners.
     * Provides functionality for navigation back to the home screen.
     *
     * @param savedInstanceState Stores small amount of data needed to reload UI state
     *                           if the system stops and then recreates the UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_upload);

        insertFile = findViewById(R.id.uploadButton);
        homeButton = findViewById(R.id.homeButton);
        fileName = findViewById(R.id.fileNameTextView);
        submit = findViewById(R.id.submitButton);
        courseAbbrev = findViewById(R.id.courseAbbreviation);
        courseNum = findViewById(R.id.courseNumber);
        fileTitle = findViewById(R.id.fileTitleTxt);



        //check it out
        Intent intent2 = getIntent();
        String username = intent2.getStringExtra("USERNAME_KEY");

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(uploadActivity.this, home_classes.class);
            startActivity(intent);
        });

        insertFile.setOnClickListener(v -> openFilePicker());

        submit.setOnClickListener(v -> {
            String abbreviation = courseAbbrev.getText().toString().trim();
            String number = courseNum.getText().toString().trim();
            String title = fileTitle.getText().toString().trim();

            String regex = "^[\\w\\-. ]+\\.(mp4|txt|docx|pdf|png|jpg|xls)$";
            if (!title.matches(regex)) {
                fileTitle.setText("");
                Toast.makeText(uploadActivity.this, "Invalid file name or unsupported file type.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (abbreviation.isEmpty() || number.isEmpty() || fileUri == null) {
                Toast.makeText(uploadActivity.this, "Please select a class and file to upload!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject postData = new JSONObject();
                    JSONObject courseObject = new JSONObject();
                    num = Integer.parseInt(courseNum.getText().toString().trim());
                    courseObject.put("courseNum", num);

                    courseObject.put("abbreviation", abbreviation);
                    String user = "Simon";

                    JSONObject userObject = new JSONObject();
                    userObject.put("username", "Simon"); // Stub username

                    postData.put("course", courseObject);
                    postData.put("user", userObject);
                    postData.put("fileName", title);

                    uploadCount++;

                    Log.d("", postData.toString());

//                    postData.put("user", userObject);
                    uploadFile(postData);

                    Intent intent = new Intent(uploadActivity.this, statsActivity.class);
                    intent.putExtra("files uploaded", uploadCount);
                    intent.putExtra("username",user);
                    intent.putExtra("Title", title);

                    startActivity(intent);


                } catch (JSONException e) {
                    Log.e("JSON Error", "Error creating JSON object", e);
                }
            }
        });
    }

    /**
     * File picker so the user can select a file from their device.
     * Launches intent to browse files, allowing the user to choose a file.
     */
    private void openFilePicker() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"), PICKFILE_RESULT_CODE);
    }

    /**
     * Handles results of the file picker method.
     * Takes selected file's name and displays it.
     *
     * @param requestCode Request code from the file picker
     * @param resultCode Result code returned from file picker.
     * @param data Intent containing data returned from file picker.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String filePath = getPath(fileUri);
            if (filePath != null) {
                fileName.setText(new File(filePath).getName());
                copyFile(filePath);
            }
        }
    }

    /**
     * Retrieves the absolute file path from a URI.
     * @param uri The URI content of the file.
     * @return The absolute file path as a string or null if not found.
     */
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("getPath Error", "Error retrieving file path", e);
        }
        return uri.getPath();
    }

    /**
     * Copies file from the source path to a folder in external storage.
     *
     * @param sourcePath The path of the file to copy.
     */
    private void copyFile(String sourcePath) {
        File source = new File(sourcePath);
        String filename = source.getName();
        File destination = new File(Environment.getExternalStorageDirectory() + "/CustomFolder/" + filename);

        try (FileChannel inChannel = new FileInputStream(source).getChannel();
             FileChannel outChannel = new FileOutputStream(destination).getChannel()) {

            inChannel.transferTo(0, inChannel.size(), outChannel);
            Log.d("File Copy", "File copied successfully to " + destination.getAbsolutePath());
        } catch (IOException e) {
            Log.e("File Copy Error", "Error copying file", e);
        }
    }


    /**
     * Sends a POST request to enable uploading file to backend
     * utilizes a Multipart request to upload a file using data from file and JSON data.
     * @param postData A JSON object containing metadata: course details and the file name.
     */
    private void uploadFile(JSONObject postData) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("Upload Success", new String(response.data, StandardCharsets.UTF_8));
                },
                error -> {
                    Log.e("Upload Error", "File upload failed", error);
                }) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>(); // emptyyyy
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                String fileMimeType = getContentResolver().getType(fileUri);
                String fileName = postData.optString("fileName", "default_name");
                byte[] fileData = getFileData(fileUri);
                params.put("file", new DataPart(fileName, fileData, fileMimeType != null ? fileMimeType : "application/octet-stream"));
                Log.d("File Byte Data", "FileName: " + fileName + ", MimeType: " + fileMimeType);


                byte[] jsonData = postData.toString().getBytes(StandardCharsets.UTF_8);
                params.put("json", new DataPart("data.json", jsonData, "application/json"));
                Log.d("JSON Byte Data", "JSON as Byte Data: " + Arrays.toString(jsonData));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(multipartRequest);
    }


    /**
     * Reads the data from the file from URI and converts it to a byte array.
     *
     * @param uri The URI of the file to read.
     * @return A byte array containing the file data, or empty if error happens.
     */
    private byte[] getFileData(Uri uri) {
        try (FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(uri)) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            Log.e("File Data Error", "Failed to read file data", e);
        }
        Log.d("File URI", uri.toString());

        return new byte[0];
    }
}