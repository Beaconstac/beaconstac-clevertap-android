package co.nearbee.clevertap.repository;

import java.util.ArrayList;
import java.util.Map;

/**
 * Copyright (C) 2019 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 2019-06-19
 */

public interface EventRepository {

    public void add(Map<String, Object> map);

    public ArrayList<Map<String, Object>> getAll();

    public void clear();

}
