package com.ryanpconnors.artthief.artgallery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gallery class for storing downloaded artworks and
 * Created by Ryan Connors on 2/9/16.
 */
public class Gallery {

    private List<Artwork> mArtworks;
    private Date mDateLastUpdated;
    private String mShowYear;
    private String mDataVersion;

    public Gallery() {
        mArtworks = new ArrayList<>();
        mDateLastUpdated = null;
        mShowYear = null;
        mDataVersion = null;
    }


    public Date getDateLastUpdated() {
        return mDateLastUpdated;
    }

    public void setDateLastUpdated(Date dateUpdated) {
        mDateLastUpdated = dateUpdated;
    }

    public List<Artwork> getArtworks() {
        return mArtworks;
    }

    public int numberOfArtworks() {
        return mArtworks.size();
    }

}
