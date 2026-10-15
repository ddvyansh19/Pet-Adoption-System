package com.petadopt.service;

import com.petadopt.dao.SettingDAO;
import com.petadopt.dao.SettingDAOImpl;
import com.petadopt.util.Validator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Singleton with an in-memory cache of the system settings (refreshed whenever an admin saves). */
public final class SettingService {
    public static final String SITE_NAME = "site_name";
    public static final String CONTACT_EMAIL = "contact_email";
    public static final String ALLOW_REGISTRATION = "allow_registration";
    public static final String MAX_ACTIVE_APPLICATIONS = "max_active_applications";

    private static final Map<String, String> DEFAULTS = Map.of(
            SITE_NAME, "Paws & Homes",
            CONTACT_EMAIL, "support@petadopt.com",
            ALLOW_REGISTRATION, "true",
            MAX_ACTIVE_APPLICATIONS, "3");

    private static final SettingService INSTANCE = new SettingService();

    private final SettingDAO settingDAO = new SettingDAOImpl();
    private volatile Map<String, String> cache;

    private SettingService() {
    }

    public static SettingService getInstance() {
        return INSTANCE;
    }

    public Map<String, String> all() {
        Map<String, String> snapshot = cache;
        if (snapshot == null) {
            synchronized (this) {
                if (cache == null) {
                    Map<String, String> merged = new LinkedHashMap<>(DEFAULTS);
                    merged.putAll(settingDAO.findAll());
                    cache = merged;
                }
                snapshot = cache;
            }
        }
        return snapshot;
    }

    public String get(String key) {
        return all().getOrDefault(key, DEFAULTS.get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULTS.get(key));
        }
    }

    public void update(String siteName, String contactEmail, String allowRegistration, String maxApplications) {
        int max = -1;
        try {
            max = Integer.parseInt(Validator.clean(maxApplications));
        } catch (NumberFormatException ignored) {
            // reported by the validator below
        }
        new Validator()
                .required(siteName, "Site name").maxLength(siteName, 60, "Site name")
                .required(contactEmail, "Contact email").email(contactEmail)
                .check(max >= 1 && max <= 20, "Maximum active applications must be a number between 1 and 20.")
                .throwIfInvalid();

        settingDAO.upsert(SITE_NAME, siteName.trim());
        settingDAO.upsert(CONTACT_EMAIL, contactEmail.trim());
        settingDAO.upsert(ALLOW_REGISTRATION, String.valueOf("true".equals(allowRegistration)));
        settingDAO.upsert(MAX_ACTIVE_APPLICATIONS, String.valueOf(max));
        cache = null; // force a reload on next read
    }
}
