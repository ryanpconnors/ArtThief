package com.ryanpconnors.artthief.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ryanpconnors.artthief.database.ArtWorkDbSchema.*;


/**
 * Created by Ryan Connors on 2/12/16.
 */
public class ArtWorkBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "artWorkBase.db";

    public ArtWorkBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the Info Table
        db.execSQL("create table " + InfoTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            InfoTable.Cols.DATA_VERSION + " INTEGER, " +
            InfoTable.Cols.SHOW_YEAR + " INTEGER, " +
            InfoTable.Cols.DATE_LAST_UPDATED + " TEXT " +
            ")"
        );

        // Create the ArtWork Table
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

    }
}
