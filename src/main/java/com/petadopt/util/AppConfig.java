package com.petadopt.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Singleton that loads db.properties once. Environment variables override file values. */
public final class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties props = new Properties();

    private AppConfig() {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read db.properties", e);
        }
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    private String get(String envName, String key, String fallback) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env;
        }
        String value = props.getProperty(key);
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    public String dbUrl() {
        return get("DB_URL", "db.url", "jdbc:mysql://localhost:3306/pet_adoption");
    }

    public String dbUser() {
        return get("DB_USER", "db.user", "root");
    }

    public String dbPassword() {
        return get("DB_PASSWORD", "db.password", "");
    }

    public Path uploadDir() {
        String dir = get("UPLOAD_DIR", "upload.dir", null);
        return dir != null ? Paths.get(dir) : Paths.get(System.getProperty("user.home"), "petadopt-uploads");
    }
}
