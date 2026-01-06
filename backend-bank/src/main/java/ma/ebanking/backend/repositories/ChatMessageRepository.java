package ma.ebanking.backend.repositories;

import ma.ebanking.backend.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
