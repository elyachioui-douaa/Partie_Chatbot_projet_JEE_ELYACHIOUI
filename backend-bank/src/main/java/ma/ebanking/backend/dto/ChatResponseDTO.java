package ma.ebanking.backend.dto;

import lombok.Data;

@Data
public class ChatResponseDTO {
    private String answer;
    private String sourceSummary; // short context summary used in the answer
}
