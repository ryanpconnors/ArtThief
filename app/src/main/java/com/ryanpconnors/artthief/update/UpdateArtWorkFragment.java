package com.ryanpconnors.artthief.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;
import com.ryanpconnors.artthief.artgallery.GalleryFetcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateArtWorkFragment.OnUpdateArtWorkFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateArtWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateArtWorkFragment extends Fragment {

    private static final String TAG = "UpdateArtWorkFragment";

    private Button mUpdateArtWorksButton;
    private TextView mLastUpdateTextView;

    private OnUpdateArtWorkFragmentInteractionListener mArtWorkUpdateListener;

    public UpdateArtWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateArtWorkFragment.
     */
    public static UpdateArtWorkFragment newInstance() {

        UpdateArtWorkFragment fragment = new UpdateArtWorkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment to ensure that the asynchronous DownloadArtWorksTask completes
        setRetainInstance(true);

        if (getArguments() != null) {
            // store given arguments in this class instance
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_update_art, container, false);

        // Setup the Update ArtWorks Button
        mUpdateArtWorksButton = (Button) v.findViewById(R.id.update_artworks_button);
        mUpdateArtWorksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateArtWorksButton.setEnabled(false);
                mUpdateArtWorksButton.setAlpha(.5f);
                new UpdateArtWorksTask().execute();
            }
        });

        // Setup the last update date textView
        mLastUpdateTextView = (TextView) v.findViewById(R.id.last_update_date_text);
        mLastUpdateTextView.setText(String.format("%s %s",
                getString(R.string.update_art_last_update),
                Gallery.get(getActivity()).getLastUpdateDate()));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnUpdateArtWorkFragmentInteractionListener) {
            mArtWorkUpdateListener = (OnUpdateArtWorkFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mArtWorkUpdateListener = null;
    }

    //TODO: perform image downloads in separate async tasks???
    private void downloadImageFiles(GalleryFetcher fetcher, ArtWork artWork) {

        // Obtain the small ArtWork image
        String smallImageUrl = artWork.getSmallImageUrl();
        if (smallImageUrl != null) {
            Bitmap artWorkImage = fetcher.fetchArtWorkImage(smallImageUrl);
            String smallImagePath = Gallery.get(getActivity()).saveToInternalStorage(
                    artWorkImage,
                    artWork.getArtThiefID() + "S.jpg"
            );
            artWork.setSmallImagePath(smallImagePath);
        }

        // Obtain the large ArtWork image
        String largeImageUrl = artWork.getLargeImageUrl();
        if (largeImageUrl != null) {
            Bitmap artWorkImage = fetcher.fetchArtWorkImage(largeImageUrl);
            String largeImagePath = Gallery.get(getActivity()).saveToInternalStorage(
                    artWorkImage,
                    artWork.getArtThiefID() + "L.jpg"
            );
            artWork.setLargeImagePath(largeImagePath);
        }
    }

    private void updateInfoTable(int showYear, int dataVersion) {

        if (showYear == -1 || dataVersion == -1) {
            Log.d(TAG, "Error in updateInfoTable() showYear= " + showYear +
                    " | dataVersion= " + dataVersion);
        }

        // Update the InfoTable in the database to reflect the current data
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format),
                getResources().getConfiguration().locale);
        sdf.setTimeZone(Calendar.getInstance().getTimeZone());
        String todayDate = sdf.format(new Date());
        Gallery.get(getActivity()).updateInfo(todayDate, showYear, dataVersion);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUpdateArtWorkFragmentInteractionListener {

        void onArtWorkDataSourceUpdate();
    }

    /**
     *
     */
    private class UpdateArtWorksTask extends AsyncTask<Void, String, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Updating Artworks");
            progressDialog.setMessage("Download in progress...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {

            GalleryFetcher fetcher = new GalleryFetcher();
            List<ArtWork> existingArtWorks = Gallery.get(getActivity()).getArtWorks();
            List<ArtWork> newArtwork = fetcher.fetchArtWorks();

            int gallerySize = existingArtWorks.size();
            int inserted = 0;
            int updated = 0;
            int removed = 0;

            //TODO Only update the artWorks that have changed
            for (ArtWork artWork : newArtwork) {

                ArtWork existingArtWork = Gallery.get(getActivity()).getArtWork(artWork.getArtThiefID());

                if (existingArtWork == null) {

                    // download image files if they exist and store the paths in newArtWork
                    downloadImageFiles(fetcher, artWork);

                    // Set the order of the new artwork to the current size of the gallery
                    artWork.setOrdering(gallerySize);

                    // insert the newArtWork into the Gallery database
                    Gallery.get(getActivity()).addArtWork(artWork);
                    inserted++;
                    gallerySize++;
                }
                else {
                    // update the existing artwork if it is not equal to newArtWork
                    if (!artWork.equals(existingArtWork)) {
                        Gallery.get(getActivity()).updateArtWork(artWork);
                        updated++;
                    }
                    // otherwise do nothing, the artwork does not need to be updated
                }
                publishProgress(artWork.getTitle());
            }

            //TODO: Remove existing artWork from the database if it is no longer in loot.json
            for (ArtWork existingArtWork : existingArtWorks) {
                if (!newArtwork.contains(existingArtWork)) {
                    Gallery.get(getActivity()).deleteArtWork(existingArtWork);
                    removed++;
                    gallerySize--;

                    // TODO : Reorder ALL artwork >= the one deleted to maintain order
                }
            }
            updateInfoTable(fetcher.getDataVersion(), fetcher.getShowYear());
            Log.d(TAG, "Inserted Artwork : " + inserted);
            Log.d(TAG, "Updated Artwork : " + updated);
            Log.d(TAG, "Removed Artwork : " + removed);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... args) {
            System.out.println("onProgressUpdate");
            for (String s : args) {
                progressDialog.setMessage("Updating : " + s);
            }
        }


        @Override
        protected void onPostExecute(Void arg) {
            mArtWorkUpdateListener.onArtWorkDataSourceUpdate();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("ArtWorks Updated!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            // Toast.makeText(getActivity(), "Updated ArtWorks", Toast.LENGTH_SHORT).show();

            mLastUpdateTextView.setText(getString(R.string.update_art_last_update) + " " + Gallery.get(getActivity()).getLastUpdateDate());
            mUpdateArtWorksButton.setEnabled(true);
            mUpdateArtWorksButton.setAlpha(1.0f);
        }
    }

}
