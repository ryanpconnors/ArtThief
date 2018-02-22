package com.ryanpconnors.art_thief.compare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ryanpconnors.art_thief.R;
import com.ryanpconnors.art_thief.artgallery.Gallery;

import java.util.logging.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompareArtWorkFragment.OnCompareArtFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompareArtWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompareArtWorkFragment extends Fragment {

    private static final String TAG = "CompareArtWorkFragment";

    private static final Logger LOG = Logger.getLogger(CompareArtWorkFragment.TAG);

    private OnCompareArtFragmentInteractionListener mCompareArtWorkListener;
    private Button mFiveStarButton;
    private Button mFourStarButton;
    private Button mThreeStarButton;
    private Button mTwoStarButton;
    private Button mOneStarButton;

    private ImageView mFiveStarDoneImageView;
    private ImageView mFourStarDoneImageView;
    private ImageView mThreeStarDoneImageView;
    private ImageView mTwoStarDoneImageView;
    private ImageView mOneStarDoneImageView;

    public CompareArtWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CompareArtWorkFragment.
     */
    public static CompareArtWorkFragment newInstance() {

        CompareArtWorkFragment fragment = new CompareArtWorkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Retain this fragment to ensure that the asynchronous UpdateArtWorksTask completes
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //TODO: store given arguments in this class instance
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_compare_art, container, false);

        mOneStarButton = (Button) v.findViewById(R.id.compare_artwork_one_star_button);
        mOneStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSortArtWorkActivity(1);
            }
        });

        mOneStarDoneImageView = (ImageView) v.findViewById(R.id.one_star_done);

        mTwoStarButton = (Button) v.findViewById(R.id.compare_artwork_two_star_button);
        mTwoStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSortArtWorkActivity(2);
            }
        });

        mTwoStarDoneImageView = (ImageView) v.findViewById(R.id.two_star_done);

        mThreeStarButton = (Button) v.findViewById(R.id.compare_artwork_three_star_button);
        mThreeStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSortArtWorkActivity(3);
            }
        });

        mThreeStarDoneImageView = (ImageView) v.findViewById(R.id.three_star_done);

        mFourStarButton = (Button) v.findViewById(R.id.compare_artwork_four_star_button);
        mFourStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSortArtWorkActivity(4);
            }
        });

        mFourStarDoneImageView = (ImageView) v.findViewById(R.id.four_star_done);

        mFiveStarButton = (Button) v.findViewById(R.id.compare_artwork_five_star_button);
        mFiveStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSortArtWorkActivity(5);
            }
        });

        mFiveStarDoneImageView = (ImageView) v.findViewById(R.id.five_star_done);

        return v;
    }

    private void startSortArtWorkActivity(int starCount) {

        Intent intent = new Intent(getActivity(), SortArtWorkActivity.class);
        intent.putExtra(SortArtWorkActivity.EXTRA_STAR_COUNT, starCount);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStarButtons();
    }

    private void refreshStarButtons() {
        for (int i = 1; i <= 5; i++) {
            setStarButtonStatus(i);
        }
    }

    private void setStarButtonStatus(int numberOfStars) {

        Button starButton;
        String starButtonText;
        int starCount;

        switch (numberOfStars) {
            case 1:
                starButton = mOneStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.one_star));
                starButtonText = getResources()
                        .getString(R.string.compare_one_star_button_label, starCount);
                mOneStarDoneImageView.setVisibility(Gallery.get(getActivity()).isSorted(1) ? View.VISIBLE : View.INVISIBLE);
                break;

            case 2:
                starButton = mTwoStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.two_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_two_star_button_label, starCount);
                mTwoStarDoneImageView.setVisibility(Gallery.get(getActivity()).isSorted(2) ? View.VISIBLE : View.INVISIBLE);
                break;

            case 3:
                starButton = mThreeStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.three_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_three_star_button_label, starCount);
                mThreeStarDoneImageView.setVisibility(Gallery.get(getActivity()).isSorted(3) ? View.VISIBLE : View.INVISIBLE);
                break;

            case 4:
                starButton = mFourStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.four_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_four_star_button_label, starCount);
                mFourStarDoneImageView.setVisibility(Gallery.get(getActivity()).isSorted(4) ? View.VISIBLE : View.INVISIBLE);
                break;

            case 5:
                starButton = mFiveStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.five_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_five_star_button_label, starCount);
                mFiveStarDoneImageView.setVisibility(Gallery.get(getActivity()).isSorted(5) ? View.VISIBLE : View.INVISIBLE);
                break;

            default:
                LOG.warning(TAG + ": Invalid number of stars: " + numberOfStars);
                return;
        }

        starButton.setText(starButtonText);

        /* Enable the star button iff there are at least 2 artworks available */
        starButton.setEnabled(starCount >= 2);
        starButton.setAlpha(starButton.isEnabled() ? 1f : .5f);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mCompareArtWorkListener != null) {
            mCompareArtWorkListener.onCompareArtFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCompareArtFragmentInteractionListener) {
            mCompareArtWorkListener = (OnCompareArtFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCompareArtWorkListener = null;
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
    public interface OnCompareArtFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCompareArtFragmentInteraction(Uri uri);
    }
}
