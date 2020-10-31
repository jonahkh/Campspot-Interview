package com.campspot.interview.reservationsystem.service;

import com.campspot.interview.reservationsystem.models.ReservationRequest;

import java.util.Date;
import java.util.List;

public interface ReservationService {
    List<String> getCampsitesWithoutGap(ReservationRequest reservationRequest, int gapSize);
    List<String> getCampsitesWithoutGap(Date startDate, Date endDate, int gapSize);
}
