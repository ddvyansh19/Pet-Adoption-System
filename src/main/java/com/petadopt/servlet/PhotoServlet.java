package com.petadopt.servlet;

import com.petadopt.util.PhotoStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Serves uploaded pet photos from the upload folder (public, read-only). */
@WebServlet(name = "PhotoServlet", urlPatterns = "/photos/*")
public class PhotoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        Path file = (info == null || info.length() < 2) ? null : PhotoStorage.resolve(info.substring(1));
        if (file == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String mime = getServletContext().getMimeType(file.getFileName().toString());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(file, resp.getOutputStream());
    }
}
