package com.example.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;
import android.content.SharedPreferences;

public class Bird {
    // Difficulty-based constants
    private static final float GRAVITY_EASY = 0.8f;
    private static final float GRAVITY_MEDIUM = 0.8f;
    private static final float GRAVITY_HARD = 1.2f;

    private static final float JUMP_EASY = -16f;
    private static final float JUMP_MEDIUM = -15.3f;
    private static final float JUMP_HARD = -23f;

    private float x, y;
    private float radius;
    private float velocityY;
    private float gravity;
    private float jumpStrength;
    private final float maxFallSpeed = 30f;
    private final RectF birdRect;
    private final RectF beakRect;

    public Bird(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velocityY = 0;
        this.birdRect = new RectF();
        this.beakRect = new RectF();
        updateDifficultySettings();
    }

    private void updateDifficultySettings() {
        Context context = GameView.getGameContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
            String difficulty = prefs.getString(SettingsActivity.DIFFICULTY_KEY, SettingsActivity.DIFFICULTY_MEDIUM);

            switch (difficulty) {
                case SettingsActivity.DIFFICULTY_EASY:
                    gravity = GRAVITY_EASY;
                    jumpStrength = JUMP_EASY;
                    break;
                case SettingsActivity.DIFFICULTY_HARD:
                    gravity = GRAVITY_HARD;
                    jumpStrength = JUMP_HARD;
                    break;
                default: // MEDIUM
                    gravity = GRAVITY_MEDIUM;
                    jumpStrength = JUMP_MEDIUM;
            }
        } else {
            // Fallback to medium settings
            gravity = GRAVITY_MEDIUM;
            jumpStrength = JUMP_MEDIUM;
        }
    }

    public void update() {
        velocityY += gravity;
        if (velocityY > maxFallSpeed) {
            velocityY = maxFallSpeed;
        }
        y += velocityY;
    }

    public void jump() {
        velocityY = jumpStrength;
    }



    public void draw(Canvas canvas, Paint paint) {
        // Update rectangles once
        birdRect.set(x - radius, y - radius, x + radius, y + radius);

        paint.setColor(0xFFFFB000);
        canvas.drawOval(birdRect, paint);

        paint.setColor(Color.WHITE);
        float eyeRadius = radius * 0.2f;
        canvas.drawCircle(x + radius * 0.3f, y - radius * 0.2f, eyeRadius, paint);

        paint.setColor(Color.BLACK);
        canvas.drawCircle(x + radius * 0.3f, y - radius * 0.2f, eyeRadius * 0.5f, paint);

        // Modified beak drawing with rounded corners
        float beakLength = radius * 0.8f;
        float beakHeight = radius * 0.4f;
        float cornerRadius = beakHeight * 0.2f; // 20% of beak height for rounded corners
        beakRect.set(x + radius * 0.5f, y - beakHeight/2,
                    x + radius * 0.5f + beakLength, y + beakHeight/2);
        paint.setColor(0xFFFF6B00);
        canvas.drawRoundRect(beakRect, cornerRadius, cornerRadius, paint);
    }

    public float getY() { return y; }
    public float getX() { return x; }
    public float getRadius() { return radius; }
}