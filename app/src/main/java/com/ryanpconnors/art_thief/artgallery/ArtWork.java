package com.ryanpconnors.art_thief.artgallery;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by Ryan Connors on 2/9/16.
 */
public class ArtWork implements Comparable<ArtWork> {

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

    private String mWidth;
    private String mHeight;

    private int mStars;
    private boolean mTaken;

    public ArtWork() {
        this(UUID.randomUUID());
    }

    public ArtWork(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public int getArtThiefID() {
        return mArtThiefID;
    }

    public void setArtThiefID(int artThiefID) {
        mArtThiefID = artThiefID;
    }

    public String getShowId() {
        return mShowId;
    }

    public void setShowId(String showId) {
        mShowId = showId;
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

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getMedia() {
        return mMedia;
    }

    public void setMedia(String media) {
        mMedia = media;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String tags) {
        mTags = tags;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        mLargeImageUrl = largeImageUrl;
    }

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        mSmallImageUrl = smallImageUrl;
    }

    public String getSmallImagePath() {
        return mSmallImagePath;
    }

    public void setSmallImagePath(String smallImagePath) {
        mSmallImagePath = smallImagePath;
    }

    public String getLargeImagePath() {
        return mLargeImagePath;
    }

    public void setLargeImagePath(String largeImagePath) {
        mLargeImagePath = largeImagePath;
    }

    public int getStars() {
        return mStars;
    }

    public void setStars(int stars) {
        if (stars < 0 || stars > 5) {
            throw new RuntimeException("Invalid number of ArtWork stars [ " + stars + " ]");
        }
        else {
            mStars = stars;
        }
    }

    public boolean isTaken() {
        return mTaken;
    }

    public void setTaken(boolean taken) {
        mTaken = taken;
    }

    public String getWidth() {
        return mWidth;
    }

    public void setWidth(String mWidth) {
        this.mWidth = mWidth;
    }

    public String getHeight() {
        return mHeight;
    }

    public void setHeight(String mHeight) {
        this.mHeight = mHeight;
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
        return this.mArtThiefID == artWork.getArtThiefID() &&
                this.mShowId.equals(artWork.getShowId()) &&
                this.mTitle.equals(artWork.getTitle()) &&
                this.mArtist.equals(artWork.getArtist()) &&
                this.mMedia.equals((artWork.getMedia())) &&
                this.mTags.equals(artWork.getTags()) &&
                this.mSmallImageUrl.equals(artWork.getSmallImageUrl()) &&
                this.mLargeImageUrl.equals(artWork.getLargeImageUrl()) &&
                this.mWidth.equals(artWork.getWidth()) &&
                this.mHeight.equals(artWork.getHeight());
    }

    /**
     * Determines whether this ArtWork object is ordered before or after the given ArtWork.
     *
     * @param @NotNull that - an ArtWork object.
     * @return the difference between this ArtWorks stars and obj
     * or in the case that the number of stars is equal, return the difference in ordering.
     */
    @Override
    public int compareTo(@NonNull ArtWork that) {
        if (this.getStars() == that.getStars()) {
            return this.getOrdering() - that.getOrdering();
        }
        else {
            return this.getStars() - that.getStars();
        }
    }
}
