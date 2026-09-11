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

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter Email");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter Password");
                return;
            }

            Map<String, String> loginData = new HashMap<>();
            loginData.put("email", email);
            loginData.put("password", password);

            btnLogin.setEnabled(false);

            ApiService apiService =
                    RetrofitClient.getApiService();

            Call<Map<String, Object>> call =
                    apiService.login(loginData);

            call.enqueue(new Callback<Map<String, Object>>() {

                @Override
                public void onResponse(
                        Call<Map<String, Object>> call,
                        Response<Map<String, Object>> response
                ) {

                    btnLogin.setEnabled(true);

                    if (response.isSuccessful()
                            && response.body() != null) {

                        Toast.makeText(
                                MainActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                MainActivity.this,
                                CitizenHomeActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Invalid Email or Password",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<Map<String, Object>> call,
                        Throwable t
                ) {

                    btnLogin.setEnabled(true);

                    Toast.makeText(
                            MainActivity.this,
                            "Server Error: " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        // REGISTER BUTTON
        btnRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}