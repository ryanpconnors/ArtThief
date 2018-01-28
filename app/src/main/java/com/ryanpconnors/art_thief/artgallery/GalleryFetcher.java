package com.ryanpconnors.art_thief.artgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ryanpconnors.art_thief.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ryan Connors on 2/18/16.
 */
public class GalleryFetcher {

    private static final String TAG = "GalleryFetcher";


    public GalleryFetcher() {
        // Required empty constructor
    }

    public HashMap<String, Object> fetchLoot(Context context) {

        HashMap<String, Object> loot = new HashMap<>();

        try {

            String lootUrl = context.getString(R.string.loot_url);
            String lootString = getUrlString(lootUrl);
            JSONObject jsonBody = new JSONObject(lootString);

            loot.put(context.getString(R.string.show_year), jsonBody.getInt(context.getString(R.string.show_year)));
            loot.put(context.getString(R.string.data_version), jsonBody.getInt(context.getString(R.string.data_version)));
            loot.put(context.getString(R.string.art_works), getArtworksArray(jsonBody));
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
            return null;
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch artwork loot", ioe);
            return null;
        }
        return loot;
    }

    public Bitmap fetchArtWorkImage(String imageUrl) {
        try {
            byte[] imageData = getUrlBytes(imageUrl);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            return imageBitmap;
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch artwork image", ioe);
            return null;
        }
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private ArrayList<ArtWork> getArtworksArray(JSONObject jsonBody) throws IOException, JSONException {

        ArrayList<ArtWork> artWorks = new ArrayList<>();
        JSONArray artWorksJsonArray = jsonBody.getJSONArray("artWorks");

        for (int i = 0; i < artWorksJsonArray.length(); i++) {
            try {
                JSONObject obj = artWorksJsonArray.getJSONObject(i);
                if (obj != null) {
                    artWorks.add(getArtWorkFromJson(obj));
                }
            }
            catch (JSONException je) {
                Log.d(TAG, "Failed to parse JSON object", je);
            }
        }
        return artWorks;
    }


    private ArtWork getArtWorkFromJson(JSONObject jsonArtWorkObject) throws JSONException {
        ArtWork artWork = new ArtWork();
        try {
            artWork.setArtThiefID(jsonArtWorkObject.getInt("artThiefID"));
            artWork.setShowId(jsonArtWorkObject.getString("showID"));
            artWork.setTitle(jsonArtWorkObject.getString("title"));
            artWork.setArtist(jsonArtWorkObject.getString("artist"));
            artWork.setMedia(jsonArtWorkObject.getString("media"));
            artWork.setTags(jsonArtWorkObject.getString("tags"));
            artWork.setLargeImageUrl(jsonArtWorkObject.getString("image_large"));
            artWork.setSmallImageUrl(jsonArtWorkObject.getString("image_small"));
            artWork.setWidth(jsonArtWorkObject.getString("width"));
            artWork.setHeight(jsonArtWorkObject.getString("height"));
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON artWork object: ", je);
        }
        return artWork;
    }

}
