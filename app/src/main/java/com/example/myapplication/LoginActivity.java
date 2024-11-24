package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private TextView registerToggle;
    private Button buttonLogin;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmailR);
        userPassword = findViewById(R.id.userPasswordR);
        buttonLogin = findViewById(R.id.buttonLogin);
        registerToggle = findViewById(R.id.registerToggle);

        dbHelper = new SQLiteHelper(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Integer userId = dbHelper.authenticateUser(email, password);

                new Thread(() -> {
                    try {
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                        String subject = "Login Detected on Your FitTrack Account";
                        String messageBody = "Hi,\n\nWe detected a login from your account at " + currentTime + ".\n" +
                                "If this wasn't you, please contact the FitTrack team immediately.\n\nBest regards,\nFitTrack Team";

                        EmailSender.sendEmail(email, subject, messageBody);
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login notification sent!", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful, but email sending failed.", Toast.LENGTH_LONG).show();
                        });
                    }
                }).start();

                if (userId != null) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
