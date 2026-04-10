package com.example.assignment.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.assignment.R;
import com.example.assignment.database.AppDatabase;
import com.example.assignment.models.Exercise;
import com.example.assignment.models.Workout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DelegateActivity extends AppCompatActivity {

    private static final String EXTRA_WORKOUT_ID = "workout_id";

    private MaterialToolbar toolbar;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etEmail;
    private MaterialButton btnSendSMS;
    private MaterialButton btnSendEmail;

    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;

    private int workoutId;
    private Workout workout;
    private String equipmentSummary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate);

        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        workoutId = getIntent().getIntExtra(EXTRA_WORKOUT_ID, -1);
        if (workoutId <= 0) {
            Toast.makeText(this, R.string.select_workout_first, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.delegate_workout);
        }
        toolbar.setNavigationOnClickListener(v -> navigateToDashboard());

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        btnSendSMS.setEnabled(false);
        btnSendEmail.setEnabled(false);
        loadWorkoutSummary();

        btnSendSMS.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "";
            if (!isValidPhone(phone)) {
                Toast.makeText(this, R.string.enter_valid_phone, Toast.LENGTH_SHORT).show();
                return;
            }

            String body = buildSmsBody();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phone)));
            intent.putExtra("sms_body", body);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, R.string.sms_failed, Toast.LENGTH_SHORT).show();
            }
        });

        btnSendEmail.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            if (!isValidEmail(email)) {
                Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
                return;
            }

            String workoutName = workout != null && workout.getName() != null ? workout.getName() : "Workout";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + Uri.encode(email)));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Workout: " + workoutName);
            intent.putExtra(Intent.EXTRA_TEXT, buildSmsBody());

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, R.string.sms_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWorkoutSummary() {
        executorService.execute(() -> {
            Workout loadedWorkout = database.workoutDao().getWorkoutById(workoutId);
            List<Exercise> exercises = database.exerciseDao().getExercisesByWorkoutId(workoutId);

            String equipment = buildEquipmentList(exercises);

            mainHandler.post(() -> {
                workout = loadedWorkout;
                equipmentSummary = equipment;
                btnSendSMS.setEnabled(workout != null);
                btnSendEmail.setEnabled(workout != null);

                if (workout == null) {
                    Toast.makeText(this, "Workout not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private String buildEquipmentList(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) return "";

        Set<String> equipments = new LinkedHashSet<>();
        for (Exercise exercise : exercises) {
            String equipment = exercise.getEquipment();
            if (equipment != null) {
                String trimmed = equipment.trim();
                if (!trimmed.isEmpty()) {
                    equipments.add(trimmed);
                }
            }
        }

        if (equipments.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (String item : equipments) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(item);
        }
        return sb.toString();
    }

    private String buildSmsBody() {
        String workoutName = workout != null && workout.getName() != null ? workout.getName() : "Workout";
        String description = workout != null && workout.getDescription() != null ? workout.getDescription().trim() : "";

        StringBuilder sb = new StringBuilder();
        sb.append("Workout: ").append(workoutName);

        if (!description.isEmpty()) {
            sb.append("\n\n").append("Description: ").append(description);
        }

        if (equipmentSummary != null && !equipmentSummary.isEmpty()) {
            sb.append("\n\n").append(getString(R.string.workout_equipment)).append("\n").append(equipmentSummary);
        }

        return sb.toString();
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleaned = phone.replace(" ", "").replace("-", "");
        return cleaned.length() >= 7;
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
