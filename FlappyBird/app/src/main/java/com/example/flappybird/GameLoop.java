package com.example.flappybird;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {
    private boolean isRunning = false;
    private GameView gameView;
    private SurfaceHolder surfaceHolder;
    private static final long TARGET_FPS = 60;
    private static final long FRAME_TIME = 1000 / TARGET_FPS;

    public GameLoop(GameView gameView, SurfaceHolder surfaceHolder) {
        this.gameView = gameView;
        this.surfaceHolder = surfaceHolder;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    public void stopLoop() {
        isRunning = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long startTime;
        long elapsedTime;
        long sleepTime;

        while (isRunning) {
            startTime = System.currentTimeMillis();
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameView.update();
                        gameView.draw(canvas);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = FRAME_TIME - elapsedTime;
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}