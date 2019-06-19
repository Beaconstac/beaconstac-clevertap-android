package co.nearbee.clevertap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import co.nearbee.ProximityEvent;
import co.nearbee.clevertap.repository.EnteredBeaconsRepository;
import co.nearbee.geofence.repository.models.GeoFence;
import co.nearbee.models.NearBeacon;

public class ProximityEventReceiver extends BroadcastReceiver {

    public static final String PROP_BEACON_ID = "beaconID";
    private static final String PROP_PLACE_ID = "placeID";
    private static final String PROP_PLACE_NAME = "placeName";

    private static final String PROP_GEOFENCE_ID = "geofenceID";
    private static final String PROP_GEOFENCE_NAME = "geofenceName";
    private static final String PROP_GEOFENCE_LAT = "lat";
    private static final String PROP_GEOFENCE_LNG = "lng";

    private static final String EVENT_GEOFENCE_ENTERED = "Geofence Entered";
    private static final String EVENT_BEACON_ENTERED = "Beacon Entered";
    private static final String EVENT_BEACON_EXITED = "Beacon Exited";

    private static final long DEFAULT_RESCAN_INTERVAL = TimeUnit.MINUTES.toMillis(5);

    @Override
    public void onReceive(Context context, Intent intent) {

        final CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
        final EnteredBeaconsRepository beaconsRepository = new EnteredBeaconsRepository(context);
        final SharedPreferences preferences = context.getSharedPreferences(
                CleverTapWrapper.PREF_CLEVERTAP_WRAPPER, Context.MODE_PRIVATE
        );
        final long beaconReEntryTime = preferences.getLong(
                CleverTapWrapper.PREF_BEACON_RESCAN_TIME, DEFAULT_RESCAN_INTERVAL
        );

        ProximityEvent.process(intent, new ProximityEvent.Listener() {
            @Override
            public void onBeaconDetected(NearBeacon beacon) {
                final long lastEventTime = beaconsRepository.getLastEventTime(beacon.getEddystoneUID());
                if ((System.currentTimeMillis() - lastEventTime) < beaconReEntryTime) {
                    return;
                }
                final HashMap<String, Object> eventMap = makeBeaconMap(beacon);
                beaconsRepository.add(eventMap);
                beaconsRepository.onProximityEvent(beacon.getEddystoneUID());
                if (cleverTapDefaultInstance != null) {
                    cleverTapDefaultInstance.pushEvent(EVENT_BEACON_ENTERED, eventMap);
                }
            }

            @Override
            public void onGeoFenceDetected(GeoFence geoFence) {
                if (cleverTapDefaultInstance != null) {
                    cleverTapDefaultInstance.pushEvent(EVENT_GEOFENCE_ENTERED, makeGeoFenceMap(geoFence));
                }
            }

            @Override
            public void onBeaconRegionExit() {
                final ArrayList<Map<String, Object>> enteredBeacons = beaconsRepository.getAll();
                beaconsRepository.clear();
                if (cleverTapDefaultInstance != null) {
                    for (Map<String, Object> beacon : enteredBeacons) {
                        cleverTapDefaultInstance.pushEvent(EVENT_BEACON_EXITED, beacon);
                    }
                }
            }
        });
    }

    private HashMap<String, Object> makeBeaconMap(NearBeacon beacon) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put(PROP_BEACON_ID, beacon.getEddystoneUID());
        map.put(PROP_PLACE_ID, beacon.getBusiness().getGooglePlaceID());
        map.put(PROP_PLACE_NAME, beacon.getBusiness().getName());
        return map;
    }

    private HashMap<String, Object> makeGeoFenceMap(GeoFence geoFence) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put(PROP_GEOFENCE_ID, String.valueOf(geoFence.getId()));
        map.put(PROP_GEOFENCE_NAME, geoFence.getName());
        map.put(PROP_GEOFENCE_LAT, geoFence.getLat());
        map.put(PROP_GEOFENCE_LNG, geoFence.getLng());
        return map;
    }

}
