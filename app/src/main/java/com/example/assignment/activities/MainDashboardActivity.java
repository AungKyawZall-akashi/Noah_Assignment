package com.example.assignment.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.assignment.R;
import com.example.assignment.adapters.WorkoutAdapter;
import com.example.assignment.database.AppDatabase;
import com.example.assignment.models.Workout;
import com.example.assignment.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDashboardActivity extends AppCompatActivity implements
        WorkoutAdapter.OnWorkoutClickListener, NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvWorkouts;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private AppDatabase database;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabAddWorkout;
    private LinearLayout llEmptyState;
    private TextView tvEmptyState, tvWelcomeMessage, tvStats, tvWorkoutCount, tvMotivation;
    private Button btnCreateFirstWorkout;
    private MaterialButton btnCreateWorkout;
    private ExecutorService executorService;
    private Handler mainHandler;

    private String[] motivations = {
            "💪 The only bad workout is the one that didn't happen!",
            "🏆 Your only limit is you. Be better than yesterday!",
            "🔥 Success starts with self-discipline!",
            "🌟 Dream it. Wish it. Do it!",
            "⚡ The pain you feel today will be the strength you feel tomorrow!",
            "🎯 Every workout is progress towards your goal!",
            "💯 You are capable of more than you know!",
            "🏃‍♂️ Make your future self proud!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main_dashboard);
            initializeViews();
            setupToolbar();
            setupNavigationDrawer();
            setupRecyclerView();
            setupSwipeGestures();
            setupClickListeners();
            setupMotivation();

            executorService = Executors.newSingleThreadExecutor();
            mainHandler = new Handler(Looper.getMainLooper());

            updateWelcomeMessage();
            loadWorkouts();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            rvWorkouts = findViewById(R.id.rvWorkouts);
            fabAddWorkout = findViewById(R.id.fabAddWorkout);
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navView);
            toolbar = findViewById(R.id.toolbar);
            llEmptyState = findViewById(R.id.llEmptyState);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
            tvStats = findViewById(R.id.tvStats);
            tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
            tvMotivation = findViewById(R.id.tvMotivation);
            btnCreateFirstWorkout = findViewById(R.id.btnCreateFirstWorkout);
            btnCreateWorkout = findViewById(R.id.btnCreateWorkout);

            database = AppDatabase.getInstance(this);
            sessionManager = new SessionManager(this);
            workoutList = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMotivation() {
        try {
            int randomIndex = (int) (Math.random() * motivations.length);
            tvMotivation.setText(motivations[randomIndex]);
        } catch (Exception e) {
            tvMotivation.setText("💪 Stay strong! Every day is a new opportunity!");
        }
    }

    private void updateWelcomeMessage() {
        try {
            String userName = sessionManager.getUserName();
            if (userName == null || userName.isEmpty()) {
                userName = "Fitness Enthusiast";
            }

            int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
            String greeting;
            if (hour < 12) {
                greeting = "Good Morning";
            } else if (hour < 17) {
                greeting = "Good Afternoon";
            } else {
                greeting = "Good Evening";
            }

            tvWelcomeMessage.setText(greeting + ", " + userName + "!");
        } catch (Exception e) {
            tvWelcomeMessage.setText("Welcome to FitLife!");
        }
    }

    private void setupToolbar() {
        try {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(R.string.app_name);
            }
            toolbar.setNavigationIcon(R.drawable.ic_menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNavigationDrawer() {
        try {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.getHeaderView(0);
            TextView tvUserName = headerView.findViewById(R.id.tvUserName);
            TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
            ImageView ivUserAvatar = headerView.findViewById(R.id.ivUserAvatar);

            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getUserEmail();

            tvUserName.setText(userName != null && !userName.isEmpty() ? userName : "User");
            tvUserEmail.setText(userEmail != null && !userEmail.isEmpty() ? userEmail : "user@example.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        try {
            workoutAdapter = new WorkoutAdapter(this);
            workoutAdapter.setGroupByCompletion(false);
            rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
            rvWorkouts.setAdapter(workoutAdapter);
            rvWorkouts.setItemAnimator(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWorkouts() {
        try {
            showEmptyState("Loading workouts...");

            executorService.execute(() -> {
                try {
                    int userId = sessionManager.getUserId();
                    List<Workout> workouts = database.workoutDao().getWorkoutsByUserId(userId);

                    mainHandler.post(() -> {
                        try {
                            updateWorkoutList(workouts);
                            updateStats(workouts);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(() -> {
                        showEmptyState("Error loading workouts. Please restart the app.");
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyState("Error loading workouts");
        }
    }

    private void updateWorkoutList(List<Workout> workouts) {
        try {
            workoutList.clear();
            if (workouts != null) {
                workoutList.addAll(workouts);
            }

            workoutAdapter.setWorkouts(workoutList);

            if (workoutList.isEmpty()) {
                showEmptyState(getString(R.string.no_workouts_message));
                rvWorkouts.setVisibility(View.GONE);
                llEmptyState.setVisibility(View.VISIBLE);
            } else {
                llEmptyState.setVisibility(View.GONE);
                rvWorkouts.setVisibility(View.VISIBLE);
            }

            int count = workoutList.size();
            tvWorkoutCount.setText(count + " workout" + (count != 1 ? "s" : ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStats(List<Workout> workouts) {
        try {
            int completedWorkouts = 0;
            if (workouts != null) {
                for (Workout workout : workouts) {
                    if (workout.isCompleted()) {
                        completedWorkouts++;
                    }
                }
            }

            int total = workouts != null ? workouts.size() : 0;
            String stats = "🏆 " + total + " workouts • ✓ " + completedWorkouts + " completed";
            tvStats.setText(stats);
        } catch (Exception e) {
            tvStats.setText("🏆 Keep pushing forward!");
        }
    }

    private void showEmptyState(String message) {
        try {
            tvEmptyState.setText(message);
            llEmptyState.setVisibility(View.VISIBLE);
            rvWorkouts.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        try {
            if (fabAddWorkout != null) {
                fabAddWorkout.setOnClickListener(v -> {
                    Intent intent = new Intent(MainDashboardActivity.this, CreateWorkoutActivity.class);
                    startActivity(intent);
                });
            }

            if (btnCreateFirstWorkout != null) {
                btnCreateFirstWorkout.setOnClickListener(v -> {
                    Intent intent = new Intent(MainDashboardActivity.this, CreateWorkoutActivity.class);
                    startActivity(intent);
                });
            }

            if (btnCreateWorkout != null) {
                btnCreateWorkout.setOnClickListener(v -> {
                    Intent intent = new Intent(MainDashboardActivity.this, CreateWorkoutActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkouts();
    }

    @Override
    public void onEditClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MainDashboardActivity.this, CreateWorkoutActivity.class);
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
                    Toast.makeText(this, R.string.deleting, Toast.LENGTH_SHORT).show();

                    executorService.execute(() -> {
                        try {
                            database.workoutDao().delete(workout);
                            database.exerciseDao().deleteExercisesByWorkoutId(workout.getId());

                            mainHandler.post(() -> {
                                loadWorkouts();
                                Toast.makeText(MainDashboardActivity.this,
                                        R.string.workout_deleted, Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            mainHandler.post(() -> {
                                Toast.makeText(MainDashboardActivity.this,
                                        "Error deleting workout", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDelegateClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MainDashboardActivity.this, DelegateActivity.class);
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
                    String message = isChecked ?
                            getString(R.string.workout_completed) :
                            getString(R.string.workout_incomplete);
                    Toast.makeText(MainDashboardActivity.this,
                            workout.getName() + " " + message, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onWorkoutClick(Workout workout) {
        if (workout != null) {
            Intent intent = new Intent(MainDashboardActivity.this, WorkoutDetailActivity.class);
            intent.putExtra("workout_id", workout.getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_my_workouts) {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(MainDashboardActivity.this, MyWorkoutsActivity.class));
            return true;
        } else if (id == R.id.nav_light_mode) {
            drawerLayout.closeDrawer(GravityCompat.START);
            sessionManager.setDarkModeEnabled(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
            return true;
        } else if (id == R.id.nav_dark_mode) {
            drawerLayout.closeDrawer(GravityCompat.START);
            sessionManager.setDarkModeEnabled(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            recreate();
            return true;
        } else if (id == R.id.nav_bmi) {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(MainDashboardActivity.this, BmiActivity.class));
            return true;
        } else if (id == R.id.nav_delegate) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Toast.makeText(this, R.string.delegate_instruction, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_logout) {
            logout();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupSwipeGestures() {
        Paint paint = new Paint();

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
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
                    new AlertDialog.Builder(MainDashboardActivity.this)
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
                        paint.setColor(ContextCompat.getColor(MainDashboardActivity.this, R.color.success_color));
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getLeft() + dX, (float) itemView.getBottom(), paint);
                    } else if (dX < 0) {
                        paint.setColor(ContextCompat.getColor(MainDashboardActivity.this, R.color.error_color));
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
                    Toast.makeText(MainDashboardActivity.this, workout.getName() + " " + getString(R.string.workout_completed), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainDashboardActivity.this, R.string.workout_deleted, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(MainDashboardActivity.this, "Error deleting workout", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(MainDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.no, null)
                    .show();
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
