package com.campspot.interview.reservationsystem.dao.impl;

import com.campspot.interview.reservationsystem.dao.CampsiteDao;
import com.campspot.interview.reservationsystem.models.Campsite;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO implement
@Repository
public class CampsiteDaoImpl implements CampsiteDao {

    @Override
    public List<Campsite> getCampsites() {
        return null;
    }

    @Override
    public Campsite getCampsiteById(String campsiteId) {
        return null;
    }

    @Override
    public void createCampsite(Campsite campsite) {

    }
}
