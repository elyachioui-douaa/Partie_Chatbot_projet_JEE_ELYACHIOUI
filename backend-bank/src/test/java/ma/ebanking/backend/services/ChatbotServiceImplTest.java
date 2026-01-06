package ma.ebanking.backend.services;

import ma.ebanking.backend.dto.ChatRequestDTO;
import ma.ebanking.backend.dto.ChatResponseDTO;
import ma.ebanking.backend.repositories.AccountOperationRepository;
import ma.ebanking.backend.repositories.ChatMessageRepository;
import ma.ebanking.backend.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class ChatbotServiceImplTest {

    @Test
    public void whenOpenAiKeyMissing_thenFallbackMessageReturned() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        AccountOperationRepository accountOperationRepository = Mockito.mock(AccountOperationRepository.class);
        ChatMessageRepository chatMessageRepository = Mockito.mock(ChatMessageRepository.class);

        ChatbotServiceImpl service = new ChatbotServiceImpl(restTemplate, customerRepository, accountOperationRepository, chatMessageRepository);
        // leave openaiKey empty (default), call generateAnswer
        ChatRequestDTO req = new ChatRequestDTO();
        req.setQuestion("Quel est le solde ?");

        ChatResponseDTO resp = service.generateAnswer(req);
        assertNotNull(resp);
        assertTrue(resp.getAnswer().contains("Désolé") || resp.getAnswer().contains("n'est pas disponible"));
    }
}
