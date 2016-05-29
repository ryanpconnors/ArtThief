package com.ryanpconnors.artthief.compare;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SortArtWorkFragment.OnSortArtworkFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SortArtWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortArtWorkFragment extends Fragment {

    private static final String ARG_NUMBER_OF_STARS = "number_of_stars";

    private int mNumberOfStars;

    private List<ArtWork> mArtWorks;

    private ImageView mArtworkImageViewAlpha;
    private ImageView mArtworkImageViewBeta;

    private ShareActionProvider mShareActionProvider;

    private OnSortArtworkFragmentInteractionListener mListener;

    public SortArtWorkFragment() {
        // Required empty public constructor
    }

    public static SortArtWorkFragment newInstance(int numberOfStars) {
        SortArtWorkFragment fragment = new SortArtWorkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER_OF_STARS, numberOfStars);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNumberOfStars = getArguments().getInt(ARG_NUMBER_OF_STARS);
            mArtWorks = Gallery.get(getActivity()).getArtWorks(mNumberOfStars);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sort_art_work, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.sort_artwork_title));

        mArtworkImageViewAlpha = (ImageView) v.findViewById(R.id.artwork_large_image_view_alpha);
        String largeImagePathAlpha = mArtWorks.get(0).getLargeImagePath();
        if (largeImagePathAlpha != null) {
            Bitmap largeArtWorkImage = Gallery.get(getActivity()).getArtWorkImage(largeImagePathAlpha);
            mArtworkImageViewAlpha.setImageBitmap(largeArtWorkImage);
        }

        mArtworkImageViewBeta = (ImageView) v.findViewById(R.id.artwork_large_image_view_beta);
        String largeImagePathBeta = mArtWorks.get(1).getLargeImagePath();
        if (largeImagePathBeta != null) {
            Bitmap largeArtWorkImageBeta = Gallery.get(getActivity()).getArtWorkImage(largeImagePathBeta);
            mArtworkImageViewBeta.setImageBitmap(largeArtWorkImageBeta);
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSortArtworkFragmentInteractionListener) {
            mListener = (OnSortArtworkFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnSortArtworkFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
