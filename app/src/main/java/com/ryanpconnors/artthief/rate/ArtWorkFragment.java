package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.ryanpconnors.artthief.artgallery.Gallery;

import java.util.UUID;

import static android.widget.RatingBar.*;


/**
 * Created by Ryan Connors on 2/14/16.
 */
public class ArtWorkFragment extends Fragment{

    private ArtWork mArtWork;

    private ImageView mArtWorkLargeImageView;
    private RatingBar mArtworkRatingBar;
    private TextView mArtWorkTitleTextView;
    private TextView mArtWorkArtistTextView;
    private TextView mArtworkMediaTextView;

    private OnArtWorkFragmentInteractionListener mListener;

    private static final String ARG_ARTWORK_ID = "artwork_id";

    public ArtWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArtWorkFragment.
     */
    public static ArtWorkFragment newInstance(UUID artWorkId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_ARTWORK_ID, artWorkId);
        ArtWorkFragment artWorkFragment = new ArtWorkFragment();
        artWorkFragment.setArguments(args);
        return artWorkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        UUID artWorkId = (UUID) getArguments().getSerializable(ARG_ARTWORK_ID);
        mArtWork = Gallery.get(getActivity()).getArtWork(artWorkId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artwork, container, false);

        //TODO: The application may be doing too much work on its main thread.
        mArtWorkLargeImageView = (ImageView) v.findViewById(R.id.artwork_large_image_view);
        String largeImagePath = mArtWork.getLargeImagePath();
        if (largeImagePath != null) {
            Bitmap largeArtWorkImage = Gallery.get(getActivity()).getArtWorkImage(largeImagePath);
            mArtWorkLargeImageView.setImageBitmap(largeArtWorkImage);
        }

        mArtworkRatingBar = (RatingBar) v.findViewById(R.id.artwork_rating_bar);
        mArtworkRatingBar.setRating(mArtWork.getStars());
        mArtworkRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //TODO: implement changing of the rating for the current ArtWork
            }
        });

        mArtWorkTitleTextView = (TextView) v.findViewById(R.id.artwork_title);
        mArtWorkTitleTextView.setText(mArtWork.getTitle());

        mArtWorkArtistTextView = (TextView) v.findViewById(R.id.artwork_artist);
        mArtWorkArtistTextView.setText(mArtWork.getArtist());

        mArtworkMediaTextView = (TextView) v.findViewById(R.id.artwork_media);
        mArtworkMediaTextView.setText(mArtWork.getMedia());

        return v;
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

    @Override
    public void onPause() {
        super.onPause();
        Gallery.get(getActivity()).updateArtWork(mArtWork);
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
