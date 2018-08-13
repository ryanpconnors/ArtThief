package com.ryanpconnors.art_thief.artgallery

import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.ryanpconnors.art_thief.database.ArtWorkBaseHelper
import com.ryanpconnors.art_thief.database.ArtWorkCursorWrapper
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.ArtWorkTable
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.InfoTable
import com.ryanpconnors.art_thief.database.ArtWorkDbSchema.SortArtworkTable

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.Locale
import java.util.UUID

/**
 * Gallery class for storing downloaded artworks
 * Created by Ryan Connors on 2/9/16.
 */
class Gallery
/**
 * @param context
 */
private constructor(context: Context) {
    private val mContext: Context
    private val mDatabase: SQLiteDatabase

    /**
     * @return
     */
    val artWorks: List<ArtWork>
        get() = getArtWorks(null, null, null)

    // return MAX_VALUE if no digits found
    val artworksSortedByShowId: List<ArtWork>
        get() {
            val artWorks = artWorks
            Collections.sort(artWorks, object : Comparator<ArtWork> {
                override fun compare(a1: ArtWork, a2: ArtWork): Int {
                    return extractInt(a1.showId!!) - extractInt(a2.showId!!)
                }

                internal fun extractInt(s: String): Int {
                    val num = s.replace("\\D".toRegex(), "")
                    return if (num.isEmpty()) Integer.MAX_VALUE else Integer.parseInt(num)
                }
            })
            return artWorks
        }

    /**
     * @return
     */
    val isEmpty: Boolean
        get() = artWorks.isEmpty()

    // Table name
    // Columns : [null] selects all columns
    // where [clause]
    // where [args]
    // groupBy
    // having
    // orderBy
    // TODO: API-Level 19+ can use automatic resource management
    val info: HashMap<String, Any>?
        get() {
            val cursor = mDatabase.query(
                    InfoTable.NAME, null, null, null, null, null, null
            )

            val info = HashMap<String, Any>()
            try {
                if (cursor.count == 0) {
                    return null
                }
                cursor.moveToFirst()
                info["showYear"] = cursor.getInt(cursor.getColumnIndex(InfoTable.Cols.SHOW_YEAR))
                info["dataVersion"] = cursor.getInt(cursor.getColumnIndex(InfoTable.Cols.DATA_VERSION))
                info["lastUpdated"] = cursor.getString(cursor.getColumnIndex(InfoTable.Cols.DATE_LAST_UPDATED))
            } finally {
                cursor.close()
            }
            return info
        }

    /**
     * Finds the user's top rated Artwork based on the number of stars and the ordering determined and returns it.
     *
     * @return the top rated artwork
     */
    val topPickArtwork: ArtWork?
        get() {
            var artWorks: List<ArtWork> = ArrayList()
            var stars = 5

            while (artWorks.isEmpty() && stars >= 1) {
                artWorks = getArtWorks(stars, false)
                stars -= 1
            }
            if (artWorks.isEmpty()) {
                return null
            } else {
                Collections.sort(artWorks)
                return artWorks[artWorks.size - 1]
            }
        }


    /**
     * @return
     */
    // String table
    // String[] columns,
    // String selection,
    // String[] selectionArgs,
    // String groupBy,
    // String having,
    // String orderBy)
    // TODO: API-Level 19+ can use automatic resource management
    val lastUpdateDate: String
        get() {
            val cursor = mDatabase.query(
                    ArtWorkDbSchema.InfoTable.NAME,
                    arrayOf(InfoTable.Cols.DATE_LAST_UPDATED), null, null, null, null, null
            )
            try {
                if (cursor.count == 0) {
                    return "N/A"
                }
                cursor.moveToFirst()
                return cursor.getString(cursor.getColumnIndex(InfoTable.Cols.DATE_LAST_UPDATED))
            } finally {
                cursor.close()
            }
        }

    init {
        mContext = context.applicationContext
        mDatabase = ArtWorkBaseHelper(mContext).writableDatabase
    }

    /**
     * Determines if the artworks for the given rating are sorted or not
     *
     * @param rating the artwork rating
     * @return true iff the artworks with the given rating are sorted, otherwise returns false
     */
    fun isSorted(rating: Int): Boolean {

        val whereClause = String.format("%s=?", SortArtworkTable.Cols.RATING)

        val whereArgs = arrayOf(Integer.toString(rating))

        val cursor = mDatabase.query(
                SortArtworkTable.NAME, // Table name
                arrayOf(SortArtworkTable.Cols.SORTED), // Columns : [null] selects all columns
                whereClause, // where [clause]
                whereArgs, null, null, null// orderBy
        )// where [args]
        // groupBy
        // having

        // TODO: API-Level 19+ can use automatic resource management
        try {
            cursor.moveToFirst()
            return cursor.getInt(cursor.getColumnIndex(SortArtworkTable.Cols.SORTED)) != 0
        } finally {
            cursor.close()
        }
    }

    /**
     * Sets the sorted flag for the given rating
     *
     * @param rating the rating value to set
     * @param sorted the boolean flag
     */
    fun setSorted(rating: Int, sorted: Boolean) {

        if (rating <= 0 || rating > 5) {
            return
        }

        val values = ContentValues()
        values.put(SortArtworkTable.Cols.SORTED, if (sorted) 1 else 0)

        mDatabase.update(SortArtworkTable.NAME, values,
                SortArtworkTable.Cols.RATING + " = ?",
                arrayOf(rating.toString()))
    }

    /**
     * @param artThiefId the artThiefId used to identify the artWork
     * @return the ArtWork in this database that has the corresponding artThiefId,
     * if not ArtWork exists in the database with the artThiefId, null is returned.
     */
    fun getArtWork(artThiefId: Int): ArtWork? {

        return getArtWork(
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                arrayOf(artThiefId.toString()), null
        )
    }

    /**
     * @param showId String - the showId used to identify the artWork
     * @return the ArtWork in this database that has the corresponding showId,
     * if not ArtWork exists in the database with the showId, null is returned.
     */
    fun getArtWork(showId: String): ArtWork? {

        return getArtWork(
                ArtWorkTable.Cols.SHOW_ID + " = ?",
                arrayOf(showId), null
        )
    }

    /**
     * @param id
     * @return
     */
    fun getArtWork(id: UUID): ArtWork? {
        return getArtWork(
                ArtWorkTable.Cols.UUID + " = ?",
                arrayOf(id.toString()), null
        )
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private fun getArtWork(whereClause: String, whereArgs: Array<String>, orderBy: String?): ArtWork? {

        val cursor = queryArtWorks(whereClause, whereArgs, orderBy)

        // TODO: API-Level 19+ can use automatic resource management
        try {
            if (cursor.count == 0) {
                return null
            }
            cursor.moveToFirst()
            return cursor.artWork
        } finally {
            cursor.close()
        }
    }

    /**
     * @param bitmapImage
     * @param imageName
     * @return
     */
    fun saveToInternalStorage(bitmapImage: Bitmap, imageName: String): String {

        val cw = ContextWrapper(mContext)

        // path to /data/data/com.ryanpconnors.art-thief/app_data/artwork_images
        val directory = cw.getDir(IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE)

        // Create imageDir
        val imageDirPath = File(directory, imageName)
        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(imageDirPath)

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (ioe: IOException) {
                Log.e(TAG, "FileOutputStream close error", ioe)
            }

        }
        return directory.absolutePath + "/" + imageName
    }

    /**
     * @param path
     * @return
     */
    fun getArtWorkImage(path: String): Bitmap? {
        try {
            return BitmapFactory.decodeStream(FileInputStream(File(path)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * @param artWork
     */
    fun addArtWork(artWork: ArtWork) {
        val values = getContentValues(artWork)
        try {
            mDatabase.insert(ArtWorkTable.NAME, null, values)
        } catch (sce: SQLiteConstraintException) {
            Log.e(TAG, "Failed to add ArtWork to database", sce)
        }

    }

    /**
     * @param artWork
     */
    fun updateArtWork(artWork: ArtWork) {
        val artThiefIdString = artWork.artThiefID.toString()
        val values = getContentValues(artWork)
        mDatabase.update(ArtWorkTable.NAME, values,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                arrayOf(artThiefIdString))
    }

    /**
     * @param artWork
     * @return
     */
    fun deleteArtWork(artWork: ArtWork): Boolean {
        val artThiefIdString = Integer.toString(artWork.artThiefID)
        return mDatabase.delete(
                ArtWorkTable.NAME,
                ArtWorkTable.Cols.ART_THIEF_ID + " = ?",
                arrayOf(artThiefIdString)
        ) > 0
    }

    /**
     * Returns all artworks with the given number of stars,
     * ordered by 'ORDERING' in ascending order.
     *
     * @param stars
     * @param taken
     * @return
     */
    fun getArtWorks(stars: Int, taken: Boolean): List<ArtWork> {

        val whereClause = String.format("%s=? AND %s=? AND %s IS NOT NULL",
                ArtWorkTable.Cols.STARS,
                ArtWorkTable.Cols.TAKEN,
                ArtWorkTable.Cols.LARGE_IMAGE_PATH)

        val whereArgs = ArrayList<String>()

        whereArgs.add(Integer.toString(stars))
        whereArgs.add(if (taken) "1" else "0")

        val orderBy = "ORDERING ASC"

        return getArtWorks(whereClause, whereArgs.toTypedArray<String>(), orderBy)
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private fun getArtWorks(whereClause: String?, whereArgs: Array<String>?, orderBy: String?): List<ArtWork> {

        val artWorks = ArrayList<ArtWork>()
        val cursor = queryArtWorks(whereClause, whereArgs, orderBy)

        // TODO: API-Level 19+ can use automatic resource management
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                artWorks.add(cursor.artWork)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
        return artWorks

    }

    /**
     * Returns all artworks with the given number of stars,
     * ordered by the given ordering String. Default ordering ASCENDING
     *
     * @param stars the number of stars to search by
     * @param order "ASC" for ASCENDING order, "DESC" for DESCENDING
     * @return
     */
    fun getArtWorks(stars: Int, order: String): List<ArtWork> {

        val whereClause = String.format("%s=?",
                ArtWorkTable.Cols.STARS)

        val whereArgs = ArrayList<String>()
        whereArgs.add(Integer.toString(stars))

        var orderBy = "ORDERING ASC"

        when (order) {

            "DESC" -> orderBy = "ORDERING DESC"

            "ASC" -> orderBy = "ORDERING ASC"

            else -> {
            }
        }

        return getArtWorks(whereClause, whereArgs.toTypedArray<String>(), orderBy)
    }

    /**
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     */
    private fun queryArtWorks(whereClause: String?, whereArgs: Array<String>?, orderBy: String?): ArtWorkCursorWrapper {

        val cursor = mDatabase.query(
                ArtWorkTable.NAME, null, // Columns : [null] selects all columns
                whereClause, // where [clause]
                whereArgs, null, null, // having
                orderBy                // orderBy
        )// Table name
        // where [args]
        // groupBy
        return ArtWorkCursorWrapper(cursor)
    }


    /**
     * Returns the total number of available, not `TAKEN`, artworks for the given number of stars
     *
     * @param numStars the number of stars to get the count of artworks for
     * @return the total number of artworks that have a rating of `numStars` that have not been marked `TAKEN`
     */
    fun getStarCount(numStars: String): Int {

        val cursor = mDatabase.query(
                ArtWorkTable.NAME,
                arrayOf("count(*)"),
                ArtWorkTable.Cols.STARS + " = ?" + " AND " +
                        ArtWorkTable.Cols.TAKEN + " = 0",
                arrayOf(numStars), null, null, null
        )

        // TODO: API-Level 19+ can use automatic resource management
        try {
            if (cursor.count == 0) {
                return 0
            }
            cursor.moveToFirst()
            return cursor.getInt(0)
        } finally {
            cursor.close()
        }
    }


    /**
     * @param date
     * @param showYear
     * @param dataVersion
     */
    fun updateInfo(date: String, showYear: Int, dataVersion: Int) {

        val values = ContentValues()
        values.put(InfoTable.Cols.DATE_LAST_UPDATED, date)
        values.put(InfoTable.Cols.DATA_VERSION, dataVersion)
        values.put(InfoTable.Cols.SHOW_YEAR, showYear)

        try {

            val lastDate = lastUpdateDate

            if (lastDate == "N/A") {
                mDatabase.insert(InfoTable.NAME, InfoTable.Cols.DATE_LAST_UPDATED, values)
            } else {
                mDatabase.update(
                        InfoTable.NAME,
                        values,
                        InfoTable.Cols.DATE_LAST_UPDATED + " = ?",
                        arrayOf(lastDate)
                )
            }
        } catch (sce: SQLiteConstraintException) {
            Log.e(TAG, "Failed to update Info table in database", sce)
        }

    }

    fun clearArtwork() {
        val deleted = mDatabase.delete(ArtWorkTable.NAME, "1", null)
        mDatabase.execSQL("vacuum")
        Log.i(TAG, String.format(Locale.US, "Deleted %d records from %s", deleted, ArtWorkTable.NAME))
    }

    companion object {

        private val IMAGE_DIRECTORY_NAME = "artwork_images"
        private var sGallery: Gallery? = null
        private val TAG = "Gallery"

        /**
         * @param context
         * @return
         */
        operator fun get(context: Context): Gallery {
            if (sGallery == null) {
                sGallery = Gallery(context)
            }
            return sGallery
        }

        /**
         * @param artWork
         * @return
         */
        private fun getContentValues(artWork: ArtWork): ContentValues {
            val values = ContentValues()
            values.put(ArtWorkTable.Cols.UUID, artWork.id.toString())

            values.put(ArtWorkTable.Cols.ART_THIEF_ID, artWork.artThiefID)
            values.put(ArtWorkTable.Cols.SHOW_ID, artWork.showId)
            values.put(ArtWorkTable.Cols.ORDERING, artWork.ordering)

            values.put(ArtWorkTable.Cols.TITLE, artWork.title)
            values.put(ArtWorkTable.Cols.ARTIST, artWork.artist)
            values.put(ArtWorkTable.Cols.MEDIA, artWork.media)
            values.put(ArtWorkTable.Cols.TAGS, artWork.tags)

            values.put(ArtWorkTable.Cols.SMALL_IMAGE_URL, artWork.smallImageUrl)
            values.put(ArtWorkTable.Cols.SMALL_IMAGE_PATH, artWork.smallImagePath)

            values.put(ArtWorkTable.Cols.LARGE_IMAGE_URL, artWork.largeImageUrl)
            values.put(ArtWorkTable.Cols.LARGE_IMAGE_PATH, artWork.largeImagePath)

            values.put(ArtWorkTable.Cols.WIDTH, artWork.width)
            values.put(ArtWorkTable.Cols.HEIGHT, artWork.height)

            values.put(ArtWorkTable.Cols.STARS, artWork.stars)
            values.put(ArtWorkTable.Cols.TAKEN, if (artWork.isTaken) 1 else 0)

            return values
        }
    }
}
