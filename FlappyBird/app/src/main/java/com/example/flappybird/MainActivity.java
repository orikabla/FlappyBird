package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseApp.initializeApp(this);

    Window window = getWindow();
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    );

    GameView gameView = new GameView(this);
    setContentView(gameView);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    GameView gameView = (GameView) findViewById(R.id.gameView); // Ensure you reference the correct view
    if (gameView != null) {
        gameView.cleanup(); // Call cleanup method
    }
}}