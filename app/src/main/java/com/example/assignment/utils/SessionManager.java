package com.example.assignment.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "FitLifePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_PURCHASED_PACKAGES = "purchasedPackages";
    private static final String KEY_PERSONAL_DIET_PLAN = "personalDietPlan";
    private static final String KEY_DAILY_ROUTINE_MORNING = "dailyRoutineMorning";
    private static final String KEY_DAILY_ROUTINE_AFTERNOON = "dailyRoutineAfternoon";
    private static final String KEY_DAILY_ROUTINE_EVENING = "dailyRoutineEvening";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String email, String name, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public void setDarkModeEnabled(boolean enabled) {
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }

    public boolean isDarkModeEnabled() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public Set<String> getPurchasedPackageIds() {
        Set<String> stored = pref.getStringSet(KEY_PURCHASED_PACKAGES, null);
        if (stored == null) return new HashSet<>();
        return new HashSet<>(stored);
    }

    public boolean isPackagePurchased(String packageId) {
        if (packageId == null) return false;
        Set<String> stored = pref.getStringSet(KEY_PURCHASED_PACKAGES, null);
        return stored != null && stored.contains(packageId);
    }

    public void addPurchasedPackage(String packageId) {
        if (packageId == null) return;
        Set<String> current = getPurchasedPackageIds();
        current.add(packageId);
        editor.putStringSet(KEY_PURCHASED_PACKAGES, current);
        editor.apply();
    }

    public void savePersonalDietPlan(String planText) {
        editor.putString(KEY_PERSONAL_DIET_PLAN, planText != null ? planText : "");
        editor.apply();
    }

    public String getPersonalDietPlan() {
        return pref.getString(KEY_PERSONAL_DIET_PLAN, "");
    }

    public void saveDailyRoutine(String morning, String afternoon, String evening) {
        editor.putString(KEY_DAILY_ROUTINE_MORNING, morning != null ? morning : "");
        editor.putString(KEY_DAILY_ROUTINE_AFTERNOON, afternoon != null ? afternoon : "");
        editor.putString(KEY_DAILY_ROUTINE_EVENING, evening != null ? evening : "");
        editor.apply();
    }

    public String getDailyRoutineMorning() {
        return pref.getString(KEY_DAILY_ROUTINE_MORNING, "");
    }

    public String getDailyRoutineAfternoon() {
        return pref.getString(KEY_DAILY_ROUTINE_AFTERNOON, "");
    }

    public String getDailyRoutineEvening() {
        return pref.getString(KEY_DAILY_ROUTINE_EVENING, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
