package com.ryanpconnors.art_thief.artgallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ryanpconnors.art_thief.database.ArtWorkBaseHelper;
import com.ryanpconnors.art_thief.database.ArtWorkCursorWrapper;
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema;
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.ArtWorkTable;
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.InfoTable;
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.SortArtworkTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Gallery class for storing downloaded artworks
 * Created by Ryan Connors on 2/9/16.
 */
public class Gallery {

    private static final String IMAGE_DIRECTORY_NAME = "artwork_images";
    private static Gallery sGallery;
    private static String TAG = "Gallery";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * @param context
     */
    private Gallery(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ArtWorkBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * @param context
     * @return
     */
    public static Gallery get(Context context) {
        if (sGallery == null) {
            sGallery = new Gallery(context);
        }
        return sGallery;
    }

    /**
     * @param artWork
     * @return
     */
    private static ContentValues getContentValues(ArtWork artWork) {
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

        values.put(ArtWorkTable.Cols.WIDTH, artWork.getWidth());
        values.put(ArtWorkTable.Cols.HEIGHT, artWork.getHeight());

        values.put(ArtWorkTable.Cols.STARS, artWork.getStars());
        values.put(ArtWorkTable.Cols.TAKEN, artWork.isTaken() ? 1 : 0);

        return values;
    }

    /**
     * Determines if the artworks for the given rating are sorted or not
     *
     * @param rating the artwork rating
     * @return true iff the artworks with the given rating are sorted, otherwise returns false
     */
    public boolean isSorted(int rating) {

        String whereClause = String.format("%s=?", SortArtworkTable.Cols.RATING);

        String[] whereArgs = {Integer.toString(rating)};

        Cursor cursor = mDatabase.query(
                SortArtworkTable.NAME,                          // Table name
                new String[]{SortArtworkTable.Cols.SORTED},     // Columns : [null] selects all columns
                whereClause,                                    // where [clause]
                whereArgs,                                      // where [args]
                null,                                  // groupBy
                null,                                   // having
                null                                   // orderBy
        );

        // TODO: API-Level 19+ can use automatic resource management
        try {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(SortArtworkTable.Cols.SORTED)) != 0;
        }
        finally {
            cursor.close();
        }
    }

    /**
     * Sets the sorted flag for the given rating
     *
     * @param rating the rating value to set
     * @param sorted the boolean flag
     */
    public void setSorted(int rating, boolean sorted) {

        if (rating <= 0 || rating > 5) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(SortArtworkTable.Cols.SORTED, sorted ? 1 : 0);

        mDatabase.update(SortArtworkTable.NAME, values,
                SortArtworkTable.Cols.RATING + " = ?",
                new String[]{String.valueOf(rating)});
    }

    /**
     * @param artThiefId the artThiefId used to identify the artWork
     * @return the ArtWork in this database that has the corresponding artThiefId,
     * if not ArtWork exists in the database with the artThiefId, null is returned.
     */
    public ArtWork getArtWork(int artThiefId) {

        return getArtWork(
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{String.valueOf(artThiefId)},
                null
        );
    }

    /**
     * @param showId String - the showId used to identify the artWork
     * @return the ArtWork in this database that has the corresponding showId,
     * if not ArtWork exists in the database with the showId, null is returned.
     */
    public ArtWork getArtWork(String showId) {

        return getArtWork(
                ArtWorkTable.Cols.SHOW_ID + " = ?",
                new String[]{showId},
                null
        );
    }

