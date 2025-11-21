package org.recnos.pg.model.dto.request.genai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PromptRequest {

    @NotBlank(message = "Prompt cannot be blank")
    private String prompt;
}
