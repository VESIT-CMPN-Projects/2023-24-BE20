package com.example.onlinecomplaintmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Splash duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the ImageView
        ImageView imageView = findViewById(R.id.imageView);

        // Delay for SPLASH_DURATION milliseconds before redirecting to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the next activity (replace YourNextActivity.class with your activity class)
                Intent intent = new Intent(SplashActivity.this, Dashboard.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent returning to it with back button
            }
        }, SPLASH_DURATION);
    }
}
