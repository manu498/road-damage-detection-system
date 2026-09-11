package com.example.garudax;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrackComplaintActivity extends AppCompatActivity {

    TextView tvComplaintId;
    TextView tvStatus;
    TextView tvSubmitted;
    TextView tvReview;
    TextView tvAssigned;
    TextView tvRepair;
    TextView tvCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_complaint);

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

        tvComplaintId = findViewById(R.id.tvComplaintId);
        tvStatus = findViewById(R.id.tvStatus);
        tvSubmitted = findViewById(R.id.tvSubmitted);
        tvReview = findViewById(R.id.tvReview);
        tvAssigned = findViewById(R.id.tvAssigned);
        tvRepair = findViewById(R.id.tvRepair);
        tvCompleted = findViewById(R.id.tvCompleted);

        tvComplaintId.setText("Complaint ID: RD001");
        tvStatus.setText("Current Status: Under Review");

        tvSubmitted.setText("✓ Complaint Submitted");
        tvReview.setText("✓ Under Review");
        tvAssigned.setText("✓ Assigned to Municipal Authority");
        tvRepair.setText("○ Repair In Progress");
        tvCompleted.setText("○ Repair Completed");
    }
}