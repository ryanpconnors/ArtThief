package com.ryanpconnors.artthief.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ryanpconnors.artthief.database.ArtWorkDbSchema.ArtWorkTable;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema.InfoTable;
import com.ryanpconnors.artthief.database.ArtWorkDbSchema.SortArtworkTable;


/**
 * Created by Ryan Connors on 2/12/16.
 */
public class ArtWorkBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "artWorkBase.db";

    public ArtWorkBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createInfoTable(db);
        createSortArtworkTable(db);
        createArtworkTable(db);
    }

    private void createInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + InfoTable.NAME + "(" +
                " _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InfoTable.Cols.DATA_VERSION + " INTEGER, " +
                InfoTable.Cols.SHOW_YEAR + " INTEGER, " +
                InfoTable.Cols.DATE_LAST_UPDATED + " TEXT " +
                ")"
        );
    }

    private void createSortArtworkTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SortArtworkTable.NAME + "(" +
                SortArtworkTable.Cols.RATING + " INTEGER PRIMARY KEY NOT NULL UNIQUE, " +
                SortArtworkTable.Cols.SORTED + " BOOLEAN " +
                ")"
        );

        // Initialize the Sorted table with a false for each rating [1.. 5]
        for (int i = 1; i <= 5; i++) {
            ContentValues values = new ContentValues();
            values.put(SortArtworkTable.Cols.RATING, i);
            values.put(SortArtworkTable.Cols.SORTED, false);
            db.insert(SortArtworkTable.NAME, null, values);
        }
    }

    private void createArtworkTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ArtWorkTable.NAME + "(" +
                " _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtWorkTable.Cols.UUID + " INTEGER NOT NULL UNIQUE, " +
                ArtWorkTable.Cols.ART_THIEF_ID + " INTEGER NOT NULL UNIQUE, " +
                ArtWorkTable.Cols.SHOW_ID + " TEXT, " +
                ArtWorkTable.Cols.ORDERING + " INTEGER NOT NULL, " +
                ArtWorkTable.Cols.TITLE + " TEXT, " +
                ArtWorkTable.Cols.ARTIST + " TEXT, " +
                ArtWorkTable.Cols.MEDIA + " TEXT, " +
                ArtWorkTable.Cols.TAGS + " TEXT, " +
                ArtWorkTable.Cols.SMALL_IMAGE_URL + " TEXT, " +
                ArtWorkTable.Cols.SMALL_IMAGE_PATH + " TEXT, " +
                ArtWorkTable.Cols.LARGE_IMAGE_URL + " TEXT, " +
                ArtWorkTable.Cols.LARGE_IMAGE_PATH + ", " +
                ArtWorkTable.Cols.STARS + " INTEGER NOT NULL DEFAULT 0, " +
                ArtWorkTable.Cols.TAKEN + " BOOLEAN " +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                createSortArtworkTable(db);
                // avoid adding a break when adding additional versions
                // to ensure each update is applied in sequence
        }
    }
}
