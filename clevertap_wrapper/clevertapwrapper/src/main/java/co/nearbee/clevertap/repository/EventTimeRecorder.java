package co.nearbee.clevertap.repository;

/**
 * Copyright (C) 2019 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 2019-06-19
 */

public interface EventTimeRecorder {

    public void onProximityEvent(String id);

    public long getLastEventTime(String id);

}
