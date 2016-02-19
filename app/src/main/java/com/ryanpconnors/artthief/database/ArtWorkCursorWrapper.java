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
        String showId = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SHOW_ID));

        String title = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TITLE));
        String artist = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ARTIST));
        String media = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.MEDIA));
        String tags = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAGS));

        String smallImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_URL));
        String largeImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_URL));

        String smallImagePath = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_PATH));
        String largeImagePath = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_PATH));

        int stars = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.STARS));
        int taken = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAKEN));

        ArtWork artWork = new ArtWork(UUID.fromString(uuidString));

        artWork.setArtThiefID(artThiefId);
        artWork.setShowId(showId);
        artWork.setTitle(title);
        artWork.setArtist(artist);
        artWork.setMedia(media);
        artWork.setTags(tags);

        artWork.setSmallImagePath(smallImagePath);
        artWork.setLargeImagePath(largeImagePath);

        artWork.setSmallImageUrl(smallImageUrl);
        artWork.setLargeImageUrl(largeImageUrl);

        artWork.setStars(stars);
        artWork.setTaken(taken != 0);

        return artWork;
    }
}
