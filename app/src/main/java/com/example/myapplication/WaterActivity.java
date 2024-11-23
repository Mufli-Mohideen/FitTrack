package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class WaterActivity extends AppCompatActivity {
    private int userId;
    private EditText txtTargetWaterW,txtAddWaterLevelW;
    private Button buttonSetTargetW,buttonDeleteTargetW;
    private CircularProgressBar circularProgressBarW;
    private TextView txtWaterCountW;
    private SQLiteHelper dbHelper;
    private ImageView btnAddWaterW,btnBackW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_intake);

        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtTargetWaterW = findViewById(R.id.txtTargetWaterW);
        buttonSetTargetW = findViewById(R.id.buttonSetTargetW);
        circularProgressBarW = findViewById(R.id.circularProgressBarW);
        txtWaterCountW = findViewById(R.id.txtWaterCountW);
        txtAddWaterLevelW = findViewById(R.id.txtAddWaterLevelW);
        btnAddWaterW = findViewById(R.id.btnAddWaterW);
        btnBackW = findViewById(R.id.btnBackW);
        buttonDeleteTargetW = findViewById(R.id.buttonDeleteTargetW);


        dbHelper = new SQLiteHelper(this);


        updateWaterLevel();

        buttonSetTargetW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetWaterStr = txtTargetWaterW.getText().toString().trim();

                if (targetWaterStr.isEmpty()) {
                    Toast.makeText(WaterActivity.this, "Please enter a target water level!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int targetWater = Integer.parseInt(targetWaterStr);

                        if (targetWater <= 0) {
                            Toast.makeText(WaterActivity.this, "Target water level must be a positive number!", Toast.LENGTH_SHORT).show();
                        } else {
                            int existingTarget = dbHelper.getTargetWater(userId);

                            if (existingTarget > 0) {
                                new AlertDialog.Builder(WaterActivity.this)
                                        .setTitle("Update Target Water Level")
                                        .setMessage("You already have a water target set. Do you want to update it?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            boolean isTargetSet = dbHelper.setWaterTarget(userId, targetWater);

                                            if (isTargetSet) {
                                                Toast.makeText(WaterActivity.this, "Water Target Updated Successfully!", Toast.LENGTH_SHORT).show();

                                                txtTargetWaterW.setText("");

                                                updateWaterLevel();


                                            } else {
                                                Toast.makeText(WaterActivity.this, "Failed to Update Water Target!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            } else {

                                boolean isTargetSet = dbHelper.setWaterTarget(userId, targetWater);

                                if (isTargetSet) {
                                    Toast.makeText(WaterActivity.this, "Water Target Set Successfully!", Toast.LENGTH_SHORT).show();

                                    txtTargetWaterW.setText("");

                                    updateWaterLevel();

                                } else {
                                    Toast.makeText(WaterActivity.this, "Failed to Set Water Target!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(WaterActivity.this, "Please enter a valid number!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnAddWaterW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addWaterStr = txtAddWaterLevelW.getText().toString().trim(); // Assuming txtAddWaterW is your EditText for water input

                if (addWaterStr.isEmpty()) {
                    Toast.makeText(WaterActivity.this, "Please enter a water amount", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int addWater = Integer.parseInt(addWaterStr);

                        if (addWater <= 0) {
                            Toast.makeText(WaterActivity.this, "Water amount must be a positive number", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isWaterAdded = dbHelper.addWaterIntake(userId, addWater);

                            if (isWaterAdded) {
                                Toast.makeText(WaterActivity.this, "Water Intake Added Successfully", Toast.LENGTH_SHORT).show();
                                txtAddWaterLevelW.setText("");

                                updateWaterLevel();

                            } else {
                                Toast.makeText(WaterActivity.this, "Failed to Add Water Intake", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(WaterActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonDeleteTargetW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(WaterActivity.this)
                        .setTitle("Delete Target and Records")
                        .setMessage("This will remove your water target and all water intake records for today. Do you want to continue?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean isDeleted = dbHelper.deleteWaterTargetAndRecords(userId);

                            if (isDeleted) {
                                Toast.makeText(WaterActivity.this, "Water Target and Today's Records Deleted Successfully", Toast.LENGTH_SHORT).show();

                                txtWaterCountW.setText("0 ml");
                                circularProgressBarW.setProgress(0);
                                txtTargetWaterW.setText("");
                            } else {
                                Toast.makeText(WaterActivity.this, "Failed to Delete Water Target and Records", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btnBackW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });


    }



    private void updateWaterLevel(){
        int currentWater = dbHelper.getCurrentWaterIntake(userId);
        int targetWaterUpdated = dbHelper.getTargetWater(userId);

        if (targetWaterUpdated > 0) {
            int progress = (int) ((float) currentWater / targetWaterUpdated * 100);
            circularProgressBarW.setProgress(progress);
        } else {
            circularProgressBarW.setProgress(0);
        }

        txtWaterCountW.setText(currentWater + "ml");
    }
}
