package com.example.employee_gamification;

public class CEOLeaderboardModel {
    private String playerName;
    private int score;
    private long points;

    public CEOLeaderboardModel(String playerName, Long points, int score) {
        this.playerName = playerName;
        this.score = score;
        this.points = points;

    }

    public long getPoints()
    {
        return points;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }
}
