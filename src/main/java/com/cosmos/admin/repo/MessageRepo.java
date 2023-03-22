package com.cosmos.admin.repo;

import com.cosmos.admin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query(value = "SELECT m FROM Message m WHERE m.type = ?1")
    List<Message> selectMessageByType(String messageType);

    @Query(value = "SELECT m FROM Message m WHERE m.type = ?1 AND m.sendMessage = true")
    List<Message> selectMessageByTypeAndSendMessage(String messageType);

    @Query(nativeQuery = true, value = "SELECT m.message_id AS id, m.updated_at AS date, m.msg_text AS message FROM tbl_message m where m.msg_type=?1 AND m.send_msg=true")
    List<Map<String, Object>> fetchMessageByTypeAndSendMessage(String messageType);

    @Query(value = "SELECT * FROM tbl_message m WHERE m.type =\"user_guide\" order by created_at desc limit 0,1", nativeQuery = true)
    Message selectMessageByUserGuide();

    @Modifying
    @Query(value = "DELETE FROM Message m WHERE m.messageId = ?1")
    void deleteById(Long id);
}
