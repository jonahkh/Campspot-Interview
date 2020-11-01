package com.campspot.interview.reservationsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Search {
    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;
}
