package com.example.assignment.data;

import com.example.assignment.models.PackageItem;
import java.util.ArrayList;
import java.util.List;

public final class PackageRepository {

    private PackageRepository() {}

    public static List<PackageItem> getPackages() {
        List<PackageItem> items = new ArrayList<>();

        items.add(new PackageItem(
                "starter",
                "Starter Package",
                "Basic workout instructions + static diet plan + daily routine.",
                4.99
        ));

        items.add(new PackageItem(
                "pro",
                "Pro Package",
                "Advanced workouts + static diet plan + personal diet plan generator + daily routine.",
                9.99
        ));

        items.add(new PackageItem(
                "elite",
                "Elite Package",
                "All access: workouts, diet plans, personal plan, routines, and priority guidance.",
                14.99
        ));

        return items;
    }

    public static PackageItem getById(String id) {
        if (id == null) return null;
        for (PackageItem item : getPackages()) {
            if (id.equals(item.getId())) return item;
        }
        return null;
    }
}
