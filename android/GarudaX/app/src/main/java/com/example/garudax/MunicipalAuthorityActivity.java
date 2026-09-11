package com.example.garudax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MunicipalAuthorityActivity extends AppCompatActivity {

    TextView tvComplaintId;
    TextView tvLocation;
    TextView tvDamageType;
    TextView tvDescription;
    TextView tvStatus;

    Button btnViewComplaint;
    Button btnUpdateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_municipal_authority);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        tvComplaintId = findViewById(R.id.tvComplaintId);
        tvLocation = findViewById(R.id.tvLocation);
        tvDamageType = findViewById(R.id.tvDamageType);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);

        btnViewComplaint = findViewById(R.id.btnViewComplaint);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        tvComplaintId.setText("Complaint ID: RD001");
        tvLocation.setText("Location: Main Road");
        tvDamageType.setText("Damage Type: Pothole");
        tvDescription.setText(
                "Description: Large pothole on the road."
        );
        tvStatus.setText("Current Status: Under Review");

        btnViewComplaint.setOnClickListener(v -> {

            Toast.makeText(
                    MunicipalAuthorityActivity.this,
                    "Complaint details viewed",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnUpdateStatus.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MunicipalAuthorityActivity.this,
                    RepairStatusActivity.class
            );

            startActivity(intent);
        });
    }
}