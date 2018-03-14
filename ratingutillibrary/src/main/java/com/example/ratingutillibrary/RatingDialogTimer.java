package com.example.ratingutillibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;

/**
 * Timer to schedule the rate-me after a number of application launches.
 */
public class RatingDialogTimer {
    private static final String TAG = RatingDialogTimer.class.getSimpleName();

    private static final String PREF_NAME = "RateThisApp";
    private static final String KEY_INSTALL_DATE = "rta_install_date";
    private static final String KEY_LAUNCH_TIMES = "rta_launch_times";
    private static final String KEY_OPT_OUT = "rta_opt_out";
    private static final String KEY_APP_VERSION = "rta_app_version";

    private static String appVersion = "0.0.0";
    private static Date mInstallDate = new Date();
    private static int mLaunchTimes = 0;
    private static boolean mOptOut = false;

    /**
     * Note to pre-1.2 users: installDate and launchTimes are now parameters in
     * {@link #shouldShowRateDialog}.
     */
    public RatingDialogTimer() {
        // Intentionally empty. See the javadoc comment
    }

    public static void onStart(Context context, Bundle savedInstanceState) {
        // Only use FIRST launch of the activity
        if (savedInstanceState != null) {
            return;
        }
        saveInPreferences(context);
    }

    public static void onStart(Context context) {
        saveInPreferences(context);
    }

    public static void saveInPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // If it is the first launch, save the date and version in shared preference.
        if (pref.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            Date now = new Date();
            editor.putLong(KEY_INSTALL_DATE, now.getTime());
            try {
                editor.putString(KEY_APP_VERSION, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
                Log.i(TAG,"Version Name: " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "First install: " + now.toString());
        }
        // Increment launch times
        int launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        launchTimes++;
        editor.putInt(KEY_LAUNCH_TIMES, launchTimes);
        Log.d(TAG, "Launch times; " + launchTimes);

        editor.apply();

        mInstallDate = new Date(pref.getLong(KEY_INSTALL_DATE, 0));
        mLaunchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        mOptOut = pref.getBoolean(KEY_OPT_OUT, false);
        appVersion = pref.getString(KEY_APP_VERSION, "");
    }

    public static boolean shouldShowRateDialog(final Context context, int installDays, int launchTimes) {
        if (isUpdateInstalled(context)) {
            return true;
        }

        if (mOptOut) {
            return false;
        } else {
            if (mLaunchTimes >= launchTimes) {
                clearSharedPreferences(context);
                return true;
            }
            final long thresholdMillis = installDays * 24 * 60 * 60 * 1000L;
            if (new Date().getTime() - mInstallDate.getTime() >= thresholdMillis) {
                clearSharedPreferences(context);
                return true;
            } else {
                return false;
            }
        }
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_INSTALL_DATE);
        editor.remove(KEY_LAUNCH_TIMES);
        editor.apply();
    }

    /**
     * Set opt out flag. If it is true, the rate dialog will never shown unless app data is cleared.
     */
    public static void setOptOut(final Context context, boolean optOut) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_OPT_OUT, optOut);
        editor.apply();
    }

    public static boolean wasRated(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_OPT_OUT, false);
    }

    private static boolean isUpdateInstalled(final Context context) {
        try {
            String[] currentVersion = (context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName).split("\\.");
            String[] olderVersion = appVersion.split("\\.");

            Log.i("CurrentVersion", Arrays.toString(currentVersion));
            Log.i("olderVersion", Arrays.toString(olderVersion));


            for(int i = 0; i < currentVersion.length; i++){
                if (Integer.parseInt(currentVersion[i]) > Integer.parseInt(olderVersion[i])){
                    return true;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Package, application, or component name cannot be found.");
        }

        return false;
    }
}