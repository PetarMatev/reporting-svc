package app.service;

import app.model.ReservationReporting;
import app.repository.ReservationRepository;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import app.web.dto.ReservationStatsResponse;
import app.web.dto.Summary;
import app.web.mapper.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportingService {
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReportingService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationReporting saveReservation(ReservationDetails reservationDetails) {

        ReservationReporting reservation = DtoMapper.fromReservationDetails(reservationDetails);

        return reservationRepository.save(reservation);
    }

    public List<ReservationReporting> getReservationHistory() {
        return reservationRepository.findAll();
    }

    public ReservationResponse getReservationDetails(UUID reservationId) {

        ReservationReporting reservationReporting = reservationRepository.findByReservationId(reservationId);
        return DtoMapper.fromReservation(reservationReporting);
    }

    public List<ReservationStatsResponse> getStats() {

        Map<String, Summary> reservationsSummary = getReservationHistory()
                .stream()
                .collect(Collectors.groupingBy(
                        ReservationReporting::getApartment,
                        Collectors.reducing(
                                new Summary(BigDecimal.ZERO, 0, 0),
                                r -> new Summary(r.getTotalPrice(), (int) r.getReservationLength(), r.getGuests()),
                                Summary::combine
                        )
                ));

        return fromSummaryMap(reservationsSummary);
    }

    public static List<ReservationStatsResponse> fromSummaryMap(Map<String, Summary> summaryMap) {
        return summaryMap.entrySet().stream()
                .map(entry -> ReservationStatsResponse.builder()
                        .apartment(entry.getKey())
                        .totalRevenue(entry.getValue().getTotalRevenue()) // Convert BigDecimal to String
                        .totalBookedDays(String.valueOf(entry.getValue().getTotalBookedDays()))
                        .totalGuestsVisited(String.valueOf(entry.getValue().getTotalGuestsVisited()))
                        .build())
                .collect(Collectors.toList());
    }
}
