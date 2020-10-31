package com.campspot.interview.reservationsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reservation implements Comparable<Reservation> {
    private String campsiteId;
    private Date startDate;
    private Date endDate;

    @Override
    public int compareTo(Reservation other) {
        return this.startDate.compareTo(other.startDate);
    }
}
