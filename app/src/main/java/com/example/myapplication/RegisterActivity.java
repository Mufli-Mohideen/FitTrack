package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText userNameR, userEmailR, userPasswordR, userReEnterPasswordR;
    private Button buttonRegister;
    private SQLiteHelper dbHelper;
    private TextView textViewLoginLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameR = findViewById(R.id.userNameR);
        userEmailR = findViewById(R.id.userEmailR);
        userPasswordR = findViewById(R.id.userPasswordR);
        userReEnterPasswordR = findViewById(R.id.userReEnterPasswordR);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        dbHelper = new SQLiteHelper(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = userNameR.getText().toString().trim();
                String email = userEmailR.getText().toString().trim();
                String password = userPasswordR.getText().toString().trim();
                String reEnteredPassword = userReEnterPasswordR.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reEnteredPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isPasswordStrong(password)) {
                    Toast.makeText(RegisterActivity.this, "Password not strong enough. It should be at least 6 characters long and contain an uppercase letter, a lowercase letter, and a number.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.isEmailRegistered(email)) {
                    Toast.makeText(RegisterActivity.this, "Email is already registered", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(reEnteredPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }

                dbHelper.insertUser(username, email, password);

                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to LoginActivity when the TextView is clicked
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    private boolean isPasswordStrong(String password) {
        String passwordPattern = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}";
        return password.matches(passwordPattern);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
