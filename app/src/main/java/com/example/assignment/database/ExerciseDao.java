package com.example.assignment.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.assignment.models.Exercise;
import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY id ASC")
    List<Exercise> getExercisesByWorkoutId(int workoutId);

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    Exercise getExerciseById(int exerciseId);

    @Query("UPDATE exercises SET isCompleted = :completed WHERE id = :exerciseId")
    void updateCompletionStatus(int exerciseId, boolean completed);

    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    void deleteExercisesByWorkoutId(int workoutId);

    @Query("SELECT COUNT(*) FROM exercises WHERE workoutId = :workoutId AND isCompleted = 1")
    int getCompletedExerciseCount(int workoutId);

    @Query("SELECT COUNT(*) FROM exercises WHERE workoutId = :workoutId")
    int getTotalExerciseCount(int workoutId);
}