package com.ryanpconnors.artthief.artgallery;

import java.util.UUID;

/**
 * Created by Ryan Connors on 2/9/16.
 */
public class ArtWork {

    private final UUID mId;

    private int mArtThiefID;

    private int mShowId;

    private String mTitle;
    private String mArtist;
    private String mMedia;
    private String mTags;

    private String mLargeImageUrl;
    private String mSmallImageUrl;

    private String mSmallImagePath;
    private byte[] mSmallImage;

    private String mLargeImagePath;
    private byte[] mLargeImage;

    private int mStars;
    private boolean mTaken;

    public ArtWork() {
        // Generate unique identifier
        this(UUID.randomUUID());
    }

    public ArtWork(UUID id) {
        mId = id;

        //TODO set default values for class members
    }


    /**********
     * Getters
     **********/

    public UUID getId() {
        return mId;
    }

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

    public String getSmallImagePath() {
        return mSmallImagePath;
    }

    public String getLargeImagePath() {
        return mLargeImagePath;
    }

    public byte[] getLargeImage() {
        return mLargeImage;
    }

    public byte[] getSmallImage() {
        return mSmallImage;
    }

    public int getStars() {
        return mStars;
    }

    public boolean isTaken() {
        return mTaken;
    }

    /**********
     * Setters
     **********/

    public void setArtThiefID(int artThiefID) {
        mArtThiefID = artThiefID;
    }

    public void setShowId(int showId) {
        mShowId = showId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public void setMedia(String media) {
        mMedia = media;
    }

    public void setTags(String tags) {
        mTags = tags;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        mLargeImageUrl = largeImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        mSmallImageUrl = smallImageUrl;
    }

    public void setSmallImagePath(String smallImagePath) {
        mSmallImagePath = smallImagePath;
    }

    public void setLargeImagePath(String largeImagePath) {
        mLargeImagePath = largeImagePath;
    }

    public void setLargeImage(byte[] largeImage) {
        mLargeImage = largeImage;
    }

    public void setSmallImage(byte[] smallImage) {
        mSmallImage = smallImage;
    }

    public void setTaken(boolean taken) {
        mTaken = taken;
    }

    public void setStars(int stars) {
        if (stars < 0 || stars > 5) {
            throw new RuntimeException("Invalid number of ArtWork stars [ " + stars + " ]");
        }
        else {
            mStars = stars;
        }
    }

}
