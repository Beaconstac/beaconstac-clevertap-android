package co.nearbee.clevertap.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import co.nearbee.clevertap.CleverTapWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 2453;
    CleverTapWrapper cleverTapWrapper;

    private Switch beaconMonitoring, geofenceMonitoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconMonitoring = findViewById(R.id.beacon_monitoring_enabled);
        geofenceMonitoring = findViewById(R.id.geofence_monitoring_enabled);

        beaconMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (getCleverTapWrapper() != null)
                    getCleverTapWrapper().enableBeaconMonitoring(isChecked);
                else
                    beaconMonitoring.setChecked(false);
            }
        });

        if (getCleverTapWrapper() != null) {
            beaconMonitoring.setChecked(getCleverTapWrapper().isBeaconMonitoringEnabled());
            geofenceMonitoring.setChecked(getCleverTapWrapper().isGeoFenceMonitoringEnabled());
        }

        geofenceMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (getCleverTapWrapper() != null)
                    getCleverTapWrapper().enableGeoFenceMonitoring(isChecked);
                else
                    geofenceMonitoring.setChecked(false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    private CleverTapWrapper getCleverTapWrapper() {
        if (cleverTapWrapper == null) {
            cleverTapWrapper = new CleverTapWrapper.Builder(this).build(new CleverTapWrapper.ErrorListener() {
                @Override
                public void onError(Exception e) {
                    // Need location permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            MainActivity.REQUEST_LOCATION_PERMISSION);
                }
            });
        }
        return cleverTapWrapper;
    }

}
