package com.example.assignment.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.assignment.models.Exercise;
import com.example.assignment.models.User;
import com.example.assignment.models.Workout;

@Database(entities = {User.class, Workout.class, Exercise.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract WorkoutDao workoutDao();
    public abstract ExerciseDao exerciseDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fitlife_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}