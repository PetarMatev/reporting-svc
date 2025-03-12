package app.web;

import app.model.ReservationReporting;
import app.service.ReportingService;
import app.web.dto.ReservationDetails;
import app.web.dto.ReservationResponse;
import app.web.dto.ReservationStatsResponse;
import app.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reporting")
public class ReportingController {

    private final ReportingService reportingService;

    @Autowired
    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/reservations")
    ResponseEntity<ReservationResponse> saveReservationDetails(@RequestBody ReservationDetails reservationDetails) {

        ReservationReporting reservation = reportingService.saveReservation(reservationDetails);

        ReservationResponse reservationResponse = DtoMapper.fromReservation(reservation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservationHistory() {

        List<ReservationResponse> reservationHistory = reportingService.getReservationHistory()
                .stream()
                .map(DtoMapper::fromReservation)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationHistory);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ReservationStatsResponse>> getSummaryStatsPerApartment() {

        List<ReservationStatsResponse> stats = reportingService.getStats();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stats);
    }

    @GetMapping("/query")
    public ResponseEntity<ReservationResponse> getReservationDetails(@RequestParam("reservationId") String reservationId) {
        UUID uuid = UUID.fromString(reservationId);  // Convert to UUID
        ReservationResponse reservationResponse = reportingService.getReservationDetails(uuid);
        return ResponseEntity.status(HttpStatus.OK).body(reservationResponse);
    }
}
