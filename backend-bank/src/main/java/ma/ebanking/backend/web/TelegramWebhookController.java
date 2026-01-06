package ma.ebanking.backend.web;

import lombok.AllArgsConstructor;
import ma.ebanking.backend.services.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TelegramWebhookController {

    private final ChatbotService chatbotService;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token:}")
    private String telegramBotToken;

    @Value("${telegram.webhook.secret:}")
    private String webhookSecret;

    @PostMapping("/api/telegram/webhook")
    public ResponseEntity<String> webhook(@RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secret,
                                          @RequestBody Map<String, Object> update) {
        // simple verification: if webhookSecret is set, require it
        if (webhookSecret != null && !webhookSecret.isBlank()) {
            if (secret == null || !secret.equals(webhookSecret)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
        }

        try {
            if (update.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) update.get("message");
                Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                Long chatId = ((Number) chat.get("id")).longValue();
                String text = (String) message.get("text");
                if (text == null) text = "";
                String answer = chatbotService.generateAnswerForTelegram(chatId, text);

                // send reply back to Telegram
                String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";
                Map<String, Object> payload = new HashMap<>();
                payload.put("chat_id", chatId);
                payload.put("text", answer);
                restTemplate.postForObject(url, payload, Object.class);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error");
        }
        return ResponseEntity.ok("ok");
    }
}
