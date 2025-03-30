package app;

import app.model.ReservationReporting;
import app.repository.ReservationRepository;
import app.service.ReportingService;
import app.web.dto.ReservationDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ReservationReportingITest {

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void clean() {
        reservationRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveReservation() {

        // Given
        UUID expectedReservationId = UUID.randomUUID();
        LocalDate expectedCheckIn = LocalDate.now();
        LocalDate expectedCheckOut = expectedCheckIn.plusDays(3);
        int expectedGuests = 2;
        long expectedLength = 3;
        BigDecimal expectedPrice = new BigDecimal("500.00");
        String expectedUser = "Petar123";
        String expectedApartment = "Apartment 1";

        ReservationDetails reservationDetails = ReservationDetails.builder()
                .reservationId(expectedReservationId)
                .checkInDate(expectedCheckIn)
                .checkOutDate(expectedCheckOut)
                .guests(expectedGuests)
                .reservationLength(expectedLength)
                .totalPrice(expectedPrice)
                .user(expectedUser)
                .apartment(expectedApartment)
                .build();

        // When
        ReservationReporting saved = reportingService.saveReservation(reservationDetails);
        List<ReservationReporting> history = reportingService.getReservationHistory();

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getReservationId()).isEqualTo(expectedReservationId);
        assertThat(saved.getCheckInDate()).isEqualTo(expectedCheckIn);
        assertThat(saved.getCheckOutDate()).isEqualTo(expectedCheckOut);
        assertThat(saved.getGuests()).isEqualTo(expectedGuests);
        assertThat(saved.getReservationLength()).isEqualTo(expectedLength);
        assertThat(saved.getTotalPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(saved.getUser()).isEqualTo(expectedUser);
        assertThat(saved.getApartment()).isEqualTo(expectedApartment);

        assertThat(history).hasSize(1);
        ReservationReporting historyReservationReporting = history.get(0);
        assertThat(historyReservationReporting.getId()).isEqualTo(saved.getId());
        assertThat(historyReservationReporting.getReservationId()).isEqualTo(expectedReservationId);
        assertThat(historyReservationReporting.getCheckInDate()).isEqualTo(expectedCheckIn);
        assertThat(historyReservationReporting.getCheckOutDate()).isEqualTo(expectedCheckOut);
        assertThat(historyReservationReporting.getGuests()).isEqualTo(expectedGuests);
        assertThat(historyReservationReporting.getReservationLength()).isEqualTo(expectedLength);
        assertThat(historyReservationReporting.getTotalPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(historyReservationReporting.getUser()).isEqualTo(expectedUser);
        assertThat(historyReservationReporting.getApartment()).isEqualTo(expectedApartment);
    }
}

