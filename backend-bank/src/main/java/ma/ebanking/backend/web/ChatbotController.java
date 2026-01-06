package ma.ebanking.backend.web;

import lombok.AllArgsConstructor;
import ma.ebanking.backend.dto.ChatRequestDTO;
import ma.ebanking.backend.dto.ChatResponseDTO;
import ma.ebanking.backend.entities.ChatMessage;
import ma.ebanking.backend.repositories.ChatMessageRepository;
import ma.ebanking.backend.services.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/chatbot")
@AllArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> message(@RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatbotService.generateAnswer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> history(@RequestParam Long customerId) {
        List<ChatMessage> messages = chatMessageRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return ResponseEntity.ok(messages);
    }
}
