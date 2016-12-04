package com.ryanpconnors.artthief.vote;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoteFragment.OnVoteFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoteFragment extends Fragment {

    public static final String TAG = "VoteFragment";

    private OnVoteFragmentInteractionListener mListener;
    private Button mScanTicketButton;

    public VoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VoteFragment.
     */
    public static VoteFragment newInstance() {
        VoteFragment fragment = new VoteFragment();

        // For arguments passed into the new instance
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // Do something with args
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        mScanTicketButton = (Button) view.findViewById(R.id.scan_ticket_button);
        mScanTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onScanTicketButtonClick();
            }
        });
        return view;
    }

    /**
     * @param ticketCode
     */
    public void vote(String ticketCode) {

        Toast.makeText(getActivity(), "TicketCode : " + ticketCode, Toast.LENGTH_SHORT).show();
        JSONObject votePackage = getVotingPackage(ticketCode);
    }

    public void sendVote() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVoteFragmentInteractionListener) {
            mListener = (OnVoteFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * @param ticketCode String
     * @return
     */
    private JSONObject getVotingPackage(String ticketCode) {

        JSONObject jsonPackage = new JSONObject();
        JSONArray list = new JSONArray();

        for (int i = 5; i >= 1; i--) {
            List<ArtWork> artworks = Gallery.get(getActivity()).getArtWorks(i, getString(R.string.descending));

            for (ArtWork artWork : artworks) {
                JSONObject artworkJson = new JSONObject();
                try {
                    artworkJson.put(getString(R.string.package_art_thief_id), artWork.getArtThiefID());
                    artworkJson.put(getString(R.string.package_show_id), artWork.getShowId());
                    artworkJson.put(getString(R.string.package_rating), artWork.getStars());
                    list.put(artworkJson);
                }
                catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        try {
            jsonPackage.put(getString(R.string.package_list), list);
            jsonPackage.put(getString(R.string.package_uuid), UUID.randomUUID().toString());
            jsonPackage.put(getString(R.string.package_scan_data), ticketCode);
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonPackage;
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
    public interface OnVoteFragmentInteractionListener {

        void onScanTicketButtonClick();
    }

}
