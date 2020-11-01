package com.campspot.interview.reservationsystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ReservationsIT {
    private static final String JSON_TEST_CASE =
            "{\"search\": {\"startDate\": \"2018-06-04\",\"endDate\": \"2018-06-06\"},\"campsites\": [{\"id\": 1,\"name\": \"Cozy Cabin\"},{\"id\": 2,\"name\": \"Comfy Cabin\"},{\"id\": 3,\"name\": \"Rustic Cabin\"},{\"id\": 4,\"name\": \"Rickety Cabin\"},{\"id\": 5,\"name\": \"Cabin in the Woods\"}],\"reservations\": [{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 1, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-01\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-02\", \"endDate\": \"2018-06-03\"},{\"campsiteId\": 2, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-02\"},{\"campsiteId\": 3, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-09\"},{\"campsiteId\": 4, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-10\"}]}";
    public static final Gson GSON = new Gson();

    @Autowired
    private MockMvc controller;

    @Test
    public void testHappyPath() throws Exception {
        final MvcResult mvcResult = controller.perform(post("http://localhost:8080/campspot/reservations/reserve")
                .contentType("application/json")
                .content(JSON_TEST_CASE)).andExpect(status().is(200)).andReturn();
        final List<String> campsites = GSON.fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<List<String>>() {
        }.getType());

        assertEquals(3, campsites.size());
        assertEquals("Comfy Cabin", campsites.get(0));
        assertEquals("Rickety Cabin", campsites.get(1));
        assertEquals("Cabin in the Woods", campsites.get(2));
    }
}
