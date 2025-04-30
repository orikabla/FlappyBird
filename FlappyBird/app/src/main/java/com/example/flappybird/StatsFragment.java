package com.example.flappybird;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsFragment extends Fragment {
    private TextView highScoreText;
    private TextView gamesPlayedText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        highScoreText = view.findViewById(R.id.highScoreText);
        gamesPlayedText = view.findViewById(R.id.gamesPlayedText);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadPlayerStats();

        return view;
    }


private void loadPlayerStats() {
    if (mAuth.getCurrentUser() != null) {
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Get high score
        db.collection("scores_v2")
            .whereEqualTo("playerEmail", userEmail)
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (isAdded()) { // Ensure fragment is attached
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Score score = queryDocumentSnapshots.getDocuments().get(0).toObject(Score.class);
                        highScoreText.setText("High Score: " + (score != null ? score.getScore() : 0));
                    } else {
                        highScoreText.setText("High Score: 0");
                    }
                }
            })
            .addOnFailureListener(e -> {
                if (isAdded()) {
                    highScoreText.setText("High Score: Error");
                }
                android.util.Log.e("StatsFragment", "Failed to fetch high score", e);
            });

        // Get games played
        db.collection("scores_v2")
            .whereEqualTo("playerEmail", userEmail)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (isAdded()) { // Ensure fragment is attached
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Long> timestamps = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Score score = document.toObject(Score.class);
                            if (score != null) {
                                timestamps.add(score.getTimestamp());
                            }
                        }

                        int gameCount = timestamps.isEmpty() ? 0 : countGameSessions(timestamps);
                        gamesPlayedText.setText("Games Played: " + gameCount);
                    } else {
                        gamesPlayedText.setText("Games Played: 0");
                    }
                }
            })
            .addOnFailureListener(e -> {
                if (isAdded()) {
                    gamesPlayedText.setText("Games Played: Error");
                }
                android.util.Log.e("StatsFragment", "Failed to fetch games played", e);
            });
    } else {
        highScoreText.setText("High Score: N/A");
        gamesPlayedText.setText("Games Played: N/A");
        android.util.Log.w("StatsFragment", "User not authenticated");
    }
}

    private int countGameSessions(List<Long> timestamps) {
        if (timestamps.isEmpty()) return 0;

        int sessions = 1;
        final long SESSION_TIMEOUT = 30000; // 30 seconds between games

        for (int i = 1; i < timestamps.size(); i++) {
            long timeDiff = timestamps.get(i) - timestamps.get(i-1);
            if (timeDiff > SESSION_TIMEOUT) {
                sessions++;
            }
        }

        return sessions;
    }

    public void updateStats() {
        loadPlayerStats();
    }
}