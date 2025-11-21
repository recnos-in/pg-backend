package org.recnos.pg.controller.genai;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.genai.PromptRequest;
import org.recnos.pg.model.dto.request.pg.PgCreateRequest;
import org.recnos.pg.service.genai.GenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/genai")
@RequiredArgsConstructor
@Tag(name = "GenAI Services", description = "APIs for Generative AI functionalities")
public class AIController {

    private final GenAIService genAIService;

    @PostMapping("/description")
    public ResponseEntity<String> generateDescription(@Valid @RequestBody PgCreateRequest pgCreateRequest) {
        String description = genAIService.generateDescription(pgCreateRequest );
        return ResponseEntity.ok(description);
    }

    @PostMapping("/prompt")
    public ResponseEntity<String> prompt(@Valid @RequestBody PromptRequest promptRequest) {
        String response = genAIService.prompt(promptRequest.getPrompt());
        return ResponseEntity.ok(response);
    }
}
