package com.ryanpconnors.artthief.artgallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ryanpconnors.artthief.database.ArtWorkBaseHelper;
import com.ryanpconnors.artthief.database.ArtWorkCursorWrapper;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema.ArtWorkTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gallery class for storing downloaded artworks and
 * Created by Ryan Connors on 2/9/16.
 */
public class Gallery {

    private static Gallery sGallery;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private Gallery(Context context) {
        mContext = context.getApplicationContext();
        mDatabase= new ArtWorkBaseHelper(mContext).getWritableDatabase();
    }

    public static Gallery get(Context context) {
        if (sGallery == null) {
            sGallery = new Gallery(context);
        }
        return sGallery;
    }

    public void addArtWork(ArtWork artWork) {
        ContentValues values = getContentValues(artWork);
        mDatabase.insert(ArtWorkTable.NAME, null, values);
    }

    public ArtWork getArtWork(UUID id) {
        ArtWorkCursorWrapper cursor = queryArtWorks(
                ArtWorkTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getArtWork();
        }
        finally {
            cursor.close();
        }
    }

    public List<ArtWork> getArtWorks() {
        List<ArtWork> artWorks = new ArrayList<>();

        ArtWorkCursorWrapper cursor = queryArtWorks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                artWorks.add(cursor.getArtWork());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return artWorks;
    }

    public void updateArtWork(ArtWork artWork) {
        String uuidString = artWork.getId().toString();
        ContentValues values = getContentValues(artWork);
        mDatabase.update(ArtWorkTable.NAME, values,
                ArtWorkTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public static ContentValues getContentValues(ArtWork artWork) {
        ContentValues values = new ContentValues();
        values.put(ArtWorkTable.Cols.UUID, artWork.getId().toString());

        values.put(ArtWorkTable.Cols.ART_THIEF_ID, artWork.getArtThiefID());
        values.put(ArtWorkTable.Cols.SHOW_ID, artWork.getShowId());
        values.put(ArtWorkTable.Cols.TITLE, artWork.getTitle());
        values.put(ArtWorkTable.Cols.ARTIST, artWork.getArtist());
        values.put(ArtWorkTable.Cols.MEDIA, artWork.getMedia());
        values.put(ArtWorkTable.Cols.TAGS, artWork.getTags());

        values.put(ArtWorkTable.Cols.SMALL_IMAGE_URL, artWork.getSmallImageUrl());
        values.put(ArtWorkTable.Cols.SMALL_IMAGE, artWork.getSmallImage());
        values.put(ArtWorkTable.Cols.LARGE_IMAGE_URL, artWork.getLargeImageUrl());
        values.put(ArtWorkTable.Cols.LARGE_IMAGE, artWork.getLargeImage());

        values.put(ArtWorkTable.Cols.STARS, artWork.getStars());
        values.put(ArtWorkTable.Cols.TAKEN, artWork.isTaken() ? 1 : 0);

        return values;
    }

    private ArtWorkCursorWrapper queryArtWorks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
               ArtWorkTable.NAME,
                null,                   // Columns : [null] selects from all columns
                whereClause,            // where [clause]
                whereArgs,              // where [args]
                null,                   // groupBy
                null,                   // having
                null                    // orderBy
        );

        return new ArtWorkCursorWrapper(cursor);

    }

}
