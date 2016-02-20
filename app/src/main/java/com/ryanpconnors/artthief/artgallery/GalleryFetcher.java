package com.ryanpconnors.artthief.artgallery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Connors on 2/18/16.
 */
public class GalleryFetcher {

    private static final String TAG = "GalleryFetcher";
    public static final String LOOT_URL = "http://artthief.zurka.com/loot.json";

    private int mDataVersion = -1;
    private int mShowYear = -1;

    public GalleryFetcher() {

    }

    public int getDataVersion() {
        return mDataVersion;
    }

    public int getShowYear() {
        return mShowYear;
    }

    public List<ArtWork> fetchArtWorks() {

        List<ArtWork> artWorks = new ArrayList<>();

        try {
            String jsonString = getUrlString(LOOT_URL);
            JSONObject jsonBody = new JSONObject(jsonString);
            Log.i(TAG, "Received Loot JSON: " + jsonString);

            parseJson(artWorks, jsonBody);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch artwork loot");
        }

        return artWorks;
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

            int bytesRead = 0;
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

    private void parseJson(List<ArtWork> artWorks, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONArray artWorksJsonArray = jsonBody.getJSONArray("artWorks");

        //TODO utilize the showYear and dataVersion JSON objects
        mShowYear = jsonBody.getInt("showYear");
        mDataVersion = jsonBody.getInt("dataVersion");

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
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON artWork object: ", je);
        }
        return artWork;
    }

}