    /**
     * @param id
     * @return
     */
    public ArtWork getArtWork(UUID id) {
        return getArtWork(
                ArtWorkTable.Cols.UUID + " = ?",
                new String[]{id.toString()},
                null
        );
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private ArtWork getArtWork(String whereClause, String[] whereArgs, String orderBy) {

        ArtWorkCursorWrapper cursor = queryArtWorks(whereClause, whereArgs, orderBy);

        // TODO: API-Level 19+ can use automatic resource management
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
     * @param bitmapImage
     * @param imageName
     * @return
     */
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
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                Log.e(TAG, "FileOutputStream close error", ioe);
            }
        }
        return directory.getAbsolutePath() + "/" + imageName;
    }

    /**
     * @param path
     * @return
     */
    public Bitmap getArtWorkImage(String path) {
        try {
            return BitmapFactory.decodeStream(new FileInputStream(new File(path)));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param artWork
     */
    public void addArtWork(ArtWork artWork) {
        ContentValues values = getContentValues(artWork);
        try {
            mDatabase.insert(ArtWorkTable.NAME, null, values);
        }
        catch (SQLiteConstraintException sce) {
            Log.e(TAG, "Failed to add ArtWork to database", sce);
        }
    }

    /**
     * @param artWork
     */
    public void updateArtWork(ArtWork artWork) {
        String artThiefIdString = String.valueOf(artWork.getArtThiefID());
        ContentValues values = getContentValues(artWork);
        mDatabase.update(ArtWorkTable.NAME, values,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{artThiefIdString});
    }

    /**
     * @param artWork
     * @return
     */
    public boolean deleteArtWork(ArtWork artWork) {
        String artThiefIdString = Integer.toString(artWork.getArtThiefID());
        return mDatabase.delete(
                ArtWorkTable.NAME,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                new String[]{artThiefIdString}
        ) > 0;
    }

    /**
     * @return
     */
    public List<ArtWork> getArtWorks() {
        return getArtWorks(null, null, null);
    }

    public List<ArtWork> getArtworksSortedByShowId() {
        List<ArtWork> artWorks = getArtWorks();
        Collections.sort(artWorks, new Comparator<ArtWork>() {
            public int compare(ArtWork a1, ArtWork a2) {
                return extractInt(a1.getShowId()) - extractInt(a2.getShowId());
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return MAX_VALUE if no digits found
                return num.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(num);
            }
        });
        return artWorks;
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return getArtWorks().isEmpty();
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

        String whereClause = String.format("%s=? AND %s=? AND %s IS NOT NULL",
                ArtWorkTable.Cols.STARS,
                ArtWorkTable.Cols.TAKEN,
                ArtWorkTable.Cols.LARGE_IMAGE_PATH);

        List<String> whereArgs = new ArrayList<>();

        whereArgs.add(Integer.toString(stars));
        whereArgs.add(taken ? "1" : "0");

        String orderBy = "ORDERING ASC";

        return getArtWorks(whereClause, whereArgs.toArray(new String[0]), orderBy);
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private List<ArtWork> getArtWorks(String whereClause, String[] whereArgs, String orderBy) {

        List<ArtWork> artWorks = new ArrayList<>();
        ArtWorkCursorWrapper cursor = queryArtWorks(whereClause, whereArgs, orderBy);

        // TODO: API-Level 19+ can use automatic resource management
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
     * ordered by the given ordering String. Default ordering ASCENDING
     *
     * @param stars the number of stars to search by
     * @param order "ASC" for ASCENDING order, "DESC" for DESCENDING
     * @return
     */
    public List<ArtWork> getArtWorks(int stars, String order) {

        String whereClause = String.format("%s=?",
                ArtWorkTable.Cols.STARS);

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(Integer.toString(stars));

        String orderBy = "ORDERING ASC";

        switch (order) {

            case "DESC":
                orderBy = "ORDERING DESC";
                break;

            case "ASC":
                orderBy = "ORDERING ASC";
                break;

            default:
                break;
        }

        return getArtWorks(whereClause, whereArgs.toArray(new String[0]), orderBy);
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private ArtWorkCursorWrapper queryArtWorks(String whereClause, String[] whereArgs, String orderBy) {

        Cursor cursor = mDatabase.query(
                ArtWorkTable.NAME,      // Table name
                null,          // Columns : [null] selects all columns
                whereClause,            // where [clause]
                whereArgs,              // where [args]
                null,          // groupBy
                null,           // having
                orderBy                // orderBy
        );
        return new ArtWorkCursorWrapper(cursor);
    }


    /**
     * @param numStars
     * @return
     */
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

        // TODO: API-Level 19+ can use automatic resource management
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

    public HashMap<String, Object> getInfo() {
        Cursor cursor = mDatabase.query(
                InfoTable.NAME,         // Table name
                null,          // Columns : [null] selects all columns
                null,          // where [clause]
                null,      // where [args]
                null,          // groupBy
                null,           // having
                null           // orderBy
        );

        HashMap<String, Object> info = new HashMap<>();
        // TODO: API-Level 19+ can use automatic resource management
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            info.put("showYear", cursor.getInt(cursor.getColumnIndex(InfoTable.Cols.SHOW_YEAR)));
            info.put("dataVersion", cursor.getInt(cursor.getColumnIndex(InfoTable.Cols.DATA_VERSION)));
            info.put("lastUpdated", cursor.getString(cursor.getColumnIndex(InfoTable.Cols.DATE_LAST_UPDATED)));
        }
        finally {
            cursor.close();
        }
        return info;
    }


    /**
     * @param date
     * @param showYear
     * @param dataVersion
     */
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
            Log.e(TAG, "Failed to update Info table in database", sce);
        }
    }

    /**
     * Finds the user's top rated Artwork based on the number of stars and the ordering determined and returns it.
     *
     * @return the top rated artwork
     */
    public ArtWork getTopPickArtwork() {
        List<ArtWork> artWorks = new ArrayList<>();
        int stars = 5;

        while (artWorks.isEmpty() && stars >= 1) {
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


    /**
     * @return
     */
    public String getLastUpdateDate() {
        Cursor cursor = mDatabase.query(
                ArtWorkDbSchema.InfoTable.NAME,                     // String table
                new String[]{InfoTable.Cols.DATE_LAST_UPDATED},     // String[] columns,
                null,                                      // String selection,
                null,                                  // String[] selectionArgs,
                null,                                      // String groupBy,
                null,                                       // String having,
                null                                       // String orderBy)
        );

        // TODO: API-Level 19+ can use automatic resource management
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

    public void clearArtwork() {
        int deleted = mDatabase.delete(ArtWorkTable.NAME, "1", null);
        mDatabase.execSQL("vacuum");
        Log.i(TAG, String.format(Locale.US, "Deleted %d records from %s", deleted, ArtWorkTable.NAME));
    }
}
