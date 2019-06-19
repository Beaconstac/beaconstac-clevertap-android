package co.nearbee.clevertap.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import co.nearbee.clevertap.ProximityEventReceiver;

/**
 * Copyright (C) 2019 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 2019-06-19
 */

public class EnteredBeaconsRepository implements EventRepository, EventTimeRecorder {

    private final SharedPreferences beaconStore;
    private final SharedPreferences beaconTimeRecorder;
    private static final String ENTERED_BEACON_STORE = "entered_beacons";
    private static final String ENTERED_BEACON_TIMER = "entered_beacons_time";
    private final Gson gson = new Gson();

    public EnteredBeaconsRepository(Context context) {
        beaconStore = context.getSharedPreferences(ENTERED_BEACON_STORE, Context.MODE_PRIVATE);
        beaconTimeRecorder = context.getSharedPreferences(ENTERED_BEACON_TIMER, Context.MODE_PRIVATE);
    }

    @Override
    public void add(Map<String, Object> map) {
        final String beaconJson = gson.toJson(map);
        beaconStore.edit().putString(String.valueOf(map.get(ProximityEventReceiver.PROP_BEACON_ID)), beaconJson).apply();
    }

    @Override
    public ArrayList<Map<String, Object>> getAll() {
        final ArrayList<Map<String, Object>> beacons = new ArrayList<>();
        final Map<String, String> allBeacons = (Map<String, String>) beaconStore.getAll();
        for (String s : allBeacons.values()) {
            Map<String, Object> beaconMap = gson.fromJson(s, Map.class);
            beacons.add(beaconMap);
        }
        return beacons;
    }

    @Override
    public void clear() {
        beaconStore.edit().clear().apply();
        beaconTimeRecorder.edit().clear().apply();
    }

    @Override
    public void onProximityEvent(String id) {
        beaconTimeRecorder.edit().putLong(id, System.currentTimeMillis()).apply();
    }

    @Override
    public long getLastEventTime(String id) {
        return beaconTimeRecorder.getLong(id, 0);
    }


}
