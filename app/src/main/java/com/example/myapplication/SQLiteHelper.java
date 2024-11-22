package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitTrackApp.db";
    private static final int DATABASE_VERSION = 1;


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL);";
        db.execSQL(createUsersTable);

        // Calorie Target Table
        String createCalorieTargetTable = "CREATE TABLE IF NOT EXISTS calorie_target (" +
                "target_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "target_calories INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(user_id));";
        db.execSQL(createCalorieTargetTable);

        // Calorie Intake Table
        String createCalorieIntakeTable = "CREATE TABLE IF NOT EXISTS calorie_intake (" +
                "intake_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "calories INTEGER, " +
                "intake_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(user_id));";
        db.execSQL(createCalorieIntakeTable);

        // Water Target Table
        String createWaterTargetTable = "CREATE TABLE IF NOT EXISTS water_target (" +
                "target_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "target_water_level INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(user_id));";
        db.execSQL(createWaterTargetTable);

        // Water Intake Table
        String createWaterIntakeTable = "CREATE TABLE IF NOT EXISTS water_intake (" +
                "intake_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "water_amount INTEGER, " +
                "intake_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(user_id));";
        db.execSQL(createWaterIntakeTable);

        // Meditation Streak Table
        String createMeditationStreakTable = "CREATE TABLE IF NOT EXISTS meditation_streak (" +
                "streak_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "streak_count INTEGER, " +
                "last_meditation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(user_id));";
        db.execSQL(createMeditationStreakTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS calorie_target");
        db.execSQL("DROP TABLE IF EXISTS calorie_intake");
        db.execSQL("DROP TABLE IF EXISTS water_target");
        db.execSQL("DROP TABLE IF EXISTS water_intake");
        db.execSQL("DROP TABLE IF EXISTS meditation_streak");
        onCreate(db);
    }


    // Insert User
    public void insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String encryptedPassword = EncryptionUtils.hashPassword(password);

        values.put("username", username);
        values.put("email", email);
        values.put("password", encryptedPassword);

        db.insert("users", null, values);
        db.close();
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }


    // Password Comparison
    public boolean authenticateUser(String email, String enteredPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"password"}, "email=?",
                new String[]{email}, null, null, null);

        if (cursor != null) {
            int passwordColumnIndex = cursor.getColumnIndex("password");

            if (passwordColumnIndex != -1) {
                if (cursor.moveToFirst()) {
                    String storedPasswordHash = cursor.getString(passwordColumnIndex);
                    cursor.close();

                    String hashedEnteredPassword = EncryptionUtils.hashPassword(enteredPassword);
                    return storedPasswordHash.equals(hashedEnteredPassword);
                }
            } else {
                Log.e("SQLiteHelper", "Column 'password' not found in the 'users' table.");
            }
            cursor.close();
        }

        return false;

    }


    // Other methods for handling calorie, water, and meditation streak data will go here
}
