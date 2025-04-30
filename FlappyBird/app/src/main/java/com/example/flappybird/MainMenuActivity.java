package com.example.flappybird;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.ImageButton;

public class MainMenuActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StatsFragment statsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views
        TextView welcomeText = findViewById(R.id.welcomeText);
        Button playButton = findViewById(R.id.playButton);
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set welcome message
        String email = mAuth.getCurrentUser() != null ?
                      mAuth.getCurrentUser().getEmail() : "Player";
        welcomeText.setText("Welcome, " + email + "!");

        // Initialize and add the StatsFragment
        if (savedInstanceState == null) {
            statsFragment = new StatsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.statsFragment, statsFragment);
            fragmentTransaction.commit();
        }

        // Set click listeners
        playButton.setOnClickListener(v -> startGame());
        leaderboardButton.setOnClickListener(v -> openLeaderboard());
        logoutButton.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (statsFragment != null) {
            statsFragment.updateStats();
        }
    }

    private void openLeaderboard() {
        Intent intent = new Intent(MainMenuActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }

    private void startGame() {
        Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}