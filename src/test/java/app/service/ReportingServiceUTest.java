package app.service;

import app.ReservationNotFoundException;
import app.model.ReservationReporting;
import app.repository.ReservationRepository;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import app.web.dto.ReservationStatsResponse;
import app.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceUTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReportingService reportingService;


    // 1. saveReservation
    @Test
    void givenCorrectReservationDetails_thenReturnReservationReporting() {

        // Given
        ReservationDetails reservationDetails = ReservationDetails.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 3, 28))
                .checkOutDate(LocalDate.of(2025, 3, 30))
                .reservationLength(2)
                .totalPrice(BigDecimal.ONE)
                .user("Ivancho")
                .apartment("Apartment 1")
                .build();
        ReservationReporting reservationReporting = DtoMapper.fromReservationDetails(reservationDetails);
        when(reservationRepository.save(any(ReservationReporting.class))).thenReturn(reservationReporting);

        // When
        ReservationReporting returnedReservationReporting = reportingService.saveReservation(reservationDetails);

        // Then
        assertEquals(reservationDetails.getReservationId(), returnedReservationReporting.getReservationId());
        assertEquals(reservationDetails.getCheckInDate(), returnedReservationReporting.getCheckInDate());
        assertEquals(reservationDetails.getCheckOutDate(), returnedReservationReporting.getCheckOutDate());
        assertEquals(reservationDetails.getGuests(), returnedReservationReporting.getGuests());
        assertEquals(reservationDetails.getReservationLength(), returnedReservationReporting.getReservationLength());
        assertTrue(reservationDetails.getTotalPrice().compareTo(returnedReservationReporting.getTotalPrice()) == 0);
        assertEquals(reservationDetails.getUser(), returnedReservationReporting.getUser());
        assertEquals(reservationDetails.getApartment(), returnedReservationReporting.getApartment());
        verify(reservationRepository, times(1)).save(any(ReservationReporting.class));
    }

    // 2. getReservationHistory
    @Test
    void givenNoParametersProvided_thenReturnAListOfReservationReportingWhenGetReservationHistoryRequested() {

        // Given
        ReservationReporting reservationReporting1 = ReservationReporting.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 3, 28))
                .checkOutDate(LocalDate.of(2025, 3, 30))
                .guests(2)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(100))
                .user("User1")
                .apartment("Apartment 1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ReservationReporting reservationReporting2 = ReservationReporting.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 4, 1))
                .checkOutDate(LocalDate.of(2025, 4, 3))
                .guests(4)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(200))
                .user("User2")
                .apartment("Apartment 2")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(reservationRepository.findAll()).thenReturn(List.of(reservationReporting1, reservationReporting2));

        // When
        List<ReservationReporting> returnedReservationHistory = reportingService.getReservationHistory();

        // Then
        assertEquals(2, returnedReservationHistory.size());
        assertEquals(reservationReporting1.getReservationId(), returnedReservationHistory.get(0).getReservationId());
        assertEquals(reservationReporting2.getReservationId(), returnedReservationHistory.get(1).getReservationId());
        assertEquals(reservationReporting1.getUser(), returnedReservationHistory.get(0).getUser());
        assertEquals(reservationReporting2.getUser(), returnedReservationHistory.get(1).getUser());
        verify(reservationRepository, times(1)).findAll();
    }

    // 3. getReservationDetails
    @Test
    void givenReservationIdThatIsCorrectInTheDatabase_thenReturnReservationResponse() {

        // Given
        UUID reservationId = UUID.randomUUID();
        ReservationReporting reservationReporting = ReservationReporting.builder()
                .reservationId(reservationId)
                .checkInDate(LocalDate.of(2025, 3, 28))
                .checkOutDate(LocalDate.of(2025, 3, 30))
                .guests(3)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(150))
                .user("Ivancho")
                .apartment("Apartment 1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        when(reservationRepository.findByReservationId(reservationId)).thenReturn(reservationReporting);
        ReservationResponse expectedReservationResponse = DtoMapper.fromReservation(reservationReporting);

        // When
        ReservationResponse returnedReservationDetails = reportingService.getReservationDetails(reservationId);

        // Then
        assertEquals(expectedReservationResponse.getReservationId(), returnedReservationDetails.getReservationId());
        assertEquals(expectedReservationResponse.getCheckInDate(), returnedReservationDetails.getCheckInDate());
        assertEquals(expectedReservationResponse.getCheckOutDate(), returnedReservationDetails.getCheckOutDate());
        assertEquals(expectedReservationResponse.getGuests(), returnedReservationDetails.getGuests());
        assertEquals(expectedReservationResponse.getReservationLength(), returnedReservationDetails.getReservationLength());
        assertEquals(expectedReservationResponse.getTotalPrice(), returnedReservationDetails.getTotalPrice());
        assertEquals(expectedReservationResponse.getUser(), returnedReservationDetails.getUser());
        assertEquals(expectedReservationResponse.getApartment(), returnedReservationDetails.getApartment());
        verify(reservationRepository, times(1)).findByReservationId(reservationId);
    }

    @Test
    void givenNonExistentReservationId_thenThrowReservationNotFoundException() {

        // Given
        UUID nonExistentReservationId = UUID.randomUUID();
        when(reservationRepository.findByReservationId(nonExistentReservationId)).thenReturn(null);

        // When/Then
        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> reportingService.getReservationDetails(nonExistentReservationId)
        );

        assertEquals(
                "Reservation not found with ID: " + nonExistentReservationId,
                exception.getMessage()
        );
        verify(reservationRepository, times(1)).findByReservationId(nonExistentReservationId);
    }

    // 4. getStats
    @Test
    void givenReservationHistory_thenReturnStatsForEachApartment() {
        // Given
        ReservationReporting reservation1 = ReservationReporting.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 3, 28))
                .checkOutDate(LocalDate.of(2025, 3, 30))
                .guests(3)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(150))
                .user("Ivancho")
                .apartment("Apartment 1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ReservationReporting reservation2 = ReservationReporting.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 3, 29))
                .checkOutDate(LocalDate.of(2025, 3, 31))
                .guests(2)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(200))
                .user("Maria")
                .apartment("Apartment 1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ReservationReporting reservation3 = ReservationReporting.builder()
                .reservationId(UUID.randomUUID())
                .checkInDate(LocalDate.of(2025, 3, 28))
                .checkOutDate(LocalDate.of(2025, 3, 30))
                .guests(4)
                .reservationLength(2)
                .totalPrice(BigDecimal.valueOf(100))
                .user("Dimitar")
                .apartment("Apartment 2")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        // Mock repository to return the mock data
        when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2, reservation3));

        // Expected statistics for each apartment
        List<ReservationStatsResponse> expectedStats = List.of(
                ReservationStatsResponse.builder()
                        .apartment("Apartment 1")
                        .totalRevenue(BigDecimal.valueOf(350))                        .totalBookedDays("4")  // 2 + 2
                        .totalGuestsVisited("5") // 3 + 2
                        .build(),
                ReservationStatsResponse.builder()
                        .apartment("Apartment 2")
                        .totalRevenue(BigDecimal.valueOf(100))                        .totalBookedDays("2")
                        .totalGuestsVisited("4")
                        .build()
        );

        // When
        List<ReservationStatsResponse> returnedStats = reportingService.getStats();

        // Then
        // Verify the statistics for each apartment
        assertEquals(expectedStats.size(), returnedStats.size());
        // Verify the repository's findAll method was called once
        verify(reservationRepository, times(1)).findAll();
    }
}
