# beaconstac-clevertap-android
Beaconstac's side-by-side Android SDK integration with CleverTap

## Add the SDKs

### Add this to your project level build.gradle
```groovy
allprojects {
    repositories {
        …
        maven {
            url  "https://dl.bintray.com/mobstac/maven"
        }
        …
    }
}
```
### Add this to your app's build.gradle
```groovy
implementation 'co.nearbee:nearbeesdk:2.1.7'
implementation 'co.nearbee:nearbee-clevertap:1.0'
```

## Setup API Keys
Add the keys to your `AndroidManifest.xml`
```xml
<meta-data
    android:name="co.nearbee.api_key"
    android:value="YOUR_API_KEY" />

<meta-data
    android:name="co.nearbee.organization_id"
    android:value="YOUR_ORG_ID" />

<meta-data
    android:name="CLEVERTAP_ACCOUNT_ID"
    android:value="YOUR_ACCOUNT_ID" />

<meta-data
    android:name="CLEVERTAP_TOKEN"
    android:value="YOUR_TOKEN" />
```

## Initialize the SDK
Builder will return `null` if location permission is not granted.
```java
CleverTapWrapper cleverTapWrapper = new CleverTapWrapper.Builder(context).build(new CleverTapWrapper.ErrorListener() {
    @Override
    public void onError(Exception e) {
        // Need location permission
        
    }
});
```

### Enable beacon monitoring
```java
cleverTapWrapper.enableBeaconMonitoring(true);
```

### Enable geofence monitoring
```java
cleverTapWrapper.enableGeoFenceMonitoring(true);
```

### Check monitoring states
```java
boolean beaconMonitoringEnabled = cleverTapWrapper.isBeaconMonitoringEnabled();
boolean geofenceMonitoringEnabled = cleverTapWrapper.isGeoFenceMonitoringEnabled();
```
