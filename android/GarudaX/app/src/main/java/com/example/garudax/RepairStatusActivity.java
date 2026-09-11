package com.example.garudax;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RepairStatusActivity extends AppCompatActivity {

    TextView tvComplaintId;
    TextView tvLocation;
    TextView tvCurrentStatus;

    Spinner spinnerStatus;
    Button btnUpdateStatus;

    String selectedStatus = "Under Review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repair_status);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

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
                }
        );

        tvComplaintId = findViewById(R.id.tvComplaintId);
        tvLocation = findViewById(R.id.tvLocation);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        String[] statusList = {
                "Submitted",
                "Under Review",
                "Assigned",
                "Repair In Progress",
                "Repair Completed"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusList
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedStatus = statusList[position];
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        btnUpdateStatus.setOnClickListener(v -> {

            tvCurrentStatus.setText(
                    "Current Status: " + selectedStatus
            );

            Toast.makeText(
                    RepairStatusActivity.this,
                    "Status Updated Successfully",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}