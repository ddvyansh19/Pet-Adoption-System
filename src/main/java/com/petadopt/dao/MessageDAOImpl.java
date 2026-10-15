package com.petadopt.dao;

import com.petadopt.model.Message;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MessageDAOImpl extends BaseDAO implements MessageDAO {
    private static final String SELECT = "SELECT m.*, s.name AS sender_name, r.name AS receiver_name "
            + "FROM messages m JOIN users s ON s.id = m.sender_id JOIN users r ON r.id = m.receiver_id ";

    private static Message map(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setSenderId(rs.getLong("sender_id"));
        m.setSenderName(rs.getString("sender_name"));
        m.setReceiverId(rs.getLong("receiver_id"));
        m.setReceiverName(rs.getString("receiver_name"));
        m.setContent(rs.getString("content"));
        m.setRead(rs.getBoolean("is_read"));
        m.setSentAt(rs.getTimestamp("sent_at"));
        return m;
    }

    @Override
    public long send(Message m) {
        return insert("INSERT INTO messages (sender_id, receiver_id, content) VALUES (?,?,?)", m.getSenderId(),
                m.getReceiverId(), m.getContent());
    }

    @Override
    public List<Message> findInbox(long userId) {
        return queryList(SELECT + "WHERE m.receiver_id = ? ORDER BY m.sent_at DESC, m.id DESC", MessageDAOImpl::map,
                userId);
    }

    @Override
    public List<Message> findSent(long userId) {
        return queryList(SELECT + "WHERE m.sender_id = ? ORDER BY m.sent_at DESC, m.id DESC", MessageDAOImpl::map,
                userId);
    }

    @Override
    public long countUnread(long userId) {
        return count("SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = 0", userId);
    }

    @Override
    public void markAllRead(long userId) {
        update("UPDATE messages SET is_read = 1 WHERE receiver_id = ? AND is_read = 0", userId);
    }
}
