package org.recnos.pg.model.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesUpdateRequest {

    private List<String> preferredLocations;

    private BigDecimal budgetMin;

    private BigDecimal budgetMax;

    private LocalDate moveInDate;
}
