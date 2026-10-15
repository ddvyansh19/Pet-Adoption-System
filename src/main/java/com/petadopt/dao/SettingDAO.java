package com.petadopt.dao;

import java.util.Map;

public interface SettingDAO {
    Map<String, String> findAll();

    /** Inserts the setting or updates it if the key already exists. */
    void upsert(String key, String value);
}
