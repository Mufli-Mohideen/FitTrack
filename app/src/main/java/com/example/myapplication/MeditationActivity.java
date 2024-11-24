package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class MeditationActivity extends AppCompatActivity {
    private int userId;
    private MaterialSpinner spinnerDuration;
    private ImageView playPauseButton;
    private boolean isPlaying = false;
    private int soundResource = 0;
    private MediaPlayer mediaPlayer;
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
        spinnerDuration = findViewById(R.id.spinner_duration);
        playPauseButton = findViewById(R.id.playPauseButton);

        playPauseButton.setImageResource(R.drawable.playbtn);

        playPauseButton.setEnabled(false);

        spinnerDuration.setItems("3 min", "5 min", "10 min", "15 min", "30 min");

        spinnerDuration.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Selected Meditation Duration: " + item, Snackbar.LENGTH_LONG).show();
                setMeditationSound(item);
                playPauseButton.setEnabled(true);
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseSound();
                } else if (soundResource != 0) { // Ensure a valid sound is selected
                    playSound();
                } else {
                    Toast.makeText(MeditationActivity.this, "Please select a meditation duration first!", Toast.LENGTH_SHORT).show();
                }
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

            // Change the button image to pause
            playPauseButton.setImageResource(R.drawable.pausebtn);

            // Handle sound completion
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                playPauseButton.setImageResource(R.drawable.playbtn);
                Toast.makeText(MeditationActivity.this, "Meditation completed!", Toast.LENGTH_SHORT).show();
            });

            Toast.makeText(this, "Playing Meditation Sound...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to play the sound.", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;

            // Change the button image to play
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

