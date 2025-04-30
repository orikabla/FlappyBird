package com.example.flappybird;

    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.widget.Button;
    import androidx.appcompat.app.AppCompatActivity;
    import android.view.View;
    import android.widget.ProgressBar;

    public class SettingsActivity extends AppCompatActivity {
        public static final String PREFS_NAME = "FlappyBirdPrefs";
        public static final String DIFFICULTY_KEY = "difficulty";
        public static final String DIFFICULTY_EASY = "easy";
        public static final String DIFFICULTY_MEDIUM = "medium";
        public static final String DIFFICULTY_HARD = "hard";

        private Button easyButton;
        private Button mediumButton;
        private Button hardButton;
        private ProgressBar loadingIndicator;
        private boolean isLoading = true;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            // Initialize views
            Button backButton = findViewById(R.id.backButton);
            easyButton = findViewById(R.id.easyButton);
            mediumButton = findViewById(R.id.mediumButton);
            hardButton = findViewById(R.id.hardButton);
            loadingIndicator = findViewById(R.id.loadingIndicator);

            // Disable buttons initially
            setButtonsEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Load preferences asynchronously
            new Thread(() -> {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String currentDifficulty = prefs.getString(DIFFICULTY_KEY, DIFFICULTY_MEDIUM);

                // Update UI on main thread
                runOnUiThread(() -> {
                    updateButtonStyles(currentDifficulty);
                    setButtonsEnabled(true);
                    loadingIndicator.setVisibility(View.GONE);
                    isLoading = false;
                });
            }).start();

            backButton.setOnClickListener(v -> {
                if (!isLoading) {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            easyButton.setOnClickListener(v -> {
                if (!isLoading) {
                    setButtonsEnabled(false);
                    saveDifficultyAsync(DIFFICULTY_EASY);
                }
            });

            mediumButton.setOnClickListener(v -> {
                if (!isLoading) {
                    setButtonsEnabled(false);
                    saveDifficultyAsync(DIFFICULTY_MEDIUM);
                }
            });

            hardButton.setOnClickListener(v -> {
                if (!isLoading) {
                    setButtonsEnabled(false);
                    saveDifficultyAsync(DIFFICULTY_HARD);
                }
            });
        }

        private void saveDifficultyAsync(String difficulty) {
            new Thread(() -> {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(DIFFICULTY_KEY, difficulty);
                editor.apply();

                // Update UI on main thread
                runOnUiThread(() -> {
                    updateButtonStyles(difficulty);
                    setButtonsEnabled(true);
                    Pipe.resetBaseSpeed(); // Reset pipe speed for new difficulty
                });
            }).start();
        }

        private void updateButtonStyles(String selectedDifficulty) {
            easyButton.setAlpha(selectedDifficulty.equals(DIFFICULTY_EASY) ? 1.0f : 0.5f);
            mediumButton.setAlpha(selectedDifficulty.equals(DIFFICULTY_MEDIUM) ? 1.0f : 0.5f);
            hardButton.setAlpha(selectedDifficulty.equals(DIFFICULTY_HARD) ? 1.0f : 0.5f);
        }

        private void setButtonsEnabled(boolean enabled) {
            easyButton.setEnabled(enabled);
            mediumButton.setEnabled(enabled);
            hardButton.setEnabled(enabled);
        }

        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }