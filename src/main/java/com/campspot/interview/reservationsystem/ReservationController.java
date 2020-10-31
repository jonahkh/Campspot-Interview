package com.campspot.interview.reservationsystem;

import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/campspot/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Stateless version
    @RequestMapping(method = RequestMethod.POST, value = "/reserve")
    public List<String> getCampsitesWithoutGap(@RequestBody ReservationRequest reservationRequest,
                                                               @RequestParam(value = "gapSize", defaultValue = "1", required = false) int gapSize) {
        return reservationService.getCampsitesWithoutGap(reservationRequest, gapSize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "findCampsitesWithoutGap")
    public ResponseEntity<List<String>> getCampsitesWithoutGap(@RequestParam(value = "startDate") Date startDate,
                                                               @RequestParam(value = "endDate") Date endDate,
                                                               @RequestParam(value = "gapSize", defaultValue = "1", required = false) int gapSize) {
        return null;
    }
}
