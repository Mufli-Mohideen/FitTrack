package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class MeditationActivity extends AppCompatActivity {
    private int userId;
    private MaterialSpinner spinnerDuration;
    private ImageView playPauseButton,btnBackM;
    private boolean isPlaying = false;
    private int soundResource = 0;
    private MediaPlayer mediaPlayer;
    private TextView remainingTimeTextView;
    private Handler handler = new Handler();
    private Runnable updateRemainingTimeTask;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spinnerDuration = findViewById(R.id.spinner_duration);
        playPauseButton = findViewById(R.id.playPauseButton);
        remainingTimeTextView = findViewById(R.id.remainingTime);
        btnBackM = findViewById(R.id.btnBackM);

        dbHelper = new SQLiteHelper(this);

        playPauseButton.setImageResource(R.drawable.playbtn);

        playPauseButton.setEnabled(false);

        spinnerDuration.setItems("Select Duration", "3 min", "5 min", "10 min", "15 min", "30 min");
        spinnerDuration.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (isPlaying) {
                    Snackbar.make(view, "Stop the current audio to change duration.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (position == 0) {
                    playPauseButton.setEnabled(false);
                    soundResource = 0;
                    Snackbar.make(view, "Please select a valid duration.", Snackbar.LENGTH_SHORT).show();
                } else {
                    setMeditationSound(item);
                    playPauseButton.setEnabled(true);
                }
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseSound();
                } else if (soundResource != 0) {
                    playSound();
                } else {
                    Toast.makeText(MeditationActivity.this, "Please select a meditation duration first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeditationActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });


    }
    private void setMeditationSound(String duration) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Map duration to the appropriate sound resource
        switch (duration) {
            case "3 min":
                soundResource = R.raw.three_minutes;
                break;
            case "5 min":
                soundResource = R.raw.five_minutes;
                break;
            case "10 min":
                soundResource = R.raw.ten_minutes;
                break;
            case "15 min":
                soundResource = R.raw.fifteen_minutes;
                break;
            case "30 min":
                soundResource = R.raw.thirty_minutes;
                break;
            default:
                soundResource = 0;
                Toast.makeText(this, "Invalid duration selected!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, soundResource);
        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;

            playPauseButton.setImageResource(R.drawable.pausebtn);
            remainingTimeTextView.setVisibility(View.VISIBLE);

            updateRemainingTime();

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                playPauseButton.setImageResource(R.drawable.playbtn);
                remainingTimeTextView.setVisibility(View.GONE);
                handler.removeCallbacks(updateRemainingTimeTask);
                Toast.makeText(MeditationActivity.this, "Meditation completed!", Toast.LENGTH_SHORT).show();
                boolean streakUpdated = dbHelper.updateMeditationStreak(userId);
            });

            Toast.makeText(this, "Playing Meditation Sound...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to play the sound.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateRemainingTime() {
        updateRemainingTimeTask = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int remainingTime = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
                    remainingTimeTextView.setText(formatTime(remainingTime));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateRemainingTimeTask);
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;

            playPauseButton.setImageResource(R.drawable.playbtn);
            Toast.makeText(this, "Meditation Sound Paused.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }



}

