package com.ryanpconnors.art_thief.database

import android.database.Cursor
import android.database.CursorWrapper

import com.ryanpconnors.art_thief.artgallery.ArtWork

import java.util.UUID

/**
 * Created by Ryan Connors on 2/12/16.
 */
class ArtWorkCursorWrapper
/**
 * Constructor for ArtWorkCursorWrapper, wrapping the given cursor
 *
 * @param cursor
 */
(cursor: Cursor) : CursorWrapper(cursor) {

    /**
     * Wrapper method that returns the current artwork pointed to by this cursor
     *
     * @return ArtWork
     */
    val artWork: ArtWork
        get() {

            val artWork = ArtWork(UUID.fromString(getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.UUID))))

            artWork.artThiefID = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ART_THIEF_ID))
            artWork.showId = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SHOW_ID))
            artWork.ordering = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ORDERING))
            artWork.title = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TITLE))
            artWork.artist = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.ARTIST))
            artWork.media = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.MEDIA))
            artWork.tags = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAGS))

            artWork.smallImagePath = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_PATH))
            artWork.largeImagePath = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_PATH))

            artWork.smallImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.SMALL_IMAGE_URL))
            artWork.largeImageUrl = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.LARGE_IMAGE_URL))

            artWork.width = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.WIDTH))
            artWork.height = getString(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.HEIGHT))

            artWork.stars = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.STARS))
            artWork.isTaken = getInt(getColumnIndex(ArtWorkDbSchema.ArtWorkTable.Cols.TAKEN)) != 0

            return artWork
        }
}
