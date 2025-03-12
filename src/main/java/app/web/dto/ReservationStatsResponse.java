package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReservationStatsResponse {

    private String apartment;

    private BigDecimal totalRevenue;

    private String totalBookedDays;

    private String totalGuestsVisited;
}
