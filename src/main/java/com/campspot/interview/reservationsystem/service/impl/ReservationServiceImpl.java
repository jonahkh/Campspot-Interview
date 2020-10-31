package com.campspot.interview.reservationsystem.service.impl;

import com.campspot.interview.reservationsystem.models.Campsite;
import com.campspot.interview.reservationsystem.models.Reservation;
import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.models.Search;
import com.campspot.interview.reservationsystem.service.ReservationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class ReservationServiceImpl implements ReservationService  {

    private static final int MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;

    @Override
    public List<String> getCampsitesWithoutGap(ReservationRequest reservationRequest, int gapSize) {
        // pre-process,
        Map<String, SortedSet<Reservation>> currentCampsiteReservations = getCurrentCampsiteReservations(reservationRequest);

        final Search currentSearch = reservationRequest.getSearch();
        List<String> matchingCampsites = new ArrayList<>();

        for (Campsite campsite : reservationRequest.getCampsites()) {
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

        boolean hasRoom = true;
        // TODO break if the reservations are past the search
        for (int i = 0; i < reservations.size() && hasRoom; i++) {
            Reservation reservation = reservations.get(i);
            final boolean hasNext = i + 1 < reservations.size();
            if ((currentSearch.getStartDate().after(reservation.getEndDate()) // Is the search start date after this reservation's end date?
                    && (hasNext || reservations.get(i).getStartDate().after(currentSearch.getEndDate())) // Check next reservation
                    && (validateGapSize(currentSearch.getStartDate(), reservation.getEndDate(), gapSize)) // Check gap from current reservation's end date to the search start date
                    && (hasNext && validateGapSize(currentSearch.getEndDate(), reservations.get(i + 1).getStartDate(), gapSize))) // Check gap from the search end date to the next reservation's start date if present
                    // The search date is before the current date, handles case of first reservation in list is after this search
                    || (currentSearch.getEndDate().before(reservation.getStartDate())
                    && validateGapSize(currentSearch.getEndDate(), reservation.getStartDate(), gapSize))) {
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
    private Map<String, SortedSet<Reservation>> getCurrentCampsiteReservations(ReservationRequest reservationRequest) {
        Map<String, SortedSet<Reservation>> currentCampsiteReservations = new HashMap<>();
        for (Reservation reservation : reservationRequest.getReservations()) {
            if (!currentCampsiteReservations.containsKey(reservation.getCampsiteId())) {
                currentCampsiteReservations.put(reservation.getCampsiteId(), new TreeSet<>());
            }
            currentCampsiteReservations.get(reservation.getCampsiteId()).add(reservation);
        }
        return currentCampsiteReservations;
    }

    private boolean validateGapSize(Date date1, Date date2, int gapSize) {
        final int gap = getGap(date1, date2);
        return gap == gapSize || gap > gapSize + 1;
    }

    @Override
    public List<String> getCampsitesWithoutGap(Date startDate, Date endDate, int gapSize) {
        return null;
    }

    private int getGap(Date date1, Date date2) {
        return (int) Math.abs((date1.getTime() - date2.getTime()) / MILLIS_TO_DAYS);
    }
}
