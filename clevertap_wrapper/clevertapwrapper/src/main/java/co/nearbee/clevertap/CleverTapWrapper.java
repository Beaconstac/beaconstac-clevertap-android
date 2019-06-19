package co.nearbee.clevertap;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import co.nearbee.NearBee;
import co.nearbee.common.AsyncCall;

/**
 * Copyright (C) 2019 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 2019-06-18
 */

public class CleverTapWrapper {

    private final Context context;
    private NearBee nearBee;
    static final String PREF_CLEVERTAP_WRAPPER = "nearbee_clevertap";
    static final String PREF_BEACON_RESCAN_TIME = "beacon_reentry_scan_time";
    static final String PREF_BEACON_MONITORING_ENABLED = "beacon_monitoring_enabled";
    private final SharedPreferences sharedPreferences;

    private CleverTapWrapper(Builder builder) {
        this.context = builder.context;
        this.sharedPreferences = context.getSharedPreferences(PREF_CLEVERTAP_WRAPPER, Context.MODE_PRIVATE);
    }

    public static class Builder {

        final Context context;

        public Builder(Context context) {
            this.context = context;
        }

        @Nullable
        public CleverTapWrapper build() {
            return build(null);
        }

        @Nullable
        public CleverTapWrapper build(@Nullable ErrorListener errorListener) {
            if (!hasLocationPermission(context)) {
                if (errorListener != null) {
                    errorListener.onError(new Exception("Location permission is needed for NearBee"));
                }
                return null;
            }
            return new CleverTapWrapper(this);
        }
    }

    public interface ErrorListener {
        public void onError(Exception e);
    }

    private NearBee getNearBee() {
        if (nearBee == null) {
            boolean bgEnabled = sharedPreferences.getBoolean(PREF_BEACON_MONITORING_ENABLED, false);
            nearBee = new NearBee.Builder(context).setBackgroundNotificationsEnabled(bgEnabled).build();
        }
        return nearBee;
    }


    public void enableBeaconMonitoring(boolean enabled) {
        sharedPreferences.edit().putBoolean(PREF_BEACON_MONITORING_ENABLED, enabled).apply();
        getNearBee().enableBackgroundNotifications(enabled);
    }

    public AsyncCall<Void> enableGeoFenceMonitoring(boolean enabled) {
        if (enabled)
            return getNearBee().startGeoFenceMonitoring();
        else
            return getNearBee().stopGeoFenceMonitoring();
    }

    public boolean isBeaconMonitoringEnabled() {
        return getNearBee().isBackgroundNotificationEnabled();
    }

    public boolean isGeoFenceMonitoringEnabled() {
        return getNearBee().isGeoFenceMonitoringEnabled();
    }

    public void setMinimumDelayForBeaconEntry(long timeInMillis) {
        if (timeInMillis < TimeUnit.SECONDS.toMillis(1) || timeInMillis > TimeUnit.DAYS.toMillis(1)) {
            throw new IllegalArgumentException("Invalid time value");
        }
        sharedPreferences.edit().putLong(PREF_BEACON_RESCAN_TIME, timeInMillis).apply();
    }

    static boolean hasLocationPermission(Context context) {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}

