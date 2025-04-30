package com.example.flappybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.ref.WeakReference;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
        private GameLoop gameLoop;
        private Bird bird;
        private List<Pipe> pipes;
        private Paint paint;
        private int screenWidth, screenHeight;
        private boolean isGameOver;
        private int score;
        private RectF menuButtonBounds;
        private boolean isMenuButtonPressed;
        private boolean isPaused;
        private boolean isMenuOpen;
        private RectF resumeButtonBounds;
        private RectF restartButtonBounds;
        private RectF stopButtonBounds;
        private FirebaseFirestore db;
        private FirebaseAuth mAuth;
        private boolean scoreSubmitted;
        private static Context gameContext;
        private static WeakReference<Context> gameContextRef;

        public GameView(Context context) {
            super(context);
            gameContextRef = new WeakReference<>(context);
            gameContext = context;  // Store context
            getHolder().addCallback(this);
            gameLoop = new GameLoop(this, getHolder());
            setFocusable(true);
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            paint = new Paint();
            pipes = new ArrayList<>();
            isGameOver = false;
            isPaused = false;
            isMenuOpen = false;
            score = 0;
            scoreSubmitted = false;
        }


        // Add this static method outside of constructor
        public static Context getGameContext() {
            return gameContextRef != null ? gameContextRef.get() : null;
        }
        

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gameContextRef = null;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        menuButtonBounds = new RectF(20, 20, 100, 100);
        resumeButtonBounds = new RectF(screenWidth/2 - 150, screenHeight/2 - 100,
                                     screenWidth/2 + 150, screenHeight/2);
        restartButtonBounds = new RectF(screenWidth/2 - 150, screenHeight/2 + 50,
                                      screenWidth/2 + 150, screenHeight/2 + 150);
        stopButtonBounds = new RectF(screenWidth/2 - 150, screenHeight/2 + 200,
                                   screenWidth/2 + 150, screenHeight/2 + 300);

        resetGame();
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Not needed but must be implemented as part of SurfaceHolder.Callback
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoop.stopLoop();
    }

    private void resetGame() {
        bird = new Bird(screenWidth / 4f, screenHeight / 2f, 50f);
        pipes.clear();
        pipes.add(new Pipe(screenWidth, 200f, screenHeight / 3f, 350f));
        isGameOver = false;
        isPaused = false;
        isMenuOpen = false;
        score = 0;
        scoreSubmitted = false;
    }
private void saveScore() {
    android.util.Log.d("GameView", "saveScore called - starting save process");
    android.util.Log.d("GameView", "Current score: " + score);
    android.util.Log.d("GameView", "User authenticated: " + (mAuth.getCurrentUser() != null));
    android.util.Log.d("GameView", "Score already submitted: " + scoreSubmitted);

    if (score == 0) {
        android.util.Log.d("GameView", "Score is 0, not saving");
        return;
    }

    if (mAuth.getCurrentUser() == null) {
        android.util.Log.e("GameView", "User not authenticated, cannot save");
        return;
    }

    if (scoreSubmitted) {
        android.util.Log.d("GameView", "Score was already submitted");
        return;
    }

    final long timestamp = System.currentTimeMillis();
    java.util.Map<String, Object> scoreData = new java.util.HashMap<>();
    scoreData.put("playerEmail", mAuth.getCurrentUser().getEmail());
    scoreData.put("score", score);
    scoreData.put("timestamp", timestamp);

    android.util.Log.d("GameView", "About to save score data: " + scoreData);

    db.collection("scores_v2")
        .add(scoreData)
        .addOnSuccessListener(documentReference -> {
            android.util.Log.d("GameView", "Score saved successfully with ID: " + documentReference.getId());
            scoreSubmitted = true; // Set flag after successful save
        })
        .addOnFailureListener(e -> {
            android.util.Log.e("GameView", "Failed to save score", e);
            android.util.Log.e("GameView", "Error details: " + e.getMessage());
        })
        .addOnCompleteListener(task -> {
            android.util.Log.d("GameView", "Save operation completed");
        });
}

