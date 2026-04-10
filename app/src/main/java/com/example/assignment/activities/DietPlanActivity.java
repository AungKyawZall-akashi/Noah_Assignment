package com.example.assignment.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.google.android.material.appbar.MaterialToolbar;

public class DietPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.diet_plan_static);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String packageId = getIntent().getStringExtra(PackageDetailActivity.EXTRA_PACKAGE_ID);
        PackageItem item = PackageRepository.getById(packageId);

        TextView tv = findViewById(R.id.tvDietPlan);
        tv.setText(buildPlan(item));
    }

    private String buildPlan(PackageItem item) {
        String name = item != null ? item.getName() : "Package";
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" - Static Diet Plan\n\n");
        sb.append("Breakfast:\n- Oats + banana + nuts\n\n");
        sb.append("Lunch:\n- Grilled chicken/fish + rice + salad\n\n");
        sb.append("Snack:\n- Yogurt or fruit\n\n");
        sb.append("Dinner:\n- Veggies + protein + light carbs\n\n");
        sb.append("Water:\n- 2-3 liters/day\n\n");
        sb.append("Tip: Adjust portion size based on your goal.");
        return sb.toString();
    }
}
