package com.campspot.interview.reservationsystem;

import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/campspot/reservations")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * API entry point for Campspot Gap Rule reservation system. Assumed that data is formatted exactly as the provided
     * sample JSON test file.
     *
     * @param reservationRequest Test data to run
     * @param gapSize Size of gap to verify against
     * @return List of campsites that have openings for the requested dates
     */
    // Stateless version
    @RequestMapping(method = RequestMethod.POST, value = "/reserve")
    public List<String> getCampsitesWithoutGap(@Valid @RequestBody ReservationRequest reservationRequest,
                                               @RequestParam(value = "gapSize", defaultValue = "1", required = false) int gapSize) {
        if (gapSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return reservationService.getCampsitesWithoutGap(reservationRequest, gapSize);
    }

    @RequestMapping(method = RequestMethod.GET, value = "findCampsitesWithoutGap")
    public ResponseEntity<List<String>> getCampsitesWithoutGap(@RequestParam(value = "startDate") Date startDate,
                                                               @RequestParam(value = "endDate") Date endDate,
                                                               @RequestParam(value = "gapSize", defaultValue = "1", required = false) int gapSize) {
        throw new NotImplementedException();
//        return new ResponseEntity<>(reservationService.getCampsitesWithoutGap(startDate, endDate, gapSize));
    }
}
