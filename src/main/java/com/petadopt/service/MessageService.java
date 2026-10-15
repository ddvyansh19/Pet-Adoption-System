package com.petadopt.service;

import com.petadopt.dao.MessageDAO;
import com.petadopt.dao.MessageDAOImpl;
import com.petadopt.dao.UserDAO;
import com.petadopt.dao.UserDAOImpl;
import com.petadopt.exception.AccessDeniedException;
import com.petadopt.model.Message;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.util.Validator;
import java.util.Comparator;
import java.util.List;

/**
 * Shelters and adopters can only message each other when an adoption application links them,
 * which keeps the inbox free from spam.
 */
public class MessageService {
    private final MessageDAO messageDAO = new MessageDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    public List<User> contacts(User user) {
        List<User> list = (user.getRole() == Role.SHELTER)
                ? userDAO.findAdoptersForShelter(user.getId())
                : userDAO.findSheltersForAdopter(user.getId());
        list.sort(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public void send(User sender, long receiverId, String content) {
        new Validator().required(content, "Message").maxLength(content, 2000, "Message").throwIfInvalid();
        boolean allowed = contacts(sender).stream().anyMatch(u -> u.getId() == receiverId);
        if (!allowed) {
            throw new AccessDeniedException("You can only message people connected to one of your applications.");
        }
        Message m = new Message();
        m.setSenderId(sender.getId());
        m.setReceiverId(receiverId);
        m.setContent(content.trim());
        messageDAO.send(m);
    }

    public List<Message> inbox(long userId) {
        return messageDAO.findInbox(userId);
    }

    public List<Message> sent(long userId) {
        return messageDAO.findSent(userId);
    }

    public long unreadCount(long userId) {
        return messageDAO.countUnread(userId);
    }

    public void markAllRead(long userId) {
        messageDAO.markAllRead(userId);
    }
}
