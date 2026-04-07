package com.example.assignment.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises",
        foreignKeys = @ForeignKey(entity = Workout.class,
                parentColumns = "id",
                childColumns = "workoutId",
                onDelete = ForeignKey.CASCADE))
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String sets;
    private String reps;
    private String equipment;
    private String instructions;
    private String imagePath;
    private boolean isCompleted;
    private int workoutId;

    // Constructor
    public Exercise(String name, String sets, String reps, String equipment,
                    String instructions, int workoutId) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.equipment = equipment;
        this.instructions = instructions;
        this.workoutId = workoutId;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
}