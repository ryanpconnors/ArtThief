package com.ryanpconnors.artthief.artgallery;

import android.media.Image;

/**
 * Created by Ryan Connors on 2/9/16.
 */
public class Artwork {

    private int mArtThiefID;

    private int mShowId;

    private String mTitle;
    private String mArtist;
    private String mMedia;
    private String mTags;

    private String mLargeImageUrl;
    private String mSmallImageUrl;

    private Image mLargeImage;
    private Image mSmallImage;

    private int mStars;


    public Artwork(int artThiefID, int showId, String title, String artist, String media,
                   String tags, String largeImageUrl, String smallImageUrl) {
        mArtThiefID = artThiefID;
        mShowId = showId;
        mTitle = title;
        mArtist = artist;
        mMedia = media;
        mTags = tags;
        mLargeImageUrl = largeImageUrl;
        mSmallImageUrl = smallImageUrl;
        mLargeImage = null;
        mSmallImage = null;
        mStars = 0;
    }


    /**
     * Getters
     */

    public int getArtThiefID() {
        return mArtThiefID;
    }

    public int getShowId() {
        return mShowId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getMedia() {
        return mMedia;
    }

    public String getTags() {
        return mTags;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public Image getLargeImage() {
        return mLargeImage;
    }

    public Image getSmallImage() {
        return mSmallImage;
    }

    public int getStars() {
        return mStars;
    }

    /**
     * Setters
     */

    public void setStars(int stars) {

        if (stars > 0 || stars > 5) {
            throw new RuntimeException("Invalid number of Artwork stars [ " + stars + " ]");
        }
        else {
            mStars = stars;
        }
    }
}
