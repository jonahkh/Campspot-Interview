package com.campspot.interview.reservationsystem.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ReservationTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testCompareTo() throws ParseException {
        Date pastDate = SIMPLE_DATE_FORMAT.parse("2020-10-10");
        Date futureDate = SIMPLE_DATE_FORMAT.parse("2020-11-10");
        List<Date> listToSort = new ArrayList<>();
        listToSort.add(futureDate);
        listToSort.add(pastDate);
        Collections.sort(listToSort);

        assertEquals(pastDate, listToSort.get(0));
        assertEquals(futureDate, listToSort.get(1));
    }
}
