package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView mainName,mainCalory, mainWaterLevel, mainDayCount;
    private ProgressBar progressBarCalories,progressBarWater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainName = findViewById(R.id.mainName);
        mainCalory = findViewById(R.id.mainCalory);
        mainWaterLevel = findViewById(R.id.mainWaterLevel);
        mainDayCount = findViewById(R.id.mainDayCount);
        progressBarCalories = findViewById(R.id.progressBarCalories);
        progressBarWater = findViewById(R.id.progressBarWater);


        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);

        if (userId != -1) {
            SQLiteHelper dbHelper = new SQLiteHelper(this);
            String username = dbHelper.getUsernameById(userId);
            int currentCalories = dbHelper.getCurrentCalories(userId);
            int currentWater = dbHelper.getCurrentWaterIntake(userId);
            int currentMeditationStreak = dbHelper.getCurrentMeditationStreak(userId);
            int targetCalories = dbHelper.getTargetCalories(userId);
            int targetWater = dbHelper.getTargetWater(userId);

            if (username != null) {
                mainName.setText(username);
                mainCalory.setText(String.valueOf(currentCalories)+"KCal");
                mainWaterLevel.setText(String.valueOf(currentWater)+"ml");
                mainDayCount.setText(String.valueOf(currentMeditationStreak));

                int calorieProgress = targetCalories > 0 ? (currentCalories * 100) / targetCalories : 0;
                int waterProgress = targetWater > 0 ? (currentWater * 100) / targetWater : 0;

                progressBarCalories.setProgress(calorieProgress);
                progressBarWater.setProgress(waterProgress);


            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: User ID not passed", Toast.LENGTH_SHORT).show();
        }
    }
}