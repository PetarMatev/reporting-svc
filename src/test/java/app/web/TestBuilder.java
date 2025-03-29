package app.web;

import app.model.ReservationReporting;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import app.web.dto.ReservationStatsResponse;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static ReservationDetails getReservationDetails() {
        return ReservationDetails.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(3))
                .guests(2)
                .reservationLength(3)
                .totalPrice(new BigDecimal("500.00"))
                .user("John Doe")
                .apartment("Deluxe Suite")
                .build();
    }

    public static ReservationResponse getReservationResponse() {

        return ReservationResponse.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .guests(3)
                .reservationLength(2)
                .totalPrice(BigDecimal.ONE)
                .user("Ivancho")
                .apartment("apartment 2")
                .build();
    }

    public static ReservationStatsResponse getReservationStatsResponse() {
        return ReservationStatsResponse.builder()
                .apartment("Apartment 1")
                .totalRevenue(new BigDecimal("5000.00"))
                .totalBookedDays("30")
                .totalGuestsVisited("45")
                .build();
    }

    public static ReservationReporting getReservationReporting() {
        return ReservationReporting.builder()
                .id(UUID.randomUUID())
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .guests(3)
                .reservationLength(2)
                .totalPrice(new BigDecimal("500.00"))
                .user("Ivancho")
                .apartment("Apartment 1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}



