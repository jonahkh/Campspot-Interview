package com.campspot.interview.reservationsystem.dao;

import com.campspot.interview.reservationsystem.models.Campsite;

import java.util.List;

public interface CampsiteDao {
    List<Campsite> getCampsites();
    Campsite getCampsiteById(String campsiteId);
    void createCampsite(Campsite campsite);
}
