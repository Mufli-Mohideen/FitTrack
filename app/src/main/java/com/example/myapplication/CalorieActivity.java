package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class CalorieActivity extends AppCompatActivity {

    private EditText targetCalorieEditText,addCaloriesEditText;
    private ImageView addCaloriesButton,btnBackC;
    private Button setTargetButton,buttonDeleteTargetC;
    private SQLiteHelper dbHelper;
    private int userId;

    private CircularProgressBar circularProgressBarCalorie;
    private TextView txtCalorieCountC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);

        targetCalorieEditText = findViewById(R.id.txtTargetCalorieC);
        setTargetButton = findViewById(R.id.btnSetTargetC);
        addCaloriesEditText = findViewById(R.id.txtAddCaloriesC);
        addCaloriesButton = findViewById(R.id.btnAddCalorieC);
        circularProgressBarCalorie = findViewById(R.id.circularProgressBarCalorie);
        txtCalorieCountC = findViewById(R.id.txtCalorieCountC);
        buttonDeleteTargetC = findViewById(R.id.buttonDeleteTargetC);
        btnBackC = findViewById(R.id.btnBackC);

        dbHelper = new SQLiteHelper(this);

        int currentCalories = dbHelper.getCurrentCalories(userId);
        int targetCalories = dbHelper.getTargetCalories(userId);

        txtCalorieCountC.setText(currentCalories + " KCal");

        if (targetCalories > 0) {
            int progress = (int) ((float) currentCalories / targetCalories * 100);
            circularProgressBarCalorie.setProgress(progress);
        } else {
            circularProgressBarCalorie.setProgress(0);
        }

        setTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetCalorieStr = targetCalorieEditText.getText().toString().trim();

                if (targetCalorieStr.isEmpty()) {
                    Toast.makeText(CalorieActivity.this, "Please enter a target calorie value", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int targetCalorie = Integer.parseInt(targetCalorieStr);

                        if (targetCalorie <= 0) {
                            Toast.makeText(CalorieActivity.this, "Target calorie must be a positive number", Toast.LENGTH_SHORT).show();
                        } else {
                            int existingTarget = dbHelper.getTargetCalories(userId);

                            if (existingTarget > 0) {
                                new AlertDialog.Builder(CalorieActivity.this)
                                        .setTitle("Update Target Calorie")
                                        .setMessage("You already have a target calorie set. Do you want to update it?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            boolean isTargetSet = dbHelper.setTargetCalorie(userId, targetCalorie);

                                            if (isTargetSet) {
                                                Toast.makeText(CalorieActivity.this, "Target Calorie Updated Successfully", Toast.LENGTH_SHORT).show();

                                                // Fetch updated values
                                                int currentCalories = dbHelper.getCurrentCalories(userId);
                                                int targetCalories = dbHelper.getTargetCalories(userId);

                                                // Update the progress bar
                                                if (targetCalories > 0) {
                                                    int progress = (int) ((float) currentCalories / targetCalories * 100);
                                                    circularProgressBarCalorie.setProgress(progress);
                                                } else {
                                                    circularProgressBarCalorie.setProgress(0);
                                                }
                                            } else {
                                                Toast.makeText(CalorieActivity.this, "Failed to Update Target Calorie", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            } else {
                                boolean isTargetSet = dbHelper.setTargetCalorie(userId, targetCalorie);

                                if (isTargetSet) {
                                    Toast.makeText(CalorieActivity.this, "Target Calorie Set Successfully", Toast.LENGTH_SHORT).show();

                                    // Fetch updated values
                                    int currentCalories = dbHelper.getCurrentCalories(userId);
                                    int targetCalories = dbHelper.getTargetCalories(userId);

                                    // Update the progress bar
                                    if (targetCalories > 0) {
                                        int progress = (int) ((float) currentCalories / targetCalories * 100);
                                        circularProgressBarCalorie.setProgress(progress);
                                    } else {
                                        circularProgressBarCalorie.setProgress(0);
                                    }
                                } else {
                                    Toast.makeText(CalorieActivity.this, "Failed to Set Target Calorie", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(CalorieActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        addCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addCaloriesStr = addCaloriesEditText.getText().toString().trim();

                if (addCaloriesStr.isEmpty()) {
                    Toast.makeText(CalorieActivity.this, "Please enter a calorie value", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int addCalories = Integer.parseInt(addCaloriesStr);

                        if (addCalories <= 0) {
                            Toast.makeText(CalorieActivity.this, "Calories must be a positive number", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isCalorieAdded = dbHelper.addCalories(userId, addCalories);

                            if (isCalorieAdded) {
                                Toast.makeText(CalorieActivity.this, "Calories Added Successfully", Toast.LENGTH_SHORT).show();
                                addCaloriesEditText.setText("");

                                int currentCalories = dbHelper.getCurrentCalories(userId);
                                int targetCalories = dbHelper.getTargetCalories(userId);

                                txtCalorieCountC.setText(currentCalories + " KCal");

                                if (targetCalories > 0) {
                                    int progress = (int) ((float) currentCalories / targetCalories * 100);
                                    circularProgressBarCalorie.setProgress(progress);
                                } else {
                                    circularProgressBarCalorie.setProgress(0);
                                }

                            } else {
                                Toast.makeText(CalorieActivity.this, "Failed to Add Calories", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(CalorieActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonDeleteTargetC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CalorieActivity.this)
                        .setTitle("Delete Target and Records")
                        .setMessage("This will remove your target and all calorie records for today. Do you want to continue?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean isDeleted = dbHelper.deleteTargetAndRecords(userId);

                            if (isDeleted) {
                                Toast.makeText(CalorieActivity.this, "Target and Today's Records Deleted Successfully", Toast.LENGTH_SHORT).show();

                                 txtCalorieCountC.setText("0 KCal");
                                circularProgressBarCalorie.setProgress(0);
                                targetCalorieEditText.setText("");
                            } else {
                                Toast.makeText(CalorieActivity.this, "Failed to Delete Target and Records", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btnBackC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalorieActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });
    }
}
