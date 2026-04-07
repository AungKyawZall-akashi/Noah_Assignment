package com.example.assignment.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.assignment.R;
import com.example.assignment.adapters.ExerciseAdapter;
import com.example.assignment.database.AppDatabase;
import com.example.assignment.models.Exercise;
import com.example.assignment.models.Workout;
import com.example.assignment.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateWorkoutActivity extends AppCompatActivity implements
        ExerciseAdapter.OnExerciseClickListener {

    private EditText etWorkoutName, etWorkoutDescription;
    private ImageView ivWorkoutImage;
    private Button btnAddImage, btnSaveWorkout, btnAddExercise;
    private RecyclerView rvExercises;
    private LinearLayout llNoExercises;
    private Toolbar toolbar;

    private List<Exercise> exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executorService;

    private Workout currentWorkout;
    private boolean isEditMode = false;
    private int workoutId = -1;
    private String workoutImagePath = null;

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        workoutImagePath = imageUri.toString();
                        Glide.with(this)
                                .load(imageUri)
                                .centerCrop()
                                .into(ivWorkoutImage);
                        Toast.makeText(this, R.string.image_added, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();
        exerciseList = new ArrayList<>();

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        checkEditMode();
        setupClickListeners();
        updateNoExercisesVisibility();
    }

    private void initializeViews() {
        etWorkoutName = findViewById(R.id.etWorkoutName);
        etWorkoutDescription = findViewById(R.id.etWorkoutDescription);
        ivWorkoutImage = findViewById(R.id.ivWorkoutImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        rvExercises = findViewById(R.id.rvExercises);
        llNoExercises = findViewById(R.id.llNoExercises);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.create_workout);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("workout_id")) {
            isEditMode = true;
            workoutId = intent.getIntExtra("workout_id", -1);
            loadWorkoutData();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_workout);
            }
        }
    }

    private void loadWorkoutData() {
        executorService.execute(() -> {
            currentWorkout = database.workoutDao().getWorkoutById(workoutId);
            List<Exercise> exercises = database.exerciseDao().getExercisesByWorkoutId(workoutId);

            runOnUiThread(() -> {
                if (currentWorkout != null) {
                    etWorkoutName.setText(currentWorkout.getName());
                    etWorkoutDescription.setText(currentWorkout.getDescription());
                    workoutImagePath = currentWorkout.getImagePath();

                    if (workoutImagePath != null && !workoutImagePath.isEmpty()) {
                        Glide.with(CreateWorkoutActivity.this)
                                .load(workoutImagePath)
                                .centerCrop()
                                .into(ivWorkoutImage);
                    }

                    exerciseList.clear();
                    exerciseList.addAll(exercises);
                    exerciseAdapter.notifyDataSetChanged();
                    updateNoExercisesVisibility();
                }
            });
        });
    }

    private void setupClickListeners() {
        btnAddImage.setOnClickListener(v -> openImagePicker());

        btnAddExercise.setOnClickListener(v -> showAddExerciseDialog(null));

        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showAddExerciseDialog(Exercise exerciseToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);
        builder.setView(dialogView);

        EditText etExerciseName = dialogView.findViewById(R.id.etExerciseName);
        EditText etSets = dialogView.findViewById(R.id.etSets);
        EditText etReps = dialogView.findViewById(R.id.etReps);
        EditText etEquipment = dialogView.findViewById(R.id.etEquipment);
        EditText etInstructions = dialogView.findViewById(R.id.etInstructions);

        boolean isEditing = exerciseToEdit != null;

        if (isEditing) {
            etExerciseName.setText(exerciseToEdit.getName());
            etSets.setText(exerciseToEdit.getSets());
            etReps.setText(exerciseToEdit.getReps());
            etEquipment.setText(exerciseToEdit.getEquipment());
            etInstructions.setText(exerciseToEdit.getInstructions());
        }

        builder.setTitle(isEditing ? R.string.edit_exercise : R.string.add_exercise)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String name = etExerciseName.getText().toString().trim();
                String sets = etSets.getText().toString().trim();
                String reps = etReps.getText().toString().trim();
                String equipment = etEquipment.getText().toString().trim();
                String instructions = etInstructions.getText().toString().trim();

                if (name.isEmpty()) {
                    etExerciseName.setError(getString(R.string.exercise_name_required));
                    etExerciseName.requestFocus();
                    return;
                }

                if (sets.isEmpty()) {
                    etSets.setError(getString(R.string.sets_required));
                    etSets.requestFocus();
                    return;
                }

                if (reps.isEmpty()) {
                    etReps.setError(getString(R.string.reps_required));
                    etReps.requestFocus();
                    return;
                }

                if (isEditing) {
                    // Update existing exercise
                    exerciseToEdit.setName(name);
                    exerciseToEdit.setSets(sets);
                    exerciseToEdit.setReps(reps);
                    exerciseToEdit.setEquipment(equipment);
                    exerciseToEdit.setInstructions(instructions);

                    int position = exerciseList.indexOf(exerciseToEdit);
                    exerciseList.set(position, exerciseToEdit);
                    exerciseAdapter.notifyItemChanged(position);
                    Toast.makeText(CreateWorkoutActivity.this,
                            R.string.exercise_updated, Toast.LENGTH_SHORT).show();
                } else {
                    // Add new exercise
                    Exercise newExercise = new Exercise(name, sets, reps, equipment, instructions, -1);
                    exerciseList.add(newExercise);
                    exerciseAdapter.notifyItemInserted(exerciseList.size() - 1);
                    Toast.makeText(CreateWorkoutActivity.this,
                            R.string.exercise_added, Toast.LENGTH_SHORT).show();
                }

                updateNoExercisesVisibility();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void updateNoExercisesVisibility() {
        if (exerciseList.isEmpty()) {
            llNoExercises.setVisibility(View.VISIBLE);
            rvExercises.setVisibility(View.GONE);
        } else {
            llNoExercises.setVisibility(View.GONE);
            rvExercises.setVisibility(View.VISIBLE);
        }
    }

    private void saveWorkout() {
        String name = etWorkoutName.getText().toString().trim();
        String description = etWorkoutDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etWorkoutName.setError(getString(R.string.workout_name_required));
            etWorkoutName.requestFocus();
            return;
        }

        if (exerciseList.isEmpty()) {
            Toast.makeText(this, R.string.add_at_least_one_exercise, Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveWorkout.setEnabled(false);
        btnSaveWorkout.setText(R.string.saving);

        executorService.execute(() -> {
            int userId = sessionManager.getUserId();
            long workoutIdResult;

            if (isEditMode && currentWorkout != null) {
                // Update existing workout
                currentWorkout.setName(name);
                currentWorkout.setDescription(description);
                currentWorkout.setImagePath(workoutImagePath);
                database.workoutDao().update(currentWorkout);
                workoutIdResult = currentWorkout.getId();

                // Delete old exercises and add new ones
                database.exerciseDao().deleteExercisesByWorkoutId(currentWorkout.getId());
            } else {
                // Create new workout
                Workout newWorkout = new Workout(name, description, userId);
                newWorkout.setImagePath(workoutImagePath);
                workoutIdResult = database.workoutDao().insert(newWorkout);
            }

            // Save all exercises with the workout ID
            int finalWorkoutId = (int) workoutIdResult;
            for (Exercise exercise : exerciseList) {
                exercise.setWorkoutId(finalWorkoutId);
                database.exerciseDao().insert(exercise);
            }

            runOnUiThread(() -> {
                btnSaveWorkout.setEnabled(true);
                btnSaveWorkout.setText(R.string.save_workout);

                String message = isEditMode ?
                        getString(R.string.workout_updated) :
                        getString(R.string.workout_created);
                Toast.makeText(CreateWorkoutActivity.this, message, Toast.LENGTH_SHORT).show();

                finish();
            });
        });
    }

    @Override
    public void onEditExercise(Exercise exercise, int position) {
        showAddExerciseDialog(exercise);
    }

    @Override
    public void onDeleteExercise(Exercise exercise, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_exercise)
                .setMessage(getString(R.string.delete_exercise_message, exercise.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    exerciseList.remove(position);
                    exerciseAdapter.notifyItemRemoved(position);
                    updateNoExercisesVisibility();
                    Toast.makeText(CreateWorkoutActivity.this,
                            R.string.exercise_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
