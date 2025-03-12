package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
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
