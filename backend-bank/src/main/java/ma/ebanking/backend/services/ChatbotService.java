package ma.ebanking.backend.services;

import ma.ebanking.backend.dto.ChatRequestDTO;
import ma.ebanking.backend.dto.ChatResponseDTO;

public interface ChatbotService {
    ChatResponseDTO generateAnswer(ChatRequestDTO request);
    String generateAnswerForTelegram(Long chatId, String messageText);
}
