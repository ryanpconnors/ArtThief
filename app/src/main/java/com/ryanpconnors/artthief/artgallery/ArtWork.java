package com.ryanpconnors.artthief.artgallery;

import java.util.UUID;

/**
 * Created by Ryan Connors on 2/9/16.
 */
public class ArtWork {

    private final UUID mId;

    private int mArtThiefID;
    private String mShowId;
    private int ordering;

    private String mTitle;
    private String mArtist;
    private String mMedia;
    private String mTags;

    private String mSmallImageUrl;
    private String mLargeImageUrl;

    private String mSmallImagePath;
    private String mLargeImagePath;

    private int mStars;
    private boolean mTaken;

    public ArtWork() {
        // Generate unique identifier
        this(UUID.randomUUID());
    }

    public ArtWork(UUID id) {
        mId = id;
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

    public String getShowId() {
        return mShowId;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
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

    public void setShowId(String showId) {
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

    public void swapOrder(ArtWork artWork) {
        int order = this.ordering;
        this.ordering = artWork.getOrdering();
        artWork.setOrdering(order);
    }


    /**
     * @param obj Object to compare to this ArtWork object for equality.
     * @return false if obj is not an instance of the class ArtWork.
     * Returns true iff the given object shares the same artThiefId, showId,
     * title, artist, media, tags, smallImageUrl and largeImageUrl.
     * Otherwise returns false.
     * <p>
     * Note: The method does not take into account the 'uuid', 'smallImagePath', 'largeImagePath',
     * 'stars', 'taken' and 'ordering' fields.
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ArtWork)) {
            return false;
        }
        ArtWork artWork = (ArtWork) obj;
        if (this.mArtThiefID == artWork.getArtThiefID() &&
                this.mShowId.equals(artWork.getShowId()) &&
                this.mTitle.equals(artWork.getTitle()) &&
                this.mArtist.equals(artWork.getArtist()) &&
                this.mMedia.equals((artWork.getMedia())) &&
                this.mTags.equals(artWork.getTags()) &&
                this.mSmallImageUrl.equals(artWork.getSmallImageUrl()) &&
                this.mLargeImageUrl.equals(artWork.getLargeImageUrl())
                ) {
            return true;
        }
        else {
            return false;
        }
    }

}
