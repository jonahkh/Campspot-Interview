package com.campspot.interview.reservationsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationRequest {
    @NotNull
    @Valid
    private Search search;

    @NotEmpty
    @Valid
    private List<Campsite> campsites;

    @NotEmpty
    @Valid
    private List<Reservation> reservations;
}
