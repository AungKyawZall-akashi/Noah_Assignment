package com.example.assignment.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class BmiActivity extends AppCompatActivity {

    private TextInputEditText etWeight;
    private TextInputEditText etHeight;
    private MaterialButton btnCalculate;
    private android.widget.TextView tvBmiValue;
    private android.widget.TextView tvBmiCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bmi_calculator);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiCategory = findViewById(R.id.tvBmiCategory);

        btnCalculate.setOnClickListener(v -> calculateBmi());
    }

    private void calculateBmi() {
        String weightText = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String heightText = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";

        float weightKg = parsePositiveFloat(weightText);
        float heightCm = parsePositiveFloat(heightText);

        if (weightKg <= 0f || heightCm <= 0f) {
            Toast.makeText(this, R.string.enter_valid_bmi_values, Toast.LENGTH_SHORT).show();
            return;
        }

        float heightM = heightCm / 100f;
        float bmi = weightKg / (heightM * heightM);

        String bmiText = String.format(Locale.US, "BMI: %.1f", bmi);
        tvBmiValue.setText(bmiText);
        tvBmiCategory.setText(getCategoryText(bmi));
    }

    private float parsePositiveFloat(String value) {
        try {
            float v = Float.parseFloat(value);
            return v > 0f ? v : -1f;
        } catch (Exception e) {
            return -1f;
        }
    }

    private String getCategoryText(float bmi) {
        if (bmi < 18.5f) {
            return getString(R.string.bmi_underweight);
        }
        if (bmi < 25f) {
            return getString(R.string.bmi_normal);
        }
        if (bmi < 30f) {
            return getString(R.string.bmi_overweight);
        }
        return getString(R.string.bmi_obese);
    }
}
