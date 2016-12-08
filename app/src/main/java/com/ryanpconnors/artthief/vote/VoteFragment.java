package com.ryanpconnors.artthief.vote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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

    private ProgressDialog mProgressDialog;

    private String mTicketCode;

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
        mTicketCode = ticketCode;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.cast_your_vote))
                .setMessage(getString(R.string.transmit_vote_message))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JSONObject votePackage = getVotingPackage(mTicketCode);
                        new VotePostTask().execute(votePackage);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
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
                    artworkJson.put(getString(R.string.vote_package_art_thief_id), artWork.getArtThiefID());
                    artworkJson.put(getString(R.string.vote_package_show_id), artWork.getShowId());
                    artworkJson.put(getString(R.string.vote_package_rating), artWork.getStars());
                    list.put(artworkJson);
                }
                catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        try {
            jsonPackage.put(getString(R.string.vote_package_list), list);
            jsonPackage.put(getString(R.string.vote_package_uuid), UUID.randomUUID().toString());
            jsonPackage.put(getString(R.string.vote_package_scan_data), ticketCode);
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonPackage;
    }

    private class VotePostTask extends AsyncTask<JSONObject, String, Boolean> {

        JSONObject voteJson;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.casting_vote));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(JSONObject... args) {

            voteJson = args[0];

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(getString(R.string.vote_url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out, voteJson.toString());

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException(urlConnection.getResponseMessage() + ": with " + url);
                }
            }
            catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(success ? getString(R.string.success) : getString(R.string.error))
                    .setMessage(success ? getString(R.string.vote_success) : getString(R.string.vote_error))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Dismiss the AlertDialog
                        }
                    });
            builder.create();
            builder.show();

            mScanTicketButton.setEnabled(!success);
            mScanTicketButton.setAlpha(success ? 0.5f : 1.0f);
        }
    }

    private void readStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(String.format("%s\n", line));
        }
        br.close();
        System.out.println("" + sb.toString());
    }

    private void writeStream(OutputStream out, String data) {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write(data);
            writer.flush();
        }
        catch (IOException e) {
            Log.e(TAG, e.toString());
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
    public interface OnVoteFragmentInteractionListener {

        void onScanTicketButtonClick();
    }

}
