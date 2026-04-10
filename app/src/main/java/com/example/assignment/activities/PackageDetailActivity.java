package com.example.assignment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.example.assignment.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class PackageDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PACKAGE_ID = "package_id";

    private SessionManager sessionManager;
    private PackageItem packageItem;

    private TextView tvName;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvStatus;
    private View cardContent;
    private MaterialButton btnBuy;

    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && packageItem != null) {
                    sessionManager.addPurchasedPackage(packageItem.getId());
                    Toast.makeText(this, R.string.payment_successful, Toast.LENGTH_SHORT).show();
                    updateUi();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);

        sessionManager = new SessionManager(this);

        String packageId = getIntent().getStringExtra(EXTRA_PACKAGE_ID);
        packageItem = PackageRepository.getById(packageId);
        if (packageItem == null) {
            Toast.makeText(this, R.string.package_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.package_details);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        cardContent = findViewById(R.id.cardContent);
        btnBuy = findViewById(R.id.btnBuy);

        MaterialButton btnWorkoutInstructions = findViewById(R.id.btnWorkoutInstructions);
        MaterialButton btnDietPlan = findViewById(R.id.btnDietPlan);
        MaterialButton btnPersonalDietPlan = findViewById(R.id.btnPersonalDietPlan);
        MaterialButton btnDailyRoutine = findViewById(R.id.btnDailyRoutine);

        btnBuy.setOnClickListener(v -> startPayment());

        btnWorkoutInstructions.setOnClickListener(v -> openIfPurchased(WorkoutInstructionsActivity.class));
        btnDietPlan.setOnClickListener(v -> openIfPurchased(DietPlanActivity.class));
        btnPersonalDietPlan.setOnClickListener(v -> openIfPurchased(PersonalDietPlanActivity.class));
        btnDailyRoutine.setOnClickListener(v -> openIfPurchased(DailyRoutineActivity.class));

        updateUi();
    }

    private void updateUi() {
        tvName.setText(packageItem.getName());
        tvPrice.setText(String.format(Locale.US, "$%.2f", packageItem.getPriceUsd()));
        tvDescription.setText(packageItem.getDescription());

        boolean purchased = sessionManager.isPackagePurchased(packageItem.getId());
        btnBuy.setVisibility(purchased ? View.GONE : View.VISIBLE);
        cardContent.setVisibility(purchased ? View.VISIBLE : View.GONE);
        tvStatus.setText(purchased ? getString(R.string.unlocked) : getString(R.string.locked_buy_to_unlock));
    }

    private void startPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_PACKAGE_ID, packageItem.getId());
        paymentLauncher.launch(intent);
    }

    private void openIfPurchased(Class<?> activityClass) {
        if (packageItem == null) return;
        if (!sessionManager.isPackagePurchased(packageItem.getId())) {
            Toast.makeText(this, R.string.locked_buy_to_unlock, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(EXTRA_PACKAGE_ID, packageItem.getId());
        startActivity(intent);
    }
}
