package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Getter
public class ReservationDetails {

    @NotNull
    private UUID reservationId;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @NotNull
    private int guests;

    @NotNull
    private long reservationLength;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private String user;

    @NotNull
    private String apartment;
}
