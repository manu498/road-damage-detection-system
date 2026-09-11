package com.example.garudax;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintDetailsActivity extends AppCompatActivity {

    ImageView imgComplaint;
    TextView tvComplaintId;
    TextView tvLocation;
    TextView tvDamageType;
    TextView tvDescription;
    TextView tvDate;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complaint_details);

        imgComplaint = findViewById(R.id.imgComplaint);
        tvComplaintId = findViewById(R.id.tvComplaintId);
        tvLocation = findViewById(R.id.tvLocation);
        tvDamageType = findViewById(R.id.tvDamageType);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);

        // Loading messages
        tvComplaintId.setText("Complaint ID: Loading...");
        tvLocation.setText("Location: Loading...");
        tvDamageType.setText("Damage Type: Loading...");
        tvDescription.setText("Description: Loading...");
        tvDate.setText("Date: Loading...");
        tvStatus.setText("Status: Loading...");

        // Get saved complaint ID
        SharedPreferences preferences =
                getSharedPreferences("PotholeX", MODE_PRIVATE);

        int complaintId =
                preferences.getInt("latest_complaint_id", -1);

        // Check complaint ID
        if (complaintId == -1) {
            showError("No complaint ID found");
            return;
        }

        // Display requested ID
        tvComplaintId.setText(
                "Complaint ID: " + complaintId
        );

        // Retrofit API
        ApiService apiService =
                RetrofitClient.getApiService();

        Call<Map<String, Object>> call =
                apiService.getComplaintDetails(complaintId);

        call.enqueue(new Callback<Map<String, Object>>() {

            @Override
            public void onResponse(
                    Call<Map<String, Object>> call,
                    Response<Map<String, Object>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    Map<String, Object> complaint =
                            response.body();

                    // Complaint ID
                    Object id = complaint.get("complaint_id");

                    tvComplaintId.setText(
                            "Complaint ID: " +
                                    (id != null
                                            ? String.valueOf(id)
                                            : "N/A")
                    );

                    // Location
                    Object location =
                            complaint.get("location");

                    tvLocation.setText(
                            "Location: " +
                                    (location != null
                                            ? String.valueOf(location)
                                            : "N/A")
                    );

                    // Damage Type
                    Object damageType =
                            complaint.get("damage_type");

                    tvDamageType.setText(
                            "Damage Type: " +
                                    (damageType != null
                                            ? String.valueOf(damageType)
                                            : "Not detected")
                    );

                    // Description
                    Object description =
                            complaint.get("description");

                    tvDescription.setText(
                            "Description: " +
                                    (description != null
                                            ? String.valueOf(description)
                                            : "N/A")
                    );

                    // Date
                    Object date =
                            complaint.get("complaint_date");

                    tvDate.setText(
                            "Date: " +
                                    (date != null
                                            ? String.valueOf(date)
                                            : "N/A")
                    );

                    // Status
                    Object status =
                            complaint.get("status");

                    tvStatus.setText(
                            "Status: " +
                                    (status != null
                                            ? String.valueOf(status)
                                            : "Pending")
                    );

                } else {

                    showError(
                            "Complaint not found\n" +
                                    "Server code: " +
                                    response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Map<String, Object>> call,
                    Throwable t) {

                showError(
                        "Unable to connect to server"
                );

                Toast.makeText(
                        ComplaintDetailsActivity.this,
                        "Server Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showError(String message) {

        tvComplaintId.setText(
                "Complaint ID: Not available"
        );

        tvLocation.setText(
                "Location: Not available"
        );

        tvDamageType.setText(
                "Damage Type: Not available"
        );

        tvDescription.setText(
                "Description: " + message
        );

        tvDate.setText(
                "Date: Not available"
        );

        tvStatus.setText(
                "Status: Unable to load"
        );
    }
}