package com.ryanpconnors.artthief.artgallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ryanpconnors.artthief.database.ArtWorkBaseHelper;
import com.ryanpconnors.artthief.database.ArtWorkCursorWrapper;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema.ArtWorkTable;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema.InfoTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Gallery class for storing downloaded artworks
 * Created by Ryan Connors on 2/9/16.
 */
public class Gallery {

    private static Gallery sGallery;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static String TAG = "Gallery";
    private static final String IMAGE_DIRECTORY_NAME = "artwork_images";

    private Gallery(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ArtWorkBaseHelper(mContext).getWritableDatabase();
    }

    public static Gallery get(Context context) {
        if (sGallery == null) {
            sGallery = new Gallery(context);
        }
        return sGallery;
    }

    /**
     * @param artThiefId the artThiefId used to identify the artWork
     * @return the ArtWork in this database that has the corresponding artThiefId,
     * if not ArtWork exists in the database with the artThiefId, null is returned.
     */
    public ArtWork getArtWork(int artThiefId) {
        ArtWorkCursorWrapper cursor = queryArtWorks(
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{String.valueOf(artThiefId)},
                null
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


    /**
     * @param id
     * @return
     */
    public ArtWork getArtWork(UUID id) {
        ArtWorkCursorWrapper cursor = queryArtWorks(
                ArtWorkTable.Cols.UUID + " = ?",
                new String[]{id.toString()},
                null
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


    public String saveToInternalStorage(Bitmap bitmapImage, String imageName) {

        ContextWrapper cw = new ContextWrapper(mContext);

        // path to /data/data/com.ryanpconnors.artthief/app_data/artwork_images
        File directory = cw.getDir(IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);

        // Create imageDir
        File imageDirPath = new File(directory, imageName);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(imageDirPath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }
            catch (IOException ioe) {
                Log.e(TAG, "FileOutputStream close error", ioe);
            }
        }
        return directory.getAbsolutePath() + "/" + imageName;
    }


    public Bitmap getArtWorkImage(String path) {
        try {
            return BitmapFactory.decodeStream(new FileInputStream(new File(path)));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void addArtWork(ArtWork artWork) {
        ContentValues values = getContentValues(artWork);
        try {
            mDatabase.insert(ArtWorkTable.NAME, null, values);
        }
        catch (SQLiteConstraintException sce) {
            Log.e(TAG, "Failed to add ArtWork to database", sce);
        }
    }

    public void updateArtWork(ArtWork artWork) {
        String artThiefIdString = String.valueOf(artWork.getArtThiefID());
        ContentValues values = getContentValues(artWork);
        mDatabase.update(ArtWorkTable.NAME, values,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{artThiefIdString});
    }

    public boolean deleteArtWork(ArtWork artWork) {
        String artThiefIdString = Integer.toString(artWork.getArtThiefID());
        return mDatabase.delete(
                ArtWorkTable.Cols.ART_THIEF_ID,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{artThiefIdString}
        ) > 0;
    }

    public List<ArtWork> getArtWorks() {
        List<ArtWork> artWorks = new ArrayList<>();

        ArtWorkCursorWrapper cursor = queryArtWorks(null, null, null);

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


    /**
     * Returns all artworks with the given number of stars,
     * ordered by 'ORDERING' in ascending order.
     *
     * @param stars
     * @param taken
     * @return
     */
    public List<ArtWork> getArtWorks(int stars, boolean taken) {
        List<ArtWork> artWorks = new ArrayList<>();

        String whereClause = String.format("%s=? AND %s=? AND %s IS NOT NULL",
                ArtWorkTable.Cols.STARS,
                ArtWorkTable.Cols.TAKEN,
                ArtWorkTable.Cols.LARGE_IMAGE_PATH);

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(Integer.toString(stars));
        if (taken) {
            whereArgs.add("1");
        }
        else {
            whereArgs.add("0");
        }

        String orderBy = "ORDERING ASC";
        ArtWorkCursorWrapper cursor = queryArtWorks(whereClause, whereArgs.toArray(new String[0]), orderBy);

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


    public static ContentValues getContentValues(ArtWork artWork) {
        ContentValues values = new ContentValues();
        values.put(ArtWorkTable.Cols.UUID, artWork.getId().toString());

        values.put(ArtWorkTable.Cols.ART_THIEF_ID, artWork.getArtThiefID());
        values.put(ArtWorkTable.Cols.SHOW_ID, artWork.getShowId());
        values.put(ArtWorkTable.Cols.ORDERING, artWork.getOrdering());

        values.put(ArtWorkTable.Cols.TITLE, artWork.getTitle());
        values.put(ArtWorkTable.Cols.ARTIST, artWork.getArtist());
        values.put(ArtWorkTable.Cols.MEDIA, artWork.getMedia());
        values.put(ArtWorkTable.Cols.TAGS, artWork.getTags());

        values.put(ArtWorkTable.Cols.SMALL_IMAGE_URL, artWork.getSmallImageUrl());
        values.put(ArtWorkTable.Cols.SMALL_IMAGE_PATH, artWork.getSmallImagePath());

        values.put(ArtWorkTable.Cols.LARGE_IMAGE_URL, artWork.getLargeImageUrl());
        values.put(ArtWorkTable.Cols.LARGE_IMAGE_PATH, artWork.getLargeImagePath());

        values.put(ArtWorkTable.Cols.STARS, artWork.getStars());
        values.put(ArtWorkTable.Cols.TAKEN, artWork.isTaken() ? 1 : 0);

        return values;
    }

    private ArtWorkCursorWrapper queryArtWorks(String whereClause, String[] whereArgs, String orderBy) {

        Cursor cursor = mDatabase.query(
                ArtWorkTable.NAME,      // Table name
                null,                   // Columns : [null] selects all columns
                whereClause,            // where [clause]
                whereArgs,              // where [args]
                null,                   // groupBy
                null,                   // having
                orderBy                 // orderBy
        );
        return new ArtWorkCursorWrapper(cursor);
    }

    public int getStarCount(String numStars) {

        Cursor cursor = mDatabase.query(
                ArtWorkTable.NAME,
                new String[]{"count(*)"},
                ArtWorkTable.Cols.STARS + " = ?",
                new String[]{numStars},
                null,
                null,
                null
        );
        try {
            if (cursor.getCount() == 0) {
                return 0;
            }
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        finally {
            cursor.close();
        }
    }


    public void updateInfo(String date, int showYear, int dataVersion) {

        ContentValues values = new ContentValues();
        values.put(InfoTable.Cols.DATE_LAST_UPDATED, date);
        values.put(InfoTable.Cols.DATA_VERSION, dataVersion);
        values.put(InfoTable.Cols.SHOW_YEAR, showYear);

        try {

            String lastDate = getLastUpdateDate();

            if (lastDate.equals("N/A")) {
                mDatabase.insert(InfoTable.NAME, InfoTable.Cols.DATE_LAST_UPDATED, values);
            }
            else {
                mDatabase.update(
                        InfoTable.NAME,
                        values,
                        InfoTable.Cols.DATE_LAST_UPDATED + " = ?",
                        new String[]{lastDate}
                );
            }
        }
        catch (SQLiteConstraintException sce) {
            Log.e(TAG, "Failed to add ArtWork to database", sce);
        }
    }

    /**
     * Finds the user's top rated Artwork based on the number of stars and the ordering determined and returns it.
     * @return the top rated artwork
     */
    public ArtWork getTopPickArtwork() {
        List<ArtWork> artWorks = new ArrayList<>();
        int stars = 5;

        while (artWorks.isEmpty() && stars >= 0) {
            artWorks = getArtWorks(stars, false);
            stars -= 1;
        }
        if (artWorks.isEmpty()) {
            return null;
        }
        else {
            Collections.sort(artWorks);
            return artWorks.get(artWorks.size() - 1);
        }
    }

    public String getLastUpdateDate() {
        Cursor cursor = mDatabase.query(
                ArtWorkDbSchema.InfoTable.NAME,                     // String table
                new String[]{InfoTable.Cols.DATE_LAST_UPDATED},     // String[] columns,
                null,                                               // String selection,
                null,                                               // String[] selectionArgs,
                null,                                               // String groupBy,
                null,                                               // String having,
                null                                                // String orderBy)
        );
        try {
            if (cursor.getCount() == 0) {
                return "N/A";
            }
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(InfoTable.Cols.DATE_LAST_UPDATED));
        }
        finally {
            cursor.close();
        }
    }
}
