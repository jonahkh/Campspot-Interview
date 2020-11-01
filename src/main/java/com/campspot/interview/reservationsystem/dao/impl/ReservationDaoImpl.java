package com.campspot.interview.reservationsystem.dao.impl;

import com.campspot.interview.reservationsystem.dao.ReservationDao;
import com.campspot.interview.reservationsystem.models.Reservation;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO implement
@Repository
public class ReservationDaoImpl implements ReservationDao {
    @Override
    public List<Reservation> getReservations() {
        return null;
    }

    @Override
    public Reservation getReservationByCampsiteId(String campsiteId) {
        return null;
    }

    @Override
    public void createReservation(Reservation reservation) {

    }
}
