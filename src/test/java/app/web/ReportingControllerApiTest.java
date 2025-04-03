package app.web;

import app.model.ReservationReporting;
import app.repository.ReservationRepository;
import app.service.ReportingService;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import app.web.dto.ReservationStatsResponse;
import app.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.web.TestBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReportingController.class)
public class ReportingControllerApiTest {

    @MockitoBean
    private ReportingService reportingService;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @Autowired
    private MockMvc mockMvc;

    // 1. saveReservationDetails

    @Test
    void saveReservationDetails_shouldReturn201WithCreatedReservation() throws Exception {

        // Given
        ReservationDetails request = getReservationDetails();
        ReservationReporting savedReservation = getReservationReporting();
        ReservationResponse expectedResponse = DtoMapper.fromReservation(savedReservation);
        when(reportingService.saveReservation(any(ReservationDetails.class)))
                .thenReturn(savedReservation);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/reporting/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "user": "Ivancho",
                    "apartment": "Apartment 1",
                    "checkInDate": "2025-04-01",
                    "checkOutDate": "2025-04-05",
                    "guests": 3,
                    "totalPrice": 500.00
                }
                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user").value("Ivancho"))
                .andExpect(jsonPath("$.apartment").value("Apartment 1"))
                .andExpect(jsonPath("$.guests").value(3))
                .andExpect(jsonPath("$.reservationLength").value(2))
                .andExpect(jsonPath("$.totalPrice").value(500.00));
    }

    @Test
    void saveReservationDetails_withInvalidInput_shouldReturn400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/reporting/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "user": "",
                    "apartment": "",
                    "checkInDate": "invalid-date",
                    "checkOutDate": "invalid-date",
                    "guests": -1,
                    "totalPrice": -100
                }
                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    // 2. getReservationDetails
    @Test
    void getReservationDetails_withValidId_shouldReturnIsOK() throws Exception {

        // 1. Build Request
        UUID reservationId = UUID.randomUUID();
        ReservationResponse mockResponse = ReservationResponse.builder()
                .user("Ivancho")
                .apartment("Apartment 1")
                .totalPrice(new BigDecimal("500.00"))
                .build();
        when(reportingService.getReservationDetails(reservationId))
                .thenReturn(mockResponse);

        // 2. Send Request
        mockMvc.perform(get("/api/v1/admin/reporting/query")
                        .param("reservationId", reservationId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("Ivancho"))
                .andExpect(jsonPath("$.apartment").value("Apartment 1"))
                .andExpect(jsonPath("$.totalPrice").value(500.00));
    }

    // 3. getReservationHistory
    @Test
    void getReservationHistory_shouldReturn200WithHistory() throws Exception {

        // 1. Build Request
        ReservationReporting reservationReportingOne = getReservationReporting();
        ReservationReporting reservationReportingTwo = getReservationReporting();
        when(reportingService.getReservationHistory()).thenReturn(
                List.of(reservationReportingOne, reservationReportingTwo));

        // 2. Send Request
        mockMvc.perform(get("/api/v1/admin/reporting"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].user").value("Ivancho"))
                .andExpect(jsonPath("$[0].apartment").value("Apartment 1"))
                .andExpect(jsonPath("$[1].guests").value(3))
                .andExpect(jsonPath("$[1].reservationLength").value(2));
    }

    // 4. getSummaryStatsPerApartment
    @Test
    void getSummaryStatsPerApartment_shouldReturn200WithStats() throws Exception {

        // 1. Build Request
        ReservationStatsResponse stat1 = getReservationStatsResponse();
        ReservationStatsResponse stat2 = getReservationStatsResponse();
        List<ReservationStatsResponse> mockStats = List.of(stat1, stat2);

        when(reportingService.getStats()).thenReturn(mockStats);

        // 2. Send Request
        mockMvc.perform(get("/api/v1/admin/reporting/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].apartment").value("Apartment 1"))
                .andExpect(jsonPath("$[0].totalRevenue").value(5000.00))
                .andExpect(jsonPath("$[0].totalBookedDays").value("30"))
                .andExpect(jsonPath("$[0].totalGuestsVisited").value("45"));

    }

    // 6. getReservationDetails
    @Test
    void getReservationDetails_withValidId_shouldReturn200() throws Exception {

        // 1. Build Request
        UUID reservationID = UUID.randomUUID();
        ReservationResponse mockResponse = getReservationResponse();
        when(reportingService.getReservationDetails(any(UUID.class))).thenReturn(mockResponse);

        // Send Request
        mockMvc.perform(get("/api/v1/admin/reporting/query")
                        .param("reservationId", reservationID.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.guests").value(3))
                .andExpect(jsonPath("$.reservationLength").value(2))
                .andExpect(jsonPath("$.totalPrice").value(1))
                .andExpect(jsonPath("$.user").value("Ivancho"))
                .andExpect(jsonPath("$.apartment").value("apartment 2"));
    }
}
