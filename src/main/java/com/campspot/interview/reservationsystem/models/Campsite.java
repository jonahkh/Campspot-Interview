package com.campspot.interview.reservationsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Campsite {
    @NotNull
    private String id;

    @NotNull
    private String name;
}
