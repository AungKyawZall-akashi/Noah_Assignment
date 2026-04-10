package com.example.assignment.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.google.android.material.appbar.MaterialToolbar;

public class WorkoutInstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_instructions);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.workout_instructions);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String packageId = getIntent().getStringExtra(PackageDetailActivity.EXTRA_PACKAGE_ID);
        PackageItem item = PackageRepository.getById(packageId);

        TextView tv = findViewById(R.id.tvInstructions);
        tv.setText(buildInstructions(item));
    }

    private String buildInstructions(PackageItem item) {
        String name = item != null ? item.getName() : "Package";

        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" - Workout Instructions\n\n");
        sb.append("Warm-up (5-10 min)\n");
        sb.append("- Light cardio\n- Dynamic stretching\n\n");
        sb.append("Workout (30-45 min)\n");
        sb.append("- Squats 3x12\n- Push-ups 3x10\n- Rows 3x12\n- Plank 3x30s\n\n");
        sb.append("Cool-down (5-10 min)\n");
        sb.append("- Stretching\n- Breathing\n\n");
        sb.append("Tip: Consistency is key. Track progress weekly.");
        return sb.toString();
    }
}
