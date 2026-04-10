package com.example.assignment.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.adapters.WorkoutAdapter;
import com.example.assignment.database.AppDatabase;
import com.example.assignment.models.Workout;
import com.example.assignment.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyWorkoutsActivity extends AppCompatActivity implements WorkoutAdapter.OnWorkoutClickListener {

    private MaterialToolbar toolbar;
    private RecyclerView rvWorkouts;
    private LinearLayout llEmptyState;
    private TextView tvEmptyState;

    private WorkoutAdapter workoutAdapter;
    private final List<Workout> workoutList = new ArrayList<>();

    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workouts);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.my_workouts);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvWorkouts = findViewById(R.id.rvWorkouts);
        llEmptyState = findViewById(R.id.llEmptyState);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        workoutAdapter = new WorkoutAdapter(this);
        workoutAdapter.setGroupByCompletion(true);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
        rvWorkouts.setAdapter(workoutAdapter);
        rvWorkouts.setItemAnimator(null);
        setupSwipeGestures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkouts();
    }

    private void loadWorkouts() {
        showEmptyState("Loading workouts...");

        executorService.execute(() -> {
            try {
                int userId = sessionManager.getUserId();
                List<Workout> workouts = database.workoutDao().getWorkoutsByUserId(userId);
                mainHandler.post(() -> updateWorkoutList(workouts));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> showEmptyState("Error loading workouts"));
            }
        });
    }

    private void updateWorkoutList(List<Workout> workouts) {
        workoutList.clear();
        if (workouts != null) {
            workoutList.addAll(workouts);
        }

        workoutAdapter.setWorkouts(workoutList);

        if (workoutList.isEmpty()) {
            showEmptyState(getString(R.string.no_workouts_message));
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvWorkouts.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState(String message) {
        tvEmptyState.setText(message);
        llEmptyState.setVisibility(View.VISIBLE);
        rvWorkouts.setVisibility(View.GONE);
    }

    @Override
    public void onEditClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MyWorkoutsActivity.this, CreateWorkoutActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("is_edit", true);
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteClick(Workout workout) {
        if (workout == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_workout)
                .setMessage(getString(R.string.delete_workout_message, workout.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteWorkout(workout);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDelegateClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MyWorkoutsActivity.this, DelegateActivity.class);
            intent.putExtra("workout_id", workout.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onCompleteClick(Workout workout, boolean isChecked) {
        if (workout == null) return;

        executorService.execute(() -> {
            try {
                workout.setCompleted(isChecked);
                database.workoutDao().update(workout);

                mainHandler.post(() -> {
                    loadWorkouts();
                    String message = isChecked ? getString(R.string.workout_completed) : getString(R.string.workout_incomplete);
                    Toast.makeText(MyWorkoutsActivity.this, workout.getName() + " " + message, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupSwipeGestures() {
        Paint paint = new Paint();

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getBindingAdapterPosition();
                if (workoutAdapter.isHeaderAt(position)) {
                    return 0;
                }
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Workout workout = workoutAdapter.getWorkoutAt(position);
                if (workout == null) {
                    workoutAdapter.notifyItemChanged(position);
                    return;
                }

                workoutAdapter.notifyItemChanged(position);

                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(MyWorkoutsActivity.this)
                            .setTitle(R.string.delete_workout)
                            .setMessage(getString(R.string.delete_workout_message, workout.getName()))
                            .setPositiveButton(R.string.delete, (dialog, which) -> deleteWorkout(workout))
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    markWorkoutCompleted(workout);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX > 0) {
                        paint.setColor(ContextCompat.getColor(MyWorkoutsActivity.this, R.color.success_color));
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getLeft() + dX, (float) itemView.getBottom(), paint);
                    } else if (dX < 0) {
                        paint.setColor(ContextCompat.getColor(MyWorkoutsActivity.this, R.color.error_color));
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(rvWorkouts);
    }

    private void markWorkoutCompleted(Workout workout) {
        executorService.execute(() -> {
            try {
                workout.setCompleted(true);
                database.workoutDao().update(workout);

                mainHandler.post(() -> {
                    loadWorkouts();
                    Toast.makeText(MyWorkoutsActivity.this, workout.getName() + " " + getString(R.string.workout_completed), Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> loadWorkouts());
            }
        });
    }

    private void deleteWorkout(Workout workout) {
        Toast.makeText(this, R.string.deleting, Toast.LENGTH_SHORT).show();
        executorService.execute(() -> {
            try {
                database.workoutDao().delete(workout);
                database.exerciseDao().deleteExercisesByWorkoutId(workout.getId());

                mainHandler.post(() -> {
                    loadWorkouts();
                    Toast.makeText(MyWorkoutsActivity.this, R.string.workout_deleted, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(MyWorkoutsActivity.this, "Error deleting workout", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onWorkoutClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MyWorkoutsActivity.this, WorkoutDetailActivity.class);
            intent.putExtra("workout_id", workout.getId());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
