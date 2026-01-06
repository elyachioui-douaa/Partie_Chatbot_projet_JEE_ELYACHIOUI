package ma.ebanking.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ebanking.backend.dto.ChatRequestDTO;
import ma.ebanking.backend.dto.ChatResponseDTO;
import ma.ebanking.backend.entities.ChatMessage;
import ma.ebanking.backend.entities.Customer;
import ma.ebanking.backend.repositories.AccountOperationRepository;
import ma.ebanking.backend.repositories.ChatMessageRepository;
import ma.ebanking.backend.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Value("${openai.api.key:}")
    private String openaiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String openaiModel;

    @Value("${telegram.bot.token:}")
    private String telegramBotToken;

    @Override
    public ChatResponseDTO generateAnswer(ChatRequestDTO request) {
        Long customerId = request.getCustomerId();
        String question = request.getQuestion();

        StringBuilder context = new StringBuilder();
        String sourceSummary = "";

        if (customerId != null) {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                context.append("Customer: ").append(customer.getName()).append(" (" + customer.getEmail() + ")\n");

                if (customer.getBankAccounts() != null && !customer.getBankAccounts().isEmpty()) {
                    context.append("Accounts:\n");
                    customer.getBankAccounts().forEach(acc -> {
                        context.append("- ").append(acc.getId()).append(" : balance=").append(acc.getBalance()).append("\n");
                        // fetch last 3 operations
                        List<String> ops = accountOperationRepository.findByBankAccountId(acc.getId()).stream()
                                .sorted(Comparator.comparing(o -> o.getOperationDate()))
                                .skip(Math.max(0, accountOperationRepository.findByBankAccountId(acc.getId()).size() - 3))
                                .map(op -> op.getOperationDate() + " " + op.getType() + " " + op.getAmount() + " " + op.getDescription())
                                .collect(Collectors.toList());
                        ops.forEach(o -> context.append("   * ").append(o).append("\n"));
                    });
                }
                sourceSummary = "Context: customer data and recent operations";
            }
        }

        String systemPrompt = "You are a secure, privacy-conscious banking assistant. Use only provided data. If the user asks for actions (transfer, debit, etc.) ask to use the app and do not perform operations. Avoid exposing PII in your answer.";
        String userPrompt = context.toString() + "\nQuestion: " + question;

        String answer = callOpenAI(systemPrompt, userPrompt);

        if (answer == null || answer.isBlank()) {
            answer = "Désolé, le service d'IA n'est pas disponible pour le moment.";
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setCustomerId(customerId);
        chatMessage.setUserMessage(question);
        chatMessage.setBotResponse(answer);
        chatMessage.setCreatedAt(new Date());
        chatMessageRepository.save(chatMessage);

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setAnswer(answer);
        responseDTO.setSourceSummary(sourceSummary);
        return responseDTO;
    }

    @Override
    public String generateAnswerForTelegram(Long chatId, String messageText) {
        ChatRequestDTO requestDTO = new ChatRequestDTO();
        requestDTO.setQuestion(messageText);
        ChatResponseDTO resp = generateAnswer(requestDTO);
        return resp.getAnswer();
    }

    private String callOpenAI(String systemPrompt, String userPrompt) {
        if (openaiKey == null || openaiKey.isBlank()) {
            log.warn("OpenAI API key not configured");
            return null;
        }

        String url = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiKey);

        Map<String, Object> messageSystem = new HashMap<>();
        messageSystem.put("role", "system");
        messageSystem.put("content", systemPrompt);
        Map<String, Object> messageUser = new HashMap<>();
        messageUser.put("role", "user");
        messageUser.put("content", userPrompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", openaiModel);
        body.put("messages", Arrays.asList(messageSystem, messageUser));
        body.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            JsonNode root = restTemplate.postForObject(url, entity, JsonNode.class);
            if (root != null && root.has("choices")) {
                JsonNode choices = root.get("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode first = choices.get(0);
                    JsonNode message = first.get("message");
                    if (message != null && message.has("content")) {
                        return message.get("content").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("OpenAI call failed", e);
        }
        return null;
    }
}
