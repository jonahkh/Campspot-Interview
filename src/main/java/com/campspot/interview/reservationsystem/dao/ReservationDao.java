package com.campspot.interview.reservationsystem.dao;

import com.campspot.interview.reservationsystem.models.Reservation;

import java.util.List;

public interface ReservationDao {
    List<Reservation> getReservations();
    Reservation getReservationByCampsiteId(String campsiteId);
    void createReservation(Reservation reservation);
}
