package ma.ebanking.backend.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private Long customerId; // optional - recommended to scope the answer
    private String accountId; // optional - to focus on a specific account
    private String question;
}
