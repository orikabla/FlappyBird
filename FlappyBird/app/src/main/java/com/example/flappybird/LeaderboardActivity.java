package com.example.flappybird;

import android.widget.Button;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> scores;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;
    private StatsFragment statsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize views
        listView = findViewById(R.id.leaderboardList);
        Button backButton = findViewById(R.id.backButton);
        scores = new ArrayList<>();

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up list view
        listView.setBackgroundColor(Color.TRANSPARENT);
        adapter = new ArrayAdapter<String>(this, R.layout.leaderboard_item, scores) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                return textView;
            }
        };
        listView.setAdapter(adapter);

        // Add StatsFragment
        if (savedInstanceState == null) {
            statsFragment = new StatsFragment();
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.statsFragmentContainer, statsFragment)
                .commit();
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        loadLeaderboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (statsFragment != null) {
            statsFragment.updateStats();
        }
    }





private void loadLeaderboard() {
    db.collection("scores_v2")  // Changed from scores_v2
        .orderBy("score", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            scores.clear();
            int rank = 1;
            android.util.Log.d("Leaderboard", "Documents count: " + queryDocumentSnapshots.size());

            java.util.HashSet<String> shownPlayers = new java.util.HashSet<>();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Score score = doc.toObject(Score.class);
                if (score != null) {
                    String email = score.getPlayerEmail();
                    if (!shownPlayers.contains(email)) {
                        String username = email.substring(0, email.indexOf('@'));
                        String entry = String.format("%d. %s: %d", rank++, username, score.getScore());
                        scores.add(entry);
                        shownPlayers.add(email);
                        android.util.Log.d("Leaderboard", "Added score: " + entry);

                        if (rank > 10) {
                            break;
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        })
        .addOnFailureListener(e -> {
            android.util.Log.e("Leaderboard", "Error loading scores_v2", e);
            Toast.makeText(this, "Failed to load scores_v2: " + e.getMessage(),
                         Toast.LENGTH_SHORT).show();
        });
}
}