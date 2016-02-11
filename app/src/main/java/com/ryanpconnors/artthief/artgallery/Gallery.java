package com.ryanpconnors.artthief.artgallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        mDatabase = new GalleryBaseHelper(mContext).getWritableDatabase();
    }

    public static Gallery get(Context context) {
        if (sGallery == null) {
            sGallery = new Gallery(context);
        }
        return sGallery;
    }

}
