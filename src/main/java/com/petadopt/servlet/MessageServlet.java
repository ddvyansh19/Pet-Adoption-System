package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.User;
import com.petadopt.service.MessageService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Messaging between shelters and adopters. Both roles use the same servlet and view. */
@WebServlet(name = "MessageServlet", urlPatterns = {"/shelter/messages", "/adopter/messages"})
public class MessageServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient MessageService messageService = new MessageService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        boolean sentBox = "sent".equals(param(req, "box"));
        req.setAttribute("box", sentBox ? "sent" : "inbox");
        req.setAttribute("basePath", req.getServletPath());
        req.setAttribute("contacts", messageService.contacts(me));
        // The list is loaded first so unread messages are still highlighted on this one view;
        // marking them read afterwards also keeps the unread badge in the header accurate.
        req.setAttribute("messages", sentBox ? messageService.sent(me.getId()) : messageService.inbox(me.getId()));
        if (!sentBox) {
            messageService.markAllRead(me.getId());
        }
        view(req, resp, "common/messages.jsp", "Messages");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        try {
            messageService.send(me, idParam(req, "receiverId"), req.getParameter("content"));
            flashSuccess(req, "Message sent successfully.");
        } catch (AppException e) {
            flashError(req, e);
        }
        redirect(req, resp, req.getServletPath() + "?box=sent");
    }
}
