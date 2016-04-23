package com.ryanpconnors.artthief.compare;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.Gallery;

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
    private Button mFiveStarButton;
    private Button mFourStarButton;
    private Button mThreeStarButton;
    private Button mTwoStarButton;
    private Button mOneStarButton;
    private OnCompareArtFragmentInteractionListener mCompareArtWorkListener;

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
                Toast.makeText(getActivity().getApplicationContext(), "One Star:" +
                                Gallery.get(getActivity()).getStarCount(getString(R.string.one_star)),
                        Toast.LENGTH_LONG).show();
            }
        });

        mTwoStarButton = (Button) v.findViewById(R.id.compare_artwork_two_star_button);
        mTwoStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Two Star:" +
                                Gallery.get(getActivity()).getStarCount(getString(R.string.two_stars)),
                        Toast.LENGTH_LONG).show();
            }
        });

        mThreeStarButton = (Button) v.findViewById(R.id.compare_artwork_three_star_button);
        mThreeStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Three Star:" +
                                Gallery.get(getActivity()).getStarCount(getString(R.string.three_stars)),
                        Toast.LENGTH_LONG).show();
            }
        });

        mFourStarButton = (Button) v.findViewById(R.id.compare_artwork_four_star_button);
        mFourStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Four Star:" +
                                Gallery.get(getActivity()).getStarCount(getString(R.string.four_stars)),
                        Toast.LENGTH_LONG).show();
            }
        });

        mFiveStarButton = (Button) v.findViewById(R.id.compare_artwork_five_star_button);
        mFiveStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Five Star:" +
                                Gallery.get(getActivity()).getStarCount(getString(R.string.five_stars)),
                        Toast.LENGTH_LONG).show();
            }
        });

        return v;
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
                break;

            case 2:
                starButton = mTwoStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.two_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_two_star_button_label, starCount);
                break;

            case 3:
                starButton = mThreeStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.three_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_three_star_button_label, starCount);
                break;

            case 4:
                starButton = mFourStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.four_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_four_star_button_label, starCount);
                break;

            case 5:
                starButton = mFiveStarButton;
                starCount = Gallery.get(getActivity()).getStarCount(getString(R.string.five_stars));
                starButtonText = getResources()
                        .getString(R.string.compare_five_star_button_label, starCount);
                break;

            default:
                LOG.warning(TAG + ": Invalid number of stars: " + numberOfStars);
                return;
        }

        starButton.setText(starButtonText);

        if (starCount > 0) {
            starButton.setEnabled(true);
            starButton.setAlpha(1f);
        } else {
            starButton.setEnabled(starCount > 0);
            starButton.setAlpha(.5f);
        }

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
        } else {
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
