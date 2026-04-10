package com.example.assignment.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.adapters.PackageAdapter;
import com.example.assignment.data.PackageRepository;
import com.example.assignment.models.PackageItem;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;

public class PackagesActivity extends AppCompatActivity implements PackageAdapter.OnPackageClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.packages);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        List<PackageItem> packages = PackageRepository.getPackages();
        RecyclerView rv = findViewById(R.id.rvPackages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PackageAdapter(packages, this));
        rv.setItemAnimator(null);
    }

    @Override
    public void onPackageClick(PackageItem item) {
        Intent intent = new Intent(this, PackageDetailActivity.class);
        intent.putExtra(PackageDetailActivity.EXTRA_PACKAGE_ID, item.getId());
        startActivity(intent);
    }
}
