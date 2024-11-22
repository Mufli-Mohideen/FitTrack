package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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
    public Integer authenticateUser(String email, String enteredPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"password", "user_id"}, "email=?",
                new String[]{email}, null, null, null);

        if (cursor != null) {
            int passwordColumnIndex = cursor.getColumnIndex("password");
            int userIdColumnIndex = cursor.getColumnIndex("user_id");

            if (passwordColumnIndex != -1) {
                if (cursor.moveToFirst()) {
                    String storedPasswordHash = cursor.getString(passwordColumnIndex);
                    int userId = cursor.getInt(userIdColumnIndex);
                    cursor.close();

                    String hashedEnteredPassword = EncryptionUtils.hashPassword(enteredPassword);
                    if (storedPasswordHash.equals(hashedEnteredPassword)) {
                        return userId;
                    }
                }
            } else {
                Log.e("SQLiteHelper", "Column 'password' not found in the 'users' table.");
            }
            cursor.close();
        }

        return null;

    }

    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"username"},
                "user_id=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor != null) {
            try {
                // Log column names for debugging
                Log.d("SQLiteHelper", "Columns in cursor: " + Arrays.toString(cursor.getColumnNames()));

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("username");

                    if (columnIndex >= 0) { // Ensure the column exists
                        return cursor.getString(columnIndex); // Return the username
                    } else {
                        Log.e("SQLiteHelper", "Column 'username' not found in query results.");
                    }
                } else {
                    Log.e("SQLiteHelper", "Cursor is empty. No matching user_id found.");
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("SQLiteHelper", "Query returned a null cursor.");
        }

        return null;
    }

    public int getCurrentCalories(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(calories) AS total_calories FROM calorie_intake " +
                        "WHERE user_id = ? AND DATE(datetime(intake_date / 1000, 'unixepoch')) = DATE('now')",
                new String[]{String.valueOf(userId)}
        );

        int totalCalories = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    totalCalories = cursor.getInt(cursor.getColumnIndexOrThrow("total_calories"));
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return totalCalories;
    }


    public int getCurrentWaterIntake(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(water_amount) AS total_water FROM water_intake WHERE user_id = ? AND DATE(intake_date) = DATE('now')",
                new String[]{String.valueOf(userId)}
        );

        int totalWater = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    totalWater = cursor.getInt(cursor.getColumnIndexOrThrow("total_water"));
                }
            } finally {
                cursor.close();
            }
        }
        return totalWater;
    }

    public int getCurrentMeditationStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT streak_count FROM meditation_streak WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int streakCount = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    streakCount = cursor.getInt(cursor.getColumnIndexOrThrow("streak_count"));
                }
            } finally {
                cursor.close();
            }
        }
        return streakCount;
    }

    public int getTargetCalories(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("calorie_target", new String[]{"target_calories"}, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int targetCalories = cursor.getInt(cursor.getColumnIndexOrThrow("target_calories"));
                cursor.close();
                return targetCalories;
            }
            cursor.close();
        }
        return 0;
    }

    public int getTargetWater(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("water_target", new String[]{"target_water_level"}, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int targetWater = cursor.getInt(cursor.getColumnIndexOrThrow("target_water_level"));
                cursor.close();
                return targetWater;
            }
            cursor.close();
        }
        return 0;
    }

    public boolean setTargetCalorie(int userId, int targetCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("target_calories", targetCalories);

        Cursor cursor = db.rawQuery("SELECT * FROM calorie_target WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            int rowsUpdated = db.update("calorie_target", values, "user_id = ?", new String[]{String.valueOf(userId)});
            cursor.close();
            return rowsUpdated > 0;
        } else {
            long rowId = db.insert("calorie_target", null, values);
            cursor.close();
            return rowId != -1;
        }
    }

    public boolean addCalories(int userId, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("calories", calories);
        values.put("intake_date", System.currentTimeMillis());

        long result = db.insert("calorie_intake", null, values);

        Log.d("CalorieTracker", "Adding calories for User ID: " + userId);

        db.close();

        return result != -1;
    }


    public boolean deleteTargetAndRecords(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Log.d("DeleteTarget", "Current Date: " + currentDate);

            ContentValues values = new ContentValues();
            values.put("calories", 0);

            int rowsUpdated = db.update("calorie_intake",
                    values,
                    "user_id = ? AND strftime('%Y-%m-%d', datetime(intake_date / 1000, 'unixepoch')) = DATE('now')",
                    new String[]{String.valueOf(userId)});

            Log.d("DeleteTarget", "Rows Updated in calorie_intake (set to 0): " + rowsUpdated);

            if (rowsUpdated > 0) {
                ContentValues targetValues = new ContentValues();
                targetValues.put("target_calories", 0);
                int rowsTargetUpdated = db.update("calorie_target", targetValues, "user_id = ?", new String[]{String.valueOf(userId)});

                Log.d("DeleteTarget", "Rows Updated in calorie_target: " + rowsTargetUpdated);

                db.setTransactionSuccessful();
                return true;
            } else {
                Log.d("DeleteTarget", "No records found to update for today.");
                return false;
            }
        } catch (Exception e) {
            Log.e("DeleteTarget", "Error in deleting target and records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }







}
