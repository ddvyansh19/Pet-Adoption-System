package com.petadopt.service;

import com.petadopt.dao.StatsDAO;
import com.petadopt.dao.StatsDAOImpl;
import java.util.LinkedHashMap;
import java.util.Map;

/** Builds the data shown on each dashboard (counts, breakdowns and rates). */
public class AnalyticsService {
    private final StatsDAO statsDAO = new StatsDAOImpl();

    public Map<String, Object> adminDashboard() {
        Map<String, Long> counts = statsDAO.platformCounts();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("stats", counts);
        model.put("petsByType", statsDAO.petsByType());
        model.put("topShelters", statsDAO.topShelters(5));
        model.put("adoptionRate", rate(counts.get("adopted_pets"), counts.get("total_pets")));
        return model;
    }

    public Map<String, Object> shelterDashboard(long shelterId) {
        Map<String, Long> counts = statsDAO.shelterCounts(shelterId);
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("stats", counts);
        model.put("adoptionRate", rate(counts.get("adopted_pets"), counts.get("total_pets")));
        return model;
    }

    public Map<String, Object> adopterDashboard(long adopterId) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("stats", statsDAO.adopterCounts(adopterId));
        return model;
    }

    /** Percentage with one decimal place; 0 when the denominator is missing or zero. */
    static double rate(Long part, Long total) {
        if (part == null || total == null || total == 0) {
            return 0.0;
        }
        return Math.round(part * 1000.0 / total) / 10.0;
    }
}
