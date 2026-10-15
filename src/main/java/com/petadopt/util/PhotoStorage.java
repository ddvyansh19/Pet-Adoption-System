package com.petadopt.util;

import com.petadopt.exception.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.Part;

/** Saves, resolves and deletes uploaded pet photos on disk (outside the WAR). */
public final class PhotoStorage {
    private static final Map<String, String> TYPES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/gif", ".gif", "image/webp", ".webp");
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9._-]+");

    private PhotoStorage() {
    }

    /** @return stored file name, or null when the form had no file selected. */
    public static String store(Part part) {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String extension = TYPES.get(part.getContentType());
        if (extension == null) {
            throw new ValidationException("Photo must be a JPG, PNG, GIF or WebP image.");
        }
        String fileName = UUID.randomUUID() + extension;
        try {
            Path dir = AppConfig.getInstance().uploadDir();
            Files.createDirectories(dir);
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save the uploaded photo", e);
        }
        return fileName;
    }

    /** Returns the file for a stored name, or null if the name is unsafe or missing. */
    public static Path resolve(String fileName) {
        if (fileName == null || !SAFE_NAME.matcher(fileName).matches()) {
            return null;
        }
        Path path = AppConfig.getInstance().uploadDir().resolve(fileName);
        return Files.isRegularFile(path) ? path : null;
    }

    public static void delete(String fileName) {
        Path path = resolve(fileName);
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
                // an orphaned file is harmless
            }
        }
    }
}
