package com.example.garudax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CitizenHomeActivity extends AppCompatActivity {

    Button btnUploadImage;
    Button btnSubmitComplaint;
    Button btnComplaintDetails;
    Button btnTrackComplaint;
    Button btnNotifications;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citizen_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);
        btnComplaintDetails = findViewById(R.id.btnComplaintDetails);
        btnTrackComplaint = findViewById(R.id.btnTrackComplaint);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnLogout = findViewById(R.id.btnLogout);

        btnUploadImage.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CitizenHomeActivity.this,
                    SubmitComplaintActivity.class
            );

            startActivity(intent);
        });

        btnSubmitComplaint.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CitizenHomeActivity.this,
                    SubmitComplaintActivity.class
            );

            startActivity(intent);
        });

        btnComplaintDetails.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CitizenHomeActivity.this,
                    ComplaintDetailsActivity.class
            );

            startActivity(intent);
        });

        btnTrackComplaint.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CitizenHomeActivity.this,
                    TrackComplaintActivity.class
            );

            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CitizenHomeActivity.this,
                    NotificationsActivity.class
            );

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}