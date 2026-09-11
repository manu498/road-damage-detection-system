package com.example.garudax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etMobile;
    EditText etPassword;

    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
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

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);


        // ==============================
        // REGISTER
        // ==============================

        btnRegister.setOnClickListener(v -> {

            String firstName =
                    etFirstName.getText().toString().trim();

            String lastName =
                    etLastName.getText().toString().trim();

            String email =
                    etEmail.getText().toString().trim();

            String mobile =
                    etMobile.getText().toString().trim();

            String password =
                    etPassword.getText().toString().trim();


            // Validation

            if (firstName.isEmpty()) {
                etFirstName.setError("Enter First Name");
                return;
            }

            if (lastName.isEmpty()) {
                etLastName.setError("Enter Last Name");
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Enter Email");
                return;
            }

            if (mobile.isEmpty()) {
                etMobile.setError("Enter Mobile Number");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter Password");
                return;
            }


            // ==============================
            // SEND DATA TO FLASK
            // ==============================

            Map<String, String> registerData =
                    new HashMap<>();

            /*
             * Your MySQL citizen table has:
             *
             * name
             * email
             * contact_no
             * password
             */

            String fullName =
                    firstName + " " + lastName;

            registerData.put("name", fullName);
            registerData.put("email", email);
            registerData.put("contact_no", mobile);
            registerData.put("password", password);


            btnRegister.setEnabled(false);


            ApiService apiService =
                    RetrofitClient.getApiService();


            Call<Map<String, Object>> call =
                    apiService.register(registerData);


            call.enqueue(
                    new Callback<Map<String, Object>>() {

                        @Override
                        public void onResponse(
                                Call<Map<String, Object>> call,
                                Response<Map<String, Object>> response) {

                            btnRegister.setEnabled(true);


                            if (response.isSuccessful()
                                    && response.body() != null) {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration Successful",
                                        Toast.LENGTH_SHORT
                                ).show();


                                Intent intent =
                                        new Intent(
                                                RegisterActivity.this,
                                                CitizenHomeActivity.class
                                        );

                                startActivity(intent);

                                finish();

                            } else {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration Failed",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }


                        @Override
                        public void onFailure(
                                Call<Map<String, Object>> call,
                                Throwable t) {

                            btnRegister.setEnabled(true);

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Server Error: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        });


        // ==============================
        // LOGIN BUTTON
        // ==============================

        btnLogin.setOnClickListener(v -> {

            finish();

        });
    }
}