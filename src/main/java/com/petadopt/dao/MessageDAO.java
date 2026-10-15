package com.petadopt.dao;

import com.petadopt.model.Message;
import java.util.List;

public interface MessageDAO {
    long send(Message message);

    List<Message> findInbox(long userId);

    List<Message> findSent(long userId);

    long countUnread(long userId);

    void markAllRead(long userId);
}
