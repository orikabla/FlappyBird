package com.example.flappybird;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;
import android.content.SharedPreferences;
public class Pipe {
    private static final float SPEED_EASY = 8f;
    private static final float SPEED_MEDIUM = 10f;
    private static final float SPEED_HARD = 12f;
    private float x, width, gapY, gapHeight;
    private float speed;
    private static float baseSpeed = SPEED_MEDIUM;
    private boolean scored = false;

    public Pipe(float x, float width, float gapY, float gapHeight) {
        this.x = x;
        this.width = width;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
        updateCurrentSpeed();  // Get current speed on creation
    }

    private void updateCurrentSpeed() {
        Context context = GameView.getGameContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            String difficulty = prefs.getString(SettingsActivity.DIFFICULTY_KEY, SettingsActivity.DIFFICULTY_MEDIUM);

            switch (difficulty) {
                case SettingsActivity.DIFFICULTY_EASY:
                    speed = SPEED_EASY;
                    break;
                case SettingsActivity.DIFFICULTY_HARD:
                    speed = SPEED_HARD;
                    break;
                default: // MEDIUM
                    speed = SPEED_MEDIUM;
            }
        } else {
            speed = SPEED_MEDIUM;
        }
    }

    public static void updateBaseSpeed(int score) {
        Context context = GameView.getGameContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            String difficulty = prefs.getString(SettingsActivity.DIFFICULTY_KEY, SettingsActivity.DIFFICULTY_MEDIUM);

            switch (difficulty) {
                case SettingsActivity.DIFFICULTY_EASY:
                    baseSpeed = SPEED_EASY;
                    break;
                case SettingsActivity.DIFFICULTY_HARD:
                    baseSpeed = SPEED_HARD;
                    break;
                default: // MEDIUM
                    baseSpeed = SPEED_MEDIUM;
            }
        } else {
            baseSpeed = SPEED_MEDIUM;
        }
        baseSpeed += (score / 5); // Score-based speed increase
    }

    public void update() {
        x -= speed;
    }


    // Add this static method
public static void resetBaseSpeed() {
    Context context = GameView.getGameContext();
    if (context != null) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            String difficulty = prefs.getString(SettingsActivity.DIFFICULTY_KEY, SettingsActivity.DIFFICULTY_MEDIUM);

            switch (difficulty) {
                case SettingsActivity.DIFFICULTY_EASY:
                    baseSpeed = SPEED_EASY;
                    break;
                case SettingsActivity.DIFFICULTY_HARD:
                    baseSpeed = SPEED_HARD;
                    break;
                default: // MEDIUM
                    baseSpeed = SPEED_MEDIUM;
            }
        } catch (Exception e) {
            baseSpeed = SPEED_MEDIUM; // Fallback to medium if there's an error
        }
    } else {
        baseSpeed = SPEED_MEDIUM; // Fallback to medium if context is null
    }
}

    public void draw(Canvas canvas, Paint paint) {
        // Main pipe color
        paint.setColor(0xFF74BF2E);  // Flappy Bird green

        // Top pipe
        canvas.drawRect(new RectF(x, 0, x + width, gapY), paint);
        // Bottom pipe
        canvas.drawRect(new RectF(x, gapY + gapHeight, x + width, canvas.getHeight()), paint);

        // Pipe borders
        paint.setColor(0xFF527F24);  // Darker green
        float borderWidth = width * 0.1f;

        // Top pipe borders
        canvas.drawRect(new RectF(x, gapY - borderWidth, x + width, gapY), paint);
        canvas.drawRect(new RectF(x + width - borderWidth, 0, x + width, gapY), paint);

        // Bottom pipe borders
        canvas.drawRect(new RectF(x, gapY + gapHeight, x + width, gapY + gapHeight + borderWidth), paint);
        canvas.drawRect(new RectF(x + width - borderWidth, gapY + gapHeight, x + width, canvas.getHeight()), paint);

        // Pipe caps
        float capWidth = width * 1.2f;
        float capHeight = width * 0.3f;
        float capOffset = (capWidth - width) / 2;

        paint.setColor(0xFF74BF2E);  // Main green
        // Top cap
        canvas.drawRect(new RectF(x - capOffset, gapY - capHeight, x + width + capOffset, gapY), paint);
        // Bottom cap
        canvas.drawRect(new RectF(x - capOffset, gapY + gapHeight, x + width + capOffset, gapY + gapHeight + capHeight), paint);

        // Cap borders
        paint.setColor(0xFF527F24);  // Darker green
        // Top cap borders
        canvas.drawRect(new RectF(x - capOffset, gapY - capHeight, x + width + capOffset, gapY - capHeight + borderWidth), paint);
        // Bottom cap borders
        canvas.drawRect(new RectF(x - capOffset, gapY + gapHeight + capHeight - borderWidth, x + width + capOffset, gapY + gapHeight + capHeight), paint);
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
    }

    public float getGapY() {
        return gapY;
    }

    public float getGapHeight() {
        return gapHeight;
    }
}