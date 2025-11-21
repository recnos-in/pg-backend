package org.recnos.pg.service.genai;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.recnos.pg.config.OpenAIConfig;
import org.recnos.pg.model.dto.request.pg.PgCreateRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenAIService {

    private final RestTemplate restTemplate;
    private final OpenAIConfig openAIConfig;

    private static final String CHAT_COMPLETIONS_ENDPOINT = "/chat/completions";

    public String generateDescription(@Valid PgCreateRequest pgCreateRequest) {
        String prompt = buildDescriptionPrompt(pgCreateRequest);
        return callOpenAI(prompt);
    }

    public String prompt(String prompt) {
        return callOpenAI(prompt);
    }

    private String callOpenAI(String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAIConfig.getApiKey());

            LLMRequest request = LLMRequest.builder()
                    .model(openAIConfig.getModel())
                    .messages(List.of(
                            LLMRequest.Message.builder()
                                    .role("system")
                                    .content("You are a helpful assistant that writes engaging property descriptions for paying guest accommodations. Write in a professional yet warm tone.")
                                    .build(),
                            LLMRequest.Message.builder()
                                    .role("user")
                                    .content(userPrompt)
                                    .build()
                    ))
                    .maxTokens(500)
                    .temperature(0.7)
                    .build();

            HttpEntity<LLMRequest> entity = new HttpEntity<>(request, headers);

            String url = openAIConfig.getBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
            LLMResponse response = restTemplate.postForObject(url, entity, LLMResponse.class);

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }

            log.warn("Empty response from OpenAI");
            return "Unable to generate description at this time.";

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return "Error generating description: " + e.getMessage();
        }
    }

    private String buildDescriptionPrompt(PgCreateRequest pg) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write an engaging description for a paying guest accommodation with the following details:\n\n");

        prompt.append("Name: ").append(pg.getName()).append("\n");
        prompt.append("Location: ").append(pg.getAddress()).append(", ").append(pg.getCity())
                .append(", ").append(pg.getState()).append(" - ").append(pg.getPincode()).append("\n");

        if (pg.getLandmark() != null) {
            prompt.append("Landmark: ").append(pg.getLandmark()).append("\n");
        }

        if (pg.getPropertyType() != null) {
            prompt.append("Property Type: ").append(pg.getPropertyType()).append("\n");
        }

        if (pg.getGenderType() != null) {
            prompt.append("For: ").append(pg.getGenderType()).append("\n");
        }

        if (pg.getOccupancyType() != null) {
            prompt.append("Occupancy: ").append(pg.getOccupancyType()).append("\n");
        }

        if (pg.getFurnishingType() != null) {
            prompt.append("Furnishing: ").append(pg.getFurnishingType()).append("\n");
        }

        if (pg.getTotalRooms() != null) {
            prompt.append("Total Rooms: ").append(pg.getTotalRooms()).append("\n");
        }

        if (pg.getTotalFloors() != null) {
            prompt.append("Total Floors: ").append(pg.getTotalFloors()).append("\n");
        }

        if (pg.getFoodAvailable() != null && pg.getFoodAvailable()) {
            prompt.append("Food: Available");
            if (pg.getFoodType() != null) {
                prompt.append(" (").append(pg.getFoodType()).append(")");
            }
            prompt.append("\n");
        }

        if (pg.getSecurityDeposit() != null) {
            prompt.append("Security Deposit: Rs. ").append(pg.getSecurityDeposit()).append("\n");
        }

        prompt.append("\nWrite a 2-3 paragraph description that highlights the key features and ");
        prompt.append("makes the property appealing to potential tenants. Focus on comfort, ");
        prompt.append("convenience, and the benefits of staying at this PG.");

        return prompt.toString();
    }
}