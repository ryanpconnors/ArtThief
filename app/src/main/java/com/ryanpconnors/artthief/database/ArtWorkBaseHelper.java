package com.ryanpconnors.artthief.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ryanpconnors.artthief.database.ArtWorkDbSchema.ArtWorkTable;

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
    db.execSQL("create table " + ArtWorkTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    ArtWorkTable.Cols.UUID + ", " +
                    ArtWorkTable.Cols.ART_THIEF_ID + "," +
                    ArtWorkTable.Cols.ARTIST + ", " +
                    ArtWorkTable.Cols.LARGE_IMAGE + ", " +
                    ArtWorkTable.Cols.MEDIA + ", " +
                    ArtWorkTable.Cols.LARGE_IMAGE_URL + ", " +
                    ArtWorkTable.Cols.SHOW_ID + ", " +
                    ArtWorkTable.Cols.SMALL_IMAGE + ", " +
                    ArtWorkTable.Cols.SMALL_IMAGE_URL + ", " +
                    ArtWorkTable.Cols.STARS + ", " +
                    ArtWorkTable.Cols.TAGS + ", " +
                    ArtWorkTable.Cols.TITLE + ", " +
                    ArtWorkTable.Cols.TAKEN +
                    ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
