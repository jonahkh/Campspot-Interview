package com.campspot.interview.reservationsystem.service;

import com.campspot.interview.reservationsystem.models.Campsite;
import com.campspot.interview.reservationsystem.models.Reservation;
import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.models.Search;
import com.campspot.interview.reservationsystem.service.impl.ReservationServiceImpl;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceImplTest {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    public void testGetCampsitesWithoutGap_providedTestFile() {
        final List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(jsonTestCaseSupplier.get(), 1);
        assertEquals(3, campsitesWithoutGap.size());
        assertEquals("Comfy Cabin", campsitesWithoutGap.get(0));
        assertEquals("Rickety Cabin", campsitesWithoutGap.get(1));
        assertEquals("Cabin in the Woods", campsitesWithoutGap.get(2));
    }

    // Use existing test file but add a reservation that has 1 day gap
    @Test
    public void testGetCampsitesWithoutGap_invalidateCabin() throws ParseException {
        final ReservationRequest reservationRequest = jsonTestCaseSupplier.get();
        // add reservation 06/01-06/02
        reservationRequest.getReservations().add(new Reservation("5", SIMPLE_DATE_FORMAT.parse("2018-06-01T"), SIMPLE_DATE_FORMAT.parse("2018-06-02T")));
        final List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(2, campsitesWithoutGap.size());
        assertEquals("Comfy Cabin", campsitesWithoutGap.get(0));
        assertEquals("Rickety Cabin", campsitesWithoutGap.get(1));
    }

    @Test
    public void testGetCampsitesWithoutGap_reservationAfterSearch() throws ParseException {
        final Search search = new Search(SIMPLE_DATE_FORMAT.parse("2018-06-04"), SIMPLE_DATE_FORMAT.parse("2018-06-06"));
        List<Campsite> campsites = Collections.singletonList(new Campsite("1", "Cozy Cabin"));
        List<Reservation> reservations = Collections.singletonList(new Reservation("1", SIMPLE_DATE_FORMAT.parse("2018-06-09"), SIMPLE_DATE_FORMAT.parse("2018-06-10")));
        final ReservationRequest reservationRequest = new ReservationRequest(search, campsites, reservations);
        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(1, campsitesWithoutGap.size());
        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));

        // Test same case but reservation the next day
        reservations.get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-07"));
        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(1, campsitesWithoutGap.size());
        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));
    }

    @Test
    public void testGetCampsiteWithoutGap_reservationSameDay() throws ParseException {
        // Test search start date == reservation start date
        final ReservationRequest reservationRequest = simpleTestCaseSupplier.get();
        final List<Reservation> reservations = reservationRequest.getReservations();
        reservations.get(0).setStartDate(reservationRequest.getSearch().getStartDate());
        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());

        // Test search end date == reservation end date
        reservations.get(0).setEndDate(reservationRequest.getSearch().getEndDate());
        reservations.get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-03"));
        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());

        // Test search start date == reservation end date
        reservations.get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-06-04"));
        reservations.get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-03"));
        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());

        // Test search end date == reservation start date
        reservations.get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-06-10"));
        reservations.get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-06"));
        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());
    }

    @Test
    public void testGetCampsiteWithoutGap_reservationBeforeSearch() throws ParseException {
        final ReservationRequest reservationRequest = simpleTestCaseSupplier.get();

        reservationRequest.getReservations().get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-05-20"));
        reservationRequest.getReservations().get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-05-22"));

        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(1, campsitesWithoutGap.size());
        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));

        // Test with reservation the day after search end
        reservationRequest.getReservations().get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-06-03"));
        assertEquals(1, campsitesWithoutGap.size());
        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));
    }
    @Test
    public void testGetCampsiteWithoutGap_reservationWrapsSearch() throws  ParseException {
        final ReservationRequest reservationRequest = simpleTestCaseSupplier.get();

        // Test same start and end date
        reservationRequest.getReservations().get(0).setStartDate(reservationRequest.getSearch().getStartDate());
        reservationRequest.getReservations().get(0).setEndDate(reservationRequest.getSearch().getEndDate());
        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());

        // Test search dates within reservation dates
        reservationRequest.getReservations().get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-03"));
        reservationRequest.getReservations().get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-06-07"));
        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());
    }

    // For the sake of simplicity this exercise, we are only validating fields at the API layer, not at the service.
    // Therefore, assume valid values from the service onwards
//    @Test
    public void testGetCampsiteWithoutGap_noReservations() {
//        // Test null
//        final ReservationRequest reservationRequest = simpleTestCaseSupplier.get();
//        reservationRequest.setReservations(null);
//        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
//        assertEquals(1, campsitesWithoutGap.size());
//        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));
//
//        // Test empty
//        reservationRequest.setReservations(Collections.emptyList());
//        campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
//        assertEquals(1, campsitesWithoutGap.size());
//        assertEquals("Cozy Cabin", campsitesWithoutGap.get(0));
    }

    @Test
    public void testGetCampsiteWithoutGap_noAvailableReservations() throws ParseException {
        final ReservationRequest reservationRequest = simpleTestCaseSupplier.get();

        reservationRequest.getReservations().get(0).setStartDate(SIMPLE_DATE_FORMAT.parse("2018-06-05"));
        reservationRequest.getReservations().get(0).setEndDate(SIMPLE_DATE_FORMAT.parse("2018-06-07"));
        List<String> campsitesWithoutGap = reservationService.getCampsitesWithoutGap(reservationRequest, 1);
        assertEquals(0, campsitesWithoutGap.size());
    }

    private static final Supplier<ReservationRequest> simpleTestCaseSupplier = () -> {
        try {
            final Search search = new Search(SIMPLE_DATE_FORMAT.parse("2018-06-04"), SIMPLE_DATE_FORMAT.parse("2018-06-06"));
            List<Campsite> campsites = Collections.singletonList(new Campsite("1", "Cozy Cabin"));
            List<Reservation> reservations = Collections.singletonList(new Reservation("1", SIMPLE_DATE_FORMAT.parse("2018-06-04"), SIMPLE_DATE_FORMAT.parse("2018-06-10")));
            return new ReservationRequest(search, campsites, reservations);
        } catch (ParseException e) {
            // This shouldn't happen but fail if it does and fix
            fail();
            return null; // test will exit before this
        }
    };

    private static final Supplier<ReservationRequest> jsonTestCaseSupplier = () -> new Gson().fromJson(
            "{\"search\": {\"startDate\": \"2018-06-04\",\"endDate\": \"2018-06-06\"},\"campsites\": [{\"id\": 1,\"name\": \"Cozy Cabin\"},{\"id\": 2,\"name\": \"Comfy Cabin\"},{\"id\": 3,\"name\": \"Rustic Cabin\"},{\"id\": 4,\"name\": \"Rickety Cabin\"},{\"id\": 5,\"name\": \"Cabin in the Woods\"}],\"reservations\": [{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 1, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-01\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-02\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-02\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 4, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-10\"}]}",
            ReservationRequest.class);
}
