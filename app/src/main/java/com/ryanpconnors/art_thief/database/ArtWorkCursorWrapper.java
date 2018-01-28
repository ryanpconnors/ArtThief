package com.ryanpconnors.art_thief.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ryanpconnors.art_thief.artgallery.ArtWork;

import java.util.UUID;

/**
 * Created by Ryan Connors on 2/12/16.
 */
public class ArtWorkCursorWrapper extends CursorWrapper {

    /**
     * Constructor for ArtWorkCursorWrapper, wrapping the given cursor
     *
     * @param cursor
     */
    public ArtWorkCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Wrapper method that returns the current artwork pointed to by this cursor
     *
     * @return ArtWork
     */
    public ArtWork getArtWork() {

        ArtWork artWork = new ArtWork(UUID.fromString(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.UUID))));

        artWork.setArtThiefID(getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ART_THIEF_ID)));
        artWork.setShowId(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SHOW_ID)));
        artWork.setOrdering(getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ORDERING)));
        artWork.setTitle(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TITLE)));
        artWork.setArtist(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ARTIST)));
        artWork.setMedia(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.MEDIA)));
        artWork.setTags(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAGS)));

        artWork.setSmallImagePath(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_PATH)));
        artWork.setLargeImagePath(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_PATH)));

        artWork.setSmallImageUrl(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_URL)));
        artWork.setLargeImageUrl(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_URL)));

        artWork.setWidth(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.WIDTH)));
        artWork.setHeight(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.HEIGHT)));

        artWork.setStars(getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.STARS)));
        artWork.setTaken(getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAKEN)) != 0);

        return artWork;
    }
}
