package com.ryanpconnors.artthief.compare;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.Gallery;

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
            // store given arguments in this class instance
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compare_art, container, false);

        // Setup the compare artwork star buttons
        mFiveStarButton = (Button) v.findViewById(R.id.compare_artwork_five_star_button);
        int fiveStarCount = Gallery.get(getActivity()).getStarCount(getString(R.string.five_stars));
        String fiveStarButtonText = getResources()
                .getString(R.string.compare_five_star_button_label, fiveStarCount);
        mFiveStarButton.setText(fiveStarButtonText);

        if (fiveStarCount > 0) {
            mFiveStarButton.setActivated(true);
            mFiveStarButton.setClickable(true);
        } else {
            mFiveStarButton.setActivated(false);
            mFiveStarButton.setClickable(false);
        }
        mFiveStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO setup onClick for star buttons
            }
        });

        //TODO setup other star buttons


        return v;
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
