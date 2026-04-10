package com.example.assignment.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class PersonalDietPlanActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextInputEditText etGoal;
    private TextInputEditText etCalories;
    private TextInputEditText etPreference;
    private TextView tvPersonalPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_diet_plan);

        sessionManager = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.diet_plan_personal);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etGoal = findViewById(R.id.etGoal);
        etCalories = findViewById(R.id.etCalories);
        etPreference = findViewById(R.id.etPreference);
        tvPersonalPlan = findViewById(R.id.tvPersonalPlan);

        String existing = sessionManager.getPersonalDietPlan();
        if (existing != null && !existing.isEmpty()) {
            tvPersonalPlan.setText(existing);
        }

        MaterialButton btnGenerate = findViewById(R.id.btnGeneratePlan);
        btnGenerate.setOnClickListener(v -> generatePlan());
    }

    private void generatePlan() {
        String goal = etGoal.getText() != null ? etGoal.getText().toString().trim() : "";
        String caloriesText = etCalories.getText() != null ? etCalories.getText().toString().trim() : "";
        String preference = etPreference.getText() != null ? etPreference.getText().toString().trim() : "";

        int calories = parsePositiveInt(caloriesText);
        if (goal.isEmpty() || calories <= 0) {
            Toast.makeText(this, R.string.enter_valid_personal_plan, Toast.LENGTH_SHORT).show();
            return;
        }

        String plan = buildPlan(goal, calories, preference);
        sessionManager.savePersonalDietPlan(plan);
        tvPersonalPlan.setText(plan);
    }

    private int parsePositiveInt(String value) {
        try {
            int v = Integer.parseInt(value);
            return v > 0 ? v : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private String buildPlan(String goal, int calories, String preference) {
        StringBuilder sb = new StringBuilder();
        sb.append("Personal Diet Plan\n\n");
        sb.append("Goal: ").append(goal).append("\n");
        sb.append("Daily Calories: ").append(String.format(Locale.US, "%d", calories)).append(" kcal\n");
        if (preference != null && !preference.isEmpty()) {
            sb.append("Preference: ").append(preference).append("\n");
        }
        sb.append("\n");
        sb.append("Breakfast:\n- Protein + complex carbs + fruit\n\n");
        sb.append("Lunch:\n- Lean protein + vegetables + healthy fats\n\n");
        sb.append("Snack:\n- Nuts or yogurt\n\n");
        sb.append("Dinner:\n- Vegetables + protein, lighter carbs\n\n");
        sb.append("Notes:\n- Keep sugar low\n- Drink water\n- Adjust portions weekly");
        return sb.toString();
    }
}
