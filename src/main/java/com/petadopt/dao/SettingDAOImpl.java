package com.petadopt.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettingDAOImpl extends BaseDAO implements SettingDAO {

    @Override
    public Map<String, String> findAll() {
        Map<String, String> settings = new LinkedHashMap<>();
        queryList("SELECT setting_key, setting_value FROM settings ORDER BY setting_key",
                rs -> settings.put(rs.getString(1), rs.getString(2)));
        return settings;
    }

    @Override
    public void upsert(String key, String value) {
        update("INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)", key, value);
    }
}
