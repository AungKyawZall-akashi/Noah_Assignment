package com.example.assignment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.adapters.ExerciseAdapter;
import com.example.assignment.database.AppDatabase;
import com.example.assignment.models.Exercise;
import com.example.assignment.models.Workout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutDetailActivity extends AppCompatActivity {

    private static final String EXTRA_WORKOUT_ID = "workout_id";

    private Toolbar toolbar;
    private TextView tvWorkoutName;
    private RecyclerView rvExercises;

    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;

    private ExerciseAdapter exerciseAdapter;
    private final List<Exercise> exerciseList = new ArrayList<>();

    private int workoutId;
    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        workoutId = getIntent().getIntExtra(EXTRA_WORKOUT_ID, -1);
        if (workoutId <= 0) {
            Toast.makeText(this, "Invalid workout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadWorkout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkout();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        rvExercises = findViewById(R.id.rvExercises);

        View fabAddExercise = findViewById(R.id.fabAddExercise);
        if (fabAddExercise != null) {
            fabAddExercise.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.workout_details);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        exerciseAdapter = new ExerciseAdapter(exerciseList, null, false);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void loadWorkout() {
        executorService.execute(() -> {
            Workout loadedWorkout = database.workoutDao().getWorkoutById(workoutId);
            List<Exercise> exercises = database.exerciseDao().getExercisesByWorkoutId(workoutId);

            mainHandler.post(() -> {
                workout = loadedWorkout;
                if (workout == null) {
                    Toast.makeText(this, "Workout not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                tvWorkoutName.setText(workout.getName());

                exerciseList.clear();
                if (exercises != null) {
                    exerciseList.addAll(exercises);
                }
                exerciseAdapter.notifyDataSetChanged();
                invalidateOptionsMenu();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem completeItem = menu.findItem(R.id.action_toggle_completed);
        if (completeItem != null) {
            boolean completed = workout != null && workout.isCompleted();
            completeItem.setTitle(completed ? R.string.mark_incomplete : R.string.mark_complete);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit_workout) {
            openEditWorkout();
            return true;
        } else if (id == R.id.action_delete_workout) {
            confirmDeleteWorkout();
            return true;
        } else if (id == R.id.action_toggle_completed) {
            toggleCompleted();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEditWorkout() {
        if (workout == null) return;
        Intent intent = new Intent(this, CreateWorkoutActivity.class);
        intent.putExtra(EXTRA_WORKOUT_ID, workout.getId());
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    private void confirmDeleteWorkout() {
        if (workout == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_workout)
                .setMessage(getString(R.string.delete_workout_message, workout.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteWorkout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteWorkout() {
        if (workout == null) return;

        executorService.execute(() -> {
            try {
                database.exerciseDao().deleteExercisesByWorkoutId(workout.getId());
                database.workoutDao().delete(workout);

                mainHandler.post(() -> {
                    Toast.makeText(this, R.string.workout_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                mainHandler.post(() ->
                        Toast.makeText(this, "Error deleting workout", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void toggleCompleted() {
        if (workout == null) return;

        boolean newValue = !workout.isCompleted();
        executorService.execute(() -> {
            try {
                workout.setCompleted(newValue);
                database.workoutDao().update(workout);

                mainHandler.post(() -> {
                    String message = newValue ?
                            getString(R.string.workout_completed) :
                            getString(R.string.workout_incomplete);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                });
            } catch (Exception e) {
                mainHandler.post(() ->
                        Toast.makeText(this, "Error updating workout", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
