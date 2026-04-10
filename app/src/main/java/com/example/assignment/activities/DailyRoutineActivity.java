package com.example.assignment.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.example.assignment.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DailyRoutineActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextInputEditText etMorning;
    private TextInputEditText etAfternoon;
    private TextInputEditText etEvening;
    private TextView tvRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routine);

        sessionManager = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.daily_routine);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String packageId = getIntent().getStringExtra(PackageDetailActivity.EXTRA_PACKAGE_ID);
        PackageItem item = PackageRepository.getById(packageId);

        etMorning = findViewById(R.id.etMorning);
        etAfternoon = findViewById(R.id.etAfternoon);
        etEvening = findViewById(R.id.etEvening);
        tvRoutine = findViewById(R.id.tvRoutine);

        loadExistingOrDefault(item);

        MaterialButton btnSave = findViewById(R.id.btnSaveRoutine);
        btnSave.setOnClickListener(v -> saveRoutine());
    }

    private void loadExistingOrDefault(PackageItem item) {
        String morning = sessionManager.getDailyRoutineMorning();
        String afternoon = sessionManager.getDailyRoutineAfternoon();
        String evening = sessionManager.getDailyRoutineEvening();

        boolean hasSaved = (morning != null && !morning.trim().isEmpty())
                || (afternoon != null && !afternoon.trim().isEmpty())
                || (evening != null && !evening.trim().isEmpty());

        if (!hasSaved) {
            String template = buildRoutineTemplate(item);
            String[] parts = splitTemplate(template);
            morning = parts[0];
            afternoon = parts[1];
            evening = parts[2];
        }

        etMorning.setText(morning);
        etAfternoon.setText(afternoon);
        etEvening.setText(evening);

        tvRoutine.setText(formatRoutineForDisplay(morning, afternoon, evening));
    }

    private void saveRoutine() {
        String morning = etMorning.getText() != null ? etMorning.getText().toString().trim() : "";
        String afternoon = etAfternoon.getText() != null ? etAfternoon.getText().toString().trim() : "";
        String evening = etEvening.getText() != null ? etEvening.getText().toString().trim() : "";

        if (morning.isEmpty() && afternoon.isEmpty() && evening.isEmpty()) {
            Toast.makeText(this, R.string.enter_routine, Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveDailyRoutine(morning, afternoon, evening);
        tvRoutine.setText(formatRoutineForDisplay(morning, afternoon, evening));
        Toast.makeText(this, R.string.routine_saved, Toast.LENGTH_SHORT).show();
    }

    private String formatRoutineForDisplay(String morning, String afternoon, String evening) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.morning)).append(":\n").append(morning.isEmpty() ? "-" : morning).append("\n\n");
        sb.append(getString(R.string.afternoon)).append(":\n").append(afternoon.isEmpty() ? "-" : afternoon).append("\n\n");
        sb.append(getString(R.string.evening)).append(":\n").append(evening.isEmpty() ? "-" : evening);
        return sb.toString();
    }

    private String[] splitTemplate(String template) {
        String morning = "";
        String afternoon = "";
        String evening = "";

        if (template != null) {
            String[] sections = template.split("\n\n");
            if (sections.length > 0) morning = removeHeader(sections[0]);
            if (sections.length > 1) afternoon = removeHeader(sections[1]);
            if (sections.length > 2) evening = removeHeader(sections[2]);
        }

        return new String[] { morning.trim(), afternoon.trim(), evening.trim() };
    }

    private String removeHeader(String section) {
        if (section == null) return "";
        int idx = section.indexOf('\n');
        if (idx < 0) return section;
        return section.substring(idx + 1);
    }

    private String buildRoutineTemplate(PackageItem item) {
        String name = item != null ? item.getName() : "Package";
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n\n");
        sb.append(getString(R.string.morning)).append(":\n");
        sb.append("- 10 min walk\n- Breakfast (high protein)\n- 5 min stretch\n\n");
        sb.append(getString(R.string.afternoon)).append(":\n");
        sb.append("- Balanced lunch\n- 5 min mobility break\n\n");
        sb.append(getString(R.string.evening)).append(":\n");
        sb.append("- Workout session (30-45 min)\n- Dinner (light)\n- 10 min relaxation");
        return sb.toString();
    }
}
