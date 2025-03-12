package app.web.mapper;

import app.model.ReservationReporting;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DtoMapper {



    // Building DTO from Entity
    public static ReservationResponse fromReservation(ReservationReporting reservationReporting) {

        return ReservationResponse.builder()
                .reservationId(reservationReporting.getReservationId())
                .checkInDate(reservationReporting.getCheckInDate())
                .checkOutDate(reservationReporting.getCheckOutDate())
                .guests(reservationReporting.getGuests())
                .reservationLength(reservationReporting.getReservationLength())
                .totalPrice(reservationReporting.getTotalPrice())
                .user(reservationReporting.getUser())
                .apartment(reservationReporting.getApartment())
                .build();
    }

    // Building Entity from DTO

    public static ReservationReporting fromReservationDetails(ReservationDetails reservationDetails) {
        return ReservationReporting.builder()
                .reservationId(reservationDetails.getReservationId())
                .checkInDate(reservationDetails.getCheckInDate())
                .checkOutDate(reservationDetails.getCheckOutDate())
                .guests(reservationDetails.getGuests())
                .reservationLength(reservationDetails.getReservationLength())
                .totalPrice(reservationDetails.getTotalPrice())
                .user(reservationDetails.getUser())
                .apartment(reservationDetails.getApartment())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
