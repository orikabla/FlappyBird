package com.example.flappybird;

public class Score {
    private String playerEmail;
    private int score;
    private long timestamp;

    public Score() {} // Required for Firebase

    public Score(String playerEmail, int score) {
        this.playerEmail = playerEmail;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }


    public Score(String playerEmail, int score, long timestamp) {
        this.playerEmail = playerEmail;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getPlayerEmail() { return playerEmail; }
    public int getScore() { return score; }
    public long getTimestamp() { return timestamp; }

    public void setPlayerEmail(String playerEmail) { this.playerEmail = playerEmail; }
    public void setScore(int score) { this.score = score; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}