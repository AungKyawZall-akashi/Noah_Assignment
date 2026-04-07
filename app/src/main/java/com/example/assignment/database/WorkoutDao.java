package com.example.assignment.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.assignment.models.Workout;
import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert
    long insert(Workout workout);

    @Update
    void update(Workout workout);

    @Delete
    void delete(Workout workout);

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY id DESC")
    List<Workout> getWorkoutsByUserId(int userId);

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    Workout getWorkoutById(int workoutId);

    @Query("UPDATE workouts SET isCompleted = :completed WHERE id = :workoutId")
    void updateCompletionStatus(int workoutId, boolean completed);

    @Query("DELETE FROM workouts WHERE userId = :userId")
    void deleteAllWorkoutsForUser(int userId);
}