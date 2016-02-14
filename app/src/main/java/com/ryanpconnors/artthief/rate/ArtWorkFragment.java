package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;

import static android.widget.RatingBar.*;


/**
 * Created by Ryan Connors on 2/14/16.
 */
public class ArtWorkFragment extends Fragment{

    private ArtWork mArtwork;

    private ImageView mArtworkLargeImageView;
    private RatingBar mArtworkRatingBar;
    private TextView mArtWorkTitleTextView;
    private TextView mArtWorkArtistTextView;
    private TextView mArtworkMediaTextView;

    private OnArtWorkFragmentInteractionListener mListener;

    public ArtWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArtWorkFragment.
     */
    public static ArtWorkFragment newInstance() {
        ArtWorkFragment fragment = new ArtWorkFragment();

        // For arguments passed into the new instance
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mArtwork = new ArtWork();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artwork, container, false);

        mArtworkLargeImageView = (ImageView) v.findViewById(R.id.artwork_large_image_view);

        mArtworkRatingBar = (RatingBar) v.findViewById(R.id.artwork_rating_bar);

        mArtworkRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //TODO: implement changing of the rating for the current ArtWork
            }
        });

        mArtWorkTitleTextView = (TextView) v.findViewById(R.id.artwork_title);

        mArtWorkArtistTextView = (TextView) v.findViewById(R.id.artwork_artist);

        mArtworkMediaTextView = (TextView) v.findViewById(R.id.artwork_media);

        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onArtWorkFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArtWorkFragmentInteractionListener) {
            mListener = (OnArtWorkFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnArtWorkFragmentInteractionListener {

        // TODO: Update argument type and name
        void onArtWorkFragmentInteraction(Uri uri);
    }
}
