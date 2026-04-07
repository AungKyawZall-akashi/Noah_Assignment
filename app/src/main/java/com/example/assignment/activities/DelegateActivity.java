package com.example.assignment.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

    private TextInputEditText etPhoneNumber;
    private MaterialButton btnSendSMS;

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

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSendSMS = findViewById(R.id.btnSendSMS);

        btnSendSMS.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
