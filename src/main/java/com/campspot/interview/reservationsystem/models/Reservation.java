package com.campspot.interview.reservationsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reservation implements Comparable<Reservation> {
    @NotNull
    private String campsiteId;

    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    @Override
    public int compareTo(Reservation other) {
        return this.startDate.compareTo(other.startDate);
    }
}