public void update() {
    if (!isPaused && !isGameOver) {
        bird.update();
        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update();

            if (!pipe.isScored() && bird.getX() > pipe.getX() + pipe.getWidth()) {
                score++;
                pipe.setScored(true);
            }

            if (pipe.isOffScreen()) {
                iterator.remove();
            }

            if (checkCollision(bird, pipe)) {
                isGameOver = true;
            }
        }

        // Check for collision with top/bottom
        if (bird.getY() - bird.getRadius() < 0 || bird.getY() + bird.getRadius() > screenHeight) {
            isGameOver = true;
        }

        // Add pipes if needed
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getX() < screenWidth - 1000) {
            float minY = screenHeight * 0.25f;
            float maxY = screenHeight * 0.65f;
            float gapY = (float) (Math.random() * (maxY - minY)) + minY;
            pipes.add(new Pipe(screenWidth, 200f, gapY, 350f));
        }
    }

    // Save score only once when the game is over and the score is greater than 0
    if (isGameOver && !scoreSubmitted && score > 0) {
        saveScore();
        scoreSubmitted = true; // Ensure this is set immediately
    }
}

    private boolean checkCollision(Bird bird, Pipe pipe) {
        float birdX = bird.getX();
        float birdY = bird.getY();
        float birdRadius = bird.getRadius();

        if (birdX + birdRadius > pipe.getX() && birdX - birdRadius < pipe.getX() + pipe.getWidth()) {
            if (birdY - birdRadius < pipe.getGapY() ||
                birdY + birdRadius > pipe.getGapY() + pipe.getGapHeight()) {
                return true;
            }
        }

        return birdY - birdRadius < 0 || birdY + birdRadius > screenHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.rgb(135, 206, 235)); // Sky blue background

            for (Pipe pipe : pipes) {
                pipe.draw(canvas, paint);
            }

            bird.draw(canvas, paint);

            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(score), screenWidth/2f, 100, paint);

            // Draw menu button
 // Draw menu button as a circle
 paint.setColor(Color.WHITE);
 float centerX = menuButtonBounds.centerX();
 float centerY = menuButtonBounds.centerY();
 float radius = menuButtonBounds.width() / 2;
 canvas.drawCircle(centerX, centerY, radius, paint);

 // Draw three dots
 paint.setColor(Color.BLACK);
 float dotRadius = radius * 0.12f;
 float dotSpacing = radius * 0.4f;
 canvas.drawCircle(centerX - dotSpacing, centerY, dotRadius, paint);
 canvas.drawCircle(centerX, centerY, dotRadius, paint);
 canvas.drawCircle(centerX + dotSpacing, centerY, dotRadius, paint);                if (isMenuOpen) {
                drawPauseMenu(canvas);
            }

            if (isGameOver) {
                drawGameOver(canvas);
                if (!scoreSubmitted) {
                    saveScore();
                }
            }
        }
    }

    private void drawPauseMenu(Canvas canvas) {
        // Semi-transparent background
        paint.setColor(Color.argb(128, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(resumeButtonBounds, 20, 20, paint);
        canvas.drawRoundRect(restartButtonBounds, 20, 20, paint);
        canvas.drawRoundRect(stopButtonBounds, 20, 20, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Resume", resumeButtonBounds.centerX(), resumeButtonBounds.centerY() + 20, paint);
        canvas.drawText("Restart", restartButtonBounds.centerX(), restartButtonBounds.centerY() + 20, paint);
        canvas.drawText("Exit", stopButtonBounds.centerX(), stopButtonBounds.centerY() + 20, paint);
    }

    private void drawGameOver(Canvas canvas) {
        paint.setColor(Color.argb(128, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", screenWidth/2f, screenHeight/2f - 150, paint);
        paint.setTextSize(80);
        canvas.drawText("Score: " + score, screenWidth/2f, screenHeight/2f + 0, paint);

        canvas.drawRoundRect(restartButtonBounds, 20, 20, paint);
        canvas.drawRoundRect(stopButtonBounds, 20, 20, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText("Restart", restartButtonBounds.centerX(), restartButtonBounds.centerY() + 20, paint);
        canvas.drawText("Exit", stopButtonBounds.centerX(), stopButtonBounds.centerY() + 20, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (menuButtonBounds.contains(touchX, touchY)) {
                    isMenuButtonPressed = true;
                    return true;
                }

                if (isMenuOpen || isGameOver) {
                    if (resumeButtonBounds.contains(touchX, touchY) && !isGameOver) {
                        isMenuOpen = false;
                        isPaused = false;
                    } else if (restartButtonBounds.contains(touchX, touchY)) {
                        resetGame();
                    } else if (stopButtonBounds.contains(touchX, touchY)) {
                        // Handle exit to main menu
                        ((android.app.Activity)getContext()).finish();
                    }
                } else {
                    bird.jump();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isMenuButtonPressed && menuButtonBounds.contains(touchX, touchY)) {
                    isMenuOpen = !isMenuOpen;
                    isPaused = isMenuOpen;
                }
                isMenuButtonPressed = false;
                break;
        }
        return true;
    }
    public void cleanup() {
        if (gameLoop != null) {
            gameLoop.stopLoop(); // Stop the game loop
        }
        gameContextRef = null; // Clear the context reference
    }
}