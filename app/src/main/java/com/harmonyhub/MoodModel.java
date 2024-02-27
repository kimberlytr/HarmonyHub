package com.harmonyhub;

public class MoodModel {
    private int id;
    private String mood;
    private int dayOfWeek;

    public MoodModel() {
        // Empty constructor required for database operations
    }

    public MoodModel(int id, String mood, int dayOfWeek) {
        this.id = id;
        this.mood = mood;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
