# Chatbot AI Integration (RAG)

This describes the new simple Chatbot AI backend integration (RAG-style) added to the project.

## Overview
- Endpoints:
  - POST /api/chatbot/message  => { "customerId": Long, "accountId": String, "question": String }
  - GET  /api/chatbot/history?customerId={id}
  - POST /api/telegram/webhook => Telegram webhook (optional secret header: `X-Telegram-Bot-Api-Secret-Token`)

- Behavior:
  - The ChatbotService gathers contextual data (customer profile, account balances, recent operations) if `customerId` is provided, builds a prompt and calls the OpenAI Chat Completion API.
  - All interactions are logged to `ChatMessage` table.
  - The Telegram webhook receives incoming messages and replies using the same bot logic.

## Configuration (add to `application.properties`)
```
openai.api.key=YOUR_OPENAI_KEY
openai.model=gpt-3.5-turbo
telegram.bot.token=YOUR_TELEGRAM_BOT_TOKEN
telegram.webhook.secret=OPTIONAL_SECRET
```

## Security
- Chat endpoints are protected by existing JWT resource server rules (same as the rest of the API).
- Telegram webhook endpoint `/api/telegram/webhook` is permitted (no JWT) but can be secured with the `telegram.webhook.secret` header.

## Usage examples
- Send a chat message (requires a customerId for contextual answers):
```
curl -X POST http://localhost:8085/api/chatbot/message \
  -H "Content-Type: application/json" \
  -d '{"customerId":1, "question":"Quel est le solde de mes comptes ?"}'
```

- Check chat history:
```
curl "http://localhost:8085/api/chatbot/history?customerId=1"
```

## Notes & Next steps
- This integration is intentionally minimal for demo/testing purposes. For production you should:
  - Use a vector database for robust RAG retrieval.
  - Sanitize and limit context to avoid leaking sensitive PII.
  - Add rate-limiting, retries, and circuit-breaker when calling the LLM.
  - Add tests and input validation.

