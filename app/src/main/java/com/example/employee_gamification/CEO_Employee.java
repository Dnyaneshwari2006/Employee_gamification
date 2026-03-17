package com.example.employee_gamification;

public class CEO_Employee {
    private String name;
    private int points;
    private String imageUrl; // URL to profile image (optional)

    // 🔹 Required empty constructor for Firestore
    public CEO_Employee() {}

    public CEO_Employee(String name, int points, String imageUrl) {
        this.name = name;
        this.points = points;
        this.imageUrl = imageUrl;
    }

    // 🔹 Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
