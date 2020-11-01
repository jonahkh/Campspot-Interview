package com.campspot.interview.reservationsystem.service.impl;

import com.campspot.interview.reservationsystem.dao.CampsiteDao;
import com.campspot.interview.reservationsystem.dao.ReservationDao;
import com.campspot.interview.reservationsystem.models.Campsite;
import com.campspot.interview.reservationsystem.models.Reservation;
import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.models.Search;
import com.campspot.interview.reservationsystem.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationServiceImpl implements ReservationService  {
    private static final int MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;

    private CampsiteDao campsiteDao;
    private ReservationDao reservationDao;

    /**
     * Returns a list of campsite names that can be reserved for the given dates requested when all current campsite
     * reservations are to be provided in the request. Campsites are considered  reservable if they are not currently
     * reserved and the number of days between reservations is not less than the expected gap size.
     *
     * @param reservationRequest A request given in the format of the test-case.json file and sent via REST
     * @param gapSize The size of the gap to validate against between existing reservations
     * @return list of campsite names that can be reserved
     */
    @Override
    public List<String> getCampsitesWithoutGap(ReservationRequest reservationRequest, int gapSize) {
        return filterCampsites(gapSize, reservationRequest.getCampsites(), reservationRequest.getSearch(), reservationRequest.getReservations());
    }

    /**
     * Returns a list of campsite names that can be reserved for the given dates requested. Campsites are considered
     * reservable if they are not currently reserved and the number of days between reservations is not less than the
     * expected gap size.
     *
     * This method assumes that current campsite reservations are not sent with the request but are validated against
     * some external data source
     *
     * @param startDate date the requested reservation would start
     * @param endDate date the requested reservation would end
     * @param gapSize The size of the gap to validate against between existing reservations
     * @return list of campsite names that can be reserved
     */
    @Override
    public List<String> getCampsitesWithoutGap(Date startDate, Date endDate, int gapSize) {
        final List<Reservation> reservations = reservationDao.getReservations();
        final List<Campsite> campsites = campsiteDao.getCampsites();
        final Search currentSearch = new Search(startDate, endDate);

        return filterCampsites(gapSize, campsites, currentSearch, reservations);
    }

    private List<String> filterCampsites(int gapSize, List<Campsite> campsites, Search currentSearch, List<Reservation> reservations) {
        Map<String, SortedSet<Reservation>> currentCampsiteReservations = getCurrentCampsiteReservations(reservations);
        List<String> matchingCampsites = new ArrayList<>();

        for (Campsite campsite : campsites) {
            if (!currentCampsiteReservations.containsKey(campsite.getId())) { // Check if any reservations exist for campsite first
                matchingCampsites.add(campsite.getName());
            } else {
                checkReservations(gapSize, currentCampsiteReservations, currentSearch, matchingCampsites, campsite);
            }
        }
        return matchingCampsites;
    }

    private void checkReservations(int gapSize, Map<String, SortedSet<Reservation>> currentCampsiteReservations, Search currentSearch, List<String> matchingCampsites, Campsite campsite) {
        final ArrayList<Reservation> reservations = new ArrayList<>(currentCampsiteReservations.get(campsite.getId()));

        // If we find an overlap, we don't need to continue evaluating reservations for this specific campsite
        boolean hasRoom = true;

        for (int i = 0; i < reservations.size() && hasRoom; i++) {
            Reservation reservation = reservations.get(i);
            final boolean hasNext = i + 1 < reservations.size();
            // "Gap Rule" rules
            if ((currentSearch.getStartDate().after(reservation.getEndDate()) // Is the search start date after this reservation's end date?
                    // Check next reservation
                    && (hasNext || reservations.get(i).getStartDate().after(currentSearch.getEndDate()))

                    // Check gap from current reservation's end date to the search start date
                    && (validateGapSize(currentSearch.getStartDate(), reservation.getEndDate(), gapSize))

                    // Check gap from the search end date to the next reservation's start date if present
                    && (hasNext && validateGapSize(currentSearch.getEndDate(), reservations.get(i + 1).getStartDate(), gapSize)))

                    // The search date is before the current date, handles case of first reservation in list is after this search
                    || (currentSearch.getEndDate().before(reservation.getStartDate())
                        && validateGapSize(currentSearch.getEndDate(), reservation.getStartDate(), gapSize))

                    // The search date is after the current date, handles case of first reservation in list is before this search
                    || (currentSearch.getStartDate().after(reservation.getEndDate())
                        && validateGapSize(currentSearch.getStartDate(), reservation.getEndDate(), gapSize))) {

                System.out.println("Reservation: " + reservation.toString() + " Does not have a gap "
                        + gapSize + " days or less for search: " + currentSearch.toString());
            } else {
                System.err.println("Reservation: " + reservation.toString() + "Has a gap "
                        + gapSize + " days or less for search: " + currentSearch.toString());
                hasRoom = false;
            }
        }

        if (hasRoom) {
            System.out.println("Reservation: " + reservations.toString() + " can be booked for campsite " + campsite.toString());
            matchingCampsites.add(campsite.getName());
        }
    }

    // Maps reservations from campsiteId (to join with Campsite.campsiteId) to a sorted collection of reservations by start date
    // We want it sorted in order to implement optimizations such as the ability to stop iterating over the reservations
    // if the reservation start date is greater than the search end date
    private Map<String, SortedSet<Reservation>> getCurrentCampsiteReservations(List<Reservation> reservations) {
        Map<String, SortedSet<Reservation>> currentCampsiteReservations = new HashMap<>();
        for (Reservation reservation : reservations) {
            if (!currentCampsiteReservations.containsKey(reservation.getCampsiteId())) {
                currentCampsiteReservations.put(reservation.getCampsiteId(), new TreeSet<>());
            }
            currentCampsiteReservations.get(reservation.getCampsiteId()).add(reservation);
        }
        return currentCampsiteReservations;
    }

    // If the gap is the size of the requested gapSize, then the dates would line up i.e. 06/04 search end, 06/05 reservation start
    // This would lead to gap == 1 which equals gapSize.
    // If search is 06/04 and reservation start is 06/06, the gap is now 2 which is the gap we're avoiding when gapSize=1
    // If search is 06/04 and reservation start is 06/07, the gap is now 3 which translates to two empty days between reservations.
    // With gapSize=1, this is larger than the prohibited gap size so we pass this case
    private boolean validateGapSize(Date date1, Date date2, int gapSize) {
        final int gap = getGap(date1, date2);
        return gap == gapSize || gap > gapSize + 1;
    }

    // Calculate the number of days between two dates
    private int getGap(Date date1, Date date2) {
        return (int) Math.abs((date1.getTime() - date2.getTime()) / MILLIS_TO_DAYS);
    }
}
