package com.example.garudax;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitComplaintActivity extends AppCompatActivity {

    ImageView imgComplaint;

    EditText etLocation;
    EditText etDamageType;
    EditText etDescription;

    Button btnSelectImage;
    Button btnSubmit;

    Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_submit_complaint);

        imgComplaint = findViewById(R.id.imgComplaint);

        etLocation = findViewById(R.id.etLocation);
        etDamageType = findViewById(R.id.etDamageType);
        etDescription = findViewById(R.id.etDescription);

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmit = findViewById(R.id.btnSubmit);


        // ==========================================
        // SELECT IMAGE
        // ==========================================

        btnSelectImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");

            startActivityForResult(
                    intent,
                    PICK_IMAGE_REQUEST
            );
        });


        // ==========================================
        // SUBMIT COMPLAINT
        // ==========================================

        btnSubmit.setOnClickListener(v -> submitComplaint());
    }


    // ==========================================
    // IMAGE SELECT RESULT
    // ==========================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            selectedImageUri = data.getData();

            if (selectedImageUri != null) {

                imgComplaint.setImageURI(
                        selectedImageUri
                );

                Toast.makeText(
                        this,
                        "Image selected",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }


    // ==========================================
    // SUBMIT COMPLAINT FUNCTION
    // ==========================================

    private void submitComplaint() {

        String location =
                etLocation.getText()
                        .toString()
                        .trim();

        String damageType =
                etDamageType.getText()
                        .toString()
                        .trim();

        String description =
                etDescription.getText()
                        .toString()
                        .trim();


        // ==========================================
        // VALIDATION
        // ==========================================

        if (selectedImageUri == null) {

            Toast.makeText(
                    this,
                    "Please select an image",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (location.isEmpty()) {

            etLocation.setError(
                    "Enter road location"
            );

            return;
        }


        if (description.isEmpty()) {

            etDescription.setError(
                    "Enter description"
            );

            return;
        }


        try {

            // ==========================================
            // CONVERT URI TO FILE
            // ==========================================

            File imageFile =
                    createFileFromUri(
                            selectedImageUri
                    );


            // ==========================================
            // IMAGE REQUEST BODY
            // ==========================================

            RequestBody imageRequestBody =
                    RequestBody.create(
                            MediaType.parse("image/*"),
                            imageFile
                    );


            // ==========================================
            // MULTIPART IMAGE
            // ==========================================

            MultipartBody.Part imagePart =
                    MultipartBody.Part.createFormData(
                            "image",
                            imageFile.getName(),
                            imageRequestBody
                    );


            // ==========================================
            // CITIZEN ID
            // ==========================================

            RequestBody citizenId =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            "1"
                    );


            // ==========================================
            // LOCATION ID
            // ==========================================

            // Currently using valid Location ID = 3
            RequestBody locationId =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            "3"
                    );


            // ==========================================
            // DESCRIPTION
            // ==========================================

            RequestBody descriptionBody =
                    RequestBody.create(
                            MediaType.parse("text/plain"),
                            description
                    );


            // ==========================================
            // DISABLE BUTTON
            // ==========================================

            btnSubmit.setEnabled(false);


            // ==========================================
            // RETROFIT API
            // ==========================================

            ApiService apiService =
                    RetrofitClient.getApiService();


            Call<Map<String, Object>> call =
                    apiService.submitComplaint(
                            citizenId,
                            locationId,
                            descriptionBody,
                            imagePart
                    );


            // ==========================================
            // SERVER RESPONSE
            // ==========================================

            call.enqueue(
                    new Callback<Map<String, Object>>() {

                        @Override
                        public void onResponse(
                                Call<Map<String, Object>> call,
                                Response<Map<String, Object>> response) {

                            btnSubmit.setEnabled(true);


                            // ==========================================
                            // SUCCESS
                            // ==========================================

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                Map<String, Object> result =
                                        response.body();


                                // ==========================================
                                // GET NEW COMPLAINT ID
                                // ==========================================

                                Object complaintIdObject =
                                        result.get("complaint_id");


                                if (complaintIdObject != null) {

                                    int complaintId =
                                            ((Number) complaintIdObject)
                                                    .intValue();


                                    // ==========================================
                                    // SAVE COMPLAINT ID
                                    // ==========================================

                                    SharedPreferences preferences =
                                            getSharedPreferences(
                                                    "PotholeX",
                                                    MODE_PRIVATE
                                            );


                                    preferences.edit()
                                            .putInt(
                                                    "latest_complaint_id",
                                                    complaintId
                                            )
                                            .apply();


                                    // ==========================================
                                    // SUCCESS MESSAGE
                                    // ==========================================

                                    Toast.makeText(
                                            SubmitComplaintActivity.this,
                                            "Complaint submitted successfully! ID: "
                                                    + complaintId,
                                            Toast.LENGTH_LONG
                                    ).show();


                                    // ==========================================
                                    // OPEN COMPLAINT DETAILS
                                    // ==========================================

                                    Intent intent =
                                            new Intent(
                                                    SubmitComplaintActivity.this,
                                                    ComplaintDetailsActivity.class
                                            );


                                    startActivity(intent);


                                    // Close submit screen
                                    finish();

                                } else {

                                    Toast.makeText(
                                            SubmitComplaintActivity.this,
                                            "Complaint submitted but ID was not received!",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }


                            } else {

                                // ==========================================
                                // SERVER ERROR
                                // ==========================================

                                Toast.makeText(
                                        SubmitComplaintActivity.this,
                                        "Upload failed. Server returned: "
                                                + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }


                        // ==========================================
                        // NETWORK FAILURE
                        // ==========================================

                        @Override
                        public void onFailure(
                                Call<Map<String, Object>> call,
                                Throwable t) {

                            btnSubmit.setEnabled(true);

                            Toast.makeText(
                                    SubmitComplaintActivity.this,
                                    "Server Error: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );


        } catch (Exception e) {

            btnSubmit.setEnabled(true);

            Toast.makeText(
                    this,
                    "Image error: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // ==========================================
    // CREATE FILE FROM URI
    // ==========================================

    private File createFileFromUri(Uri uri)
            throws Exception {

        String fileName =
                getFileName(uri);


        if (fileName == null) {

            fileName =
                    "road_damage_image.jpg";
        }


        File file =
                new File(
                        getCacheDir(),
                        fileName
                );


        InputStream inputStream =
                getContentResolver()
                        .openInputStream(uri);


        FileOutputStream outputStream =
                new FileOutputStream(file);


        byte[] buffer =
                new byte[4096];


        int length;


        while ((length =
                inputStream.read(buffer)) > 0) {

            outputStream.write(
                    buffer,
                    0,
                    length
            );
        }


        outputStream.close();

        inputStream.close();


        return file;
    }


    // ==========================================
    // GET FILE NAME
    // ==========================================

    private String getFileName(Uri uri) {

        String result = null;


        if ("content".equals(
                uri.getScheme())) {

            try (Cursor cursor =
                         getContentResolver()
                                 .query(
                                         uri,
                                         null,
                                         null,
                                         null,
                                         null
                                 )) {

                if (cursor != null
                        && cursor.moveToFirst()) {

                    int index =
                            cursor.getColumnIndex(
                                    OpenableColumns.DISPLAY_NAME
                            );


                    if (index >= 0) {

                        result =
                                cursor.getString(index);
                    }
                }
            }
        }


        if (result == null) {

            result =
                    uri.getPath();


            if (result != null) {

                int cut =
                        result.lastIndexOf('/');


                if (cut != -1) {

                    result =
                            result.substring(
                                    cut + 1
                            );
                }
            }
        }


        return result;
    }
}