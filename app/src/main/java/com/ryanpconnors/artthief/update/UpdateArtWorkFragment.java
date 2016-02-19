package com.ryanpconnors.artthief.update;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;
import com.ryanpconnors.artthief.artgallery.GalleryFetcher;

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

        // Retain this fragment to ensure that the asynchronous UpdateArtWorksTask completes
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
                new UpdateArtWorksTask().execute();
            }
        });

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

    private class UpdateArtWorksTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            List<ArtWork> artWorksFromJson = new GalleryFetcher().fetchArtWorks();
            List<ArtWork> existingArtWorks = Gallery.get(getActivity()).getArtWorks();

            int inserted = 0;
            int updated = 0;
            int removed = 0;

            //TODO Only update the artWorks that have changed
            for (ArtWork newArtWork : artWorksFromJson) {

                ArtWork existingArtWork = Gallery.get(getActivity()).getArtWork(newArtWork.getArtThiefID());

                if (existingArtWork == null) {

                    // insert the newArtWork into the Gallery database
                    Gallery.get(getActivity()).addArtWork(newArtWork);
                    inserted++;
                }
                else {

                    // update the existing artwork if it is not equal to newArtWork
                    if (!newArtWork.equals(existingArtWork)) {
                        Gallery.get(getActivity()).updateArtWork(newArtWork);
                        updated++;
                    }
                    // otherwise do nothing, the artwork does not need to be updated
                }
            }

            //TODO Remove existing artWork from the database if it is not longer in loot.json
            for (ArtWork existingArtWork : existingArtWorks) {
                if (!artWorksFromJson.contains(existingArtWork)) {
                    Gallery.get(getActivity()).deleteArtWork(existingArtWork);
                    removed++;
                }
            }

            Log.d(TAG, "Inserted: " + inserted);
            Log.d(TAG, "Updated: " + updated);
            Log.d(TAG, "Removed: " + removed);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mArtWorkUpdateListener.onArtWorkDataSourceUpdate();
            Toast.makeText(getActivity(), "Updated ArtWorks", Toast.LENGTH_SHORT).show();
        }
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

}
