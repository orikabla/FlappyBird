package com.example.flappybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
public class MenuBirdView extends View {
    private Paint paint;
    private RectF birdRect;
    private RectF beakRect;
    private float radius;
    private static final float ROTATION_ANGLE = -30; // -30 degrees for upward rotation

    public MenuBirdView(Context context) {
        super(context);
        init();
    }

    public MenuBirdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        birdRect = new RectF();
        beakRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) / 3f;
    }

   @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        canvas.save();
        canvas.rotate(ROTATION_ANGLE, centerX, centerY);

        // Draw bird body
        birdRect.set(centerX - radius, centerY - radius,
                    centerX + radius, centerY + radius);
        paint.setColor(0xFFFFB000);
        canvas.drawOval(birdRect, paint);

        // Draw eye
        paint.setColor(Color.WHITE);
        float eyeRadius = radius * 0.2f;
        canvas.drawCircle(centerX + radius * 0.3f,
                         centerY - radius * 0.2f,
                         eyeRadius, paint);

        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerX + radius * 0.3f,
                         centerY - radius * 0.2f,
                         eyeRadius * 0.5f, paint);

        // Modified beak drawing with rounded corners
        float beakLength = radius * 0.8f;
        float beakHeight = radius * 0.4f;
        float cornerRadius = beakHeight * 0.2f; // 20% of beak height for rounded corners
        beakRect.set(centerX + radius * 0.5f,
                    centerY - beakHeight/2,
                    centerX + radius * 0.5f + beakLength,
                    centerY + beakHeight/2);
        paint.setColor(0xFFFF6B00);
        canvas.drawRoundRect(beakRect, cornerRadius, cornerRadius, paint);

        canvas.restore();
    }
}