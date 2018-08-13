package com.ryanpconnors.art_thief.artgallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.ryanpconnors.art_thief.R

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.HashMap


/**
 * Created by Ryan Connors on 2/18/16.
 */
class GalleryFetcher {

    fun fetchLoot(context: Context): HashMap<String, Any>? {

        val loot = HashMap<String, Any>()

        try {

            val lootUrl = context.getString(R.string.loot_url)
            val lootString = getUrlString(lootUrl)
            val jsonBody = JSONObject(lootString)

            loot[context.getString(R.string.show_year)] = jsonBody.getInt(context.getString(R.string.show_year))
            loot[context.getString(R.string.data_version)] = jsonBody.getInt(context.getString(R.string.data_version))
            loot[context.getString(R.string.art_works)] = getArtworksArray(jsonBody)
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je)
            return null
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch artwork loot", ioe)
            return null
        }

        return loot
    }

    fun fetchArtWorkImage(imageUrl: String): Bitmap? {
        try {
            val imageData = getUrlBytes(imageUrl)
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch artwork image", ioe)
            return null
        }

    }

    @Throws(IOException::class)
    private fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val out = ByteArrayOutputStream()
            val `in` = connection.inputStream

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(connection.responseMessage + ": with " + urlSpec)
            }

            var bytesRead: Int
            val buffer = ByteArray(1024)
            while ((bytesRead = `in`.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead)
            }
            out.close()
            return out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    @Throws(IOException::class, JSONException::class)
    private fun getArtworksArray(jsonBody: JSONObject): ArrayList<ArtWork> {

        val artWorks = ArrayList<ArtWork>()
        val artWorksJsonArray = jsonBody.getJSONArray("artWorks")

        for (i in 0 until artWorksJsonArray.length()) {
            try {
                val obj = artWorksJsonArray.getJSONObject(i)
                if (obj != null) {
                    artWorks.add(getArtWorkFromJson(obj))
                }
            } catch (je: JSONException) {
                Log.d(TAG, "Failed to parse JSON object", je)
            }

        }
        return artWorks
    }


    @Throws(JSONException::class)
    private fun getArtWorkFromJson(jsonArtWorkObject: JSONObject): ArtWork {
        val artWork = ArtWork()
        try {
            artWork.artThiefID = jsonArtWorkObject.getInt("artThiefID")
            artWork.showId = jsonArtWorkObject.getString("showID")
            artWork.title = jsonArtWorkObject.getString("title")
            artWork.artist = jsonArtWorkObject.getString("artist")
            artWork.media = jsonArtWorkObject.getString("media")
            artWork.tags = jsonArtWorkObject.getString("tags")
            artWork.largeImageUrl = jsonArtWorkObject.getString("image_large")
            artWork.smallImageUrl = jsonArtWorkObject.getString("image_small")
            artWork.width = jsonArtWorkObject.getString("width")
            artWork.height = jsonArtWorkObject.getString("height")
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON artWork object: ", je)
        }

        return artWork
    }

    companion object {

        private val TAG = "GalleryFetcher"
    }

}// Required empty constructor
