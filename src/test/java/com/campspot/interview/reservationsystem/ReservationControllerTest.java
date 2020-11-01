package com.campspot.interview.reservationsystem;

import com.campspot.interview.reservationsystem.models.ReservationRequest;
import com.campspot.interview.reservationsystem.service.ReservationService;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {
    private static final Gson GSON = new Gson();

    @Autowired
    private MockMvc reservationController;

    @MockBean
    private ReservationService reservationService;

    @Test
    public void testGetCampsitesWithoutGap() throws Exception {
        List<String> response = Collections.singletonList("hello");
        when(reservationService.getCampsitesWithoutGap(any(), eq(1)))
                .thenReturn(response);
        final MvcResult mvcResult = reservationController.perform(post("http://localhost:8080/campspot/reservations/reserve")
                .contentType("application/json")
                .content(jsonTestCaseString)
        ).andExpect(status().is(200)).andReturn();

        assertEquals(GSON.toJson(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCampsitesWithoutGap_invalidRoute() throws Exception {
        reservationController.perform(post("http://localhost:8080/campspot/reservations/reverse")
                .contentType("application/json")
                .content(jsonTestCaseString))
                .andExpect(status().is(404)).andReturn();
    }

    private static final Supplier<ReservationRequest> jsonTestCaseSupplier = () -> new Gson().fromJson(
            "{\"search\": {\"startDate\": \"2018-06-04\",\"endDate\": \"2018-06-06\"},\"campsites\": [{\"id\": 1,\"name\": \"Cozy Cabin\"},{\"id\": 2,\"name\": \"Comfy Cabin\"},{\"id\": 3,\"name\": \"Rustic Cabin\"},{\"id\": 4,\"name\": \"Rickety Cabin\"},{\"id\": 5,\"name\": \"Cabin in the Woods\"}],\"reservations\": [{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 1, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-01\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-02\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-02\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 4, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-10\"}]}",
            ReservationRequest.class);

    private static final String jsonTestCaseString =
            "{\"search\": {\"startDate\": \"2018-06-04\",\"endDate\": \"2018-06-06\"},\"campsites\": [{\"id\": 1,\"name\": \"Cozy Cabin\"},{\"id\": 2,\"name\": \"Comfy Cabin\"},{\"id\": 3,\"name\": \"Rustic Cabin\"},{\"id\": 4,\"name\": \"Rickety Cabin\"},{\"id\": 5,\"name\": \"Cabin in the Woods\"}],\"reservations\": [{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 1, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-01\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-02\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-02\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 4, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-10\"}]}";
}
