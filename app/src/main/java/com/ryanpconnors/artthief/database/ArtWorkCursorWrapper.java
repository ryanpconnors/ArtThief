package com.ryanpconnors.artthief.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ryanpconnors.artthief.artgallery.ArtWork;

import java.util.UUID;

/**
 * Created by Ryan Connors on 2/12/16.
 */
public class ArtWorkCursorWrapper extends CursorWrapper {

    /**
     * Constructor for ArtWorkCursorWrapper, wrapping the given cursor
     * @param cursor
     */
    public ArtWorkCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Wrapper method that returns the current artwork pointed to by this cursor
     * @return ArtWork
     */
    public ArtWork getArtWork() {
        String uuidString = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.UUID));
        int artThiefId = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ART_THIEF_ID));
        int showId = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SHOW_ID));

        String title = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TITLE));
        String artist = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ARTIST));
        String media = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.MEDIA));
        String tags = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAGS));

        String largeImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_URL));
        String smallImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_URL));
        byte[] smallImage = getBlob(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE));
        byte[] largeImage = getBlob(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE));

        int stars = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.STARS));
        int taken = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAKEN));

        ArtWork artWork = new ArtWork(UUID.fromString(uuidString));

        artWork.setArtThiefID(artThiefId);
        artWork.setShowId(showId);
        artWork.setTitle(title);
        artWork.setArtist(artist);
        artWork.setMedia(media);
        artWork.setTags(tags);

        artWork.setSmallImageUrl(smallImageUrl);
        artWork.setSmallImage(smallImage);
        artWork.setLargeImageUrl(largeImageUrl);
        artWork.setLargeImage(largeImage);

        artWork.setStars(stars);
        artWork.setTaken(taken != 0);

        return artWork;
    }
}
