package com.petadopt.dao;

import java.util.List;
import java.util.Map;

/** Read-only aggregate queries (COUNT / GROUP BY / JOIN) that feed the dashboards. */
public interface StatsDAO {
    Map<String, Long> platformCounts();

    Map<String, Long> petsByType();

    List<Map<String, Object>> topShelters(int limit);

    Map<String, Long> shelterCounts(long shelterId);

    Map<String, Long> adopterCounts(long adopterId);
}
