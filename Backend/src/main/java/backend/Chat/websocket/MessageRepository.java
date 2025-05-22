package backend.Chat.websocket;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Soma Germano
 * message repositoru/ table in database
 */
public interface MessageRepository extends JpaRepository<Message, Long>{

}
