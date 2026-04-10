package com.example.assignment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment.R;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_PACKAGE_ID = "package_id";

    private TextInputEditText etCardNumber;
    private TextInputEditText etExpiry;
    private TextInputEditText etCvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String packageId = getIntent().getStringExtra(EXTRA_PACKAGE_ID);
        PackageItem item = PackageRepository.getById(packageId);
        if (item == null) {
            Toast.makeText(this, R.string.package_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.payment);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvPaymentPackage = findViewById(R.id.tvPaymentPackage);
        tvPaymentPackage.setText(item.getName() + " • " + String.format(Locale.US, "$%.2f", item.getPriceUsd()));

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);

        MaterialButton btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> submitPayment(packageId));
    }

    private void submitPayment(String packageId) {
        String card = etCardNumber.getText() != null ? etCardNumber.getText().toString().trim() : "";
        String expiry = etExpiry.getText() != null ? etExpiry.getText().toString().trim() : "";
        String cvv = etCvv.getText() != null ? etCvv.getText().toString().trim() : "";

        if (card.length() < 12 || cvv.length() < 3 || expiry.length() < 4) {
            Toast.makeText(this, R.string.enter_valid_payment, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_PACKAGE_ID, packageId);
        setResult(RESULT_OK, result);
        finish();
    }
}
