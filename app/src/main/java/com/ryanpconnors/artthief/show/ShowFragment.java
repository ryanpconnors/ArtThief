package com.ryanpconnors.artthief.show;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowFragment.OnShowFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowFragment extends Fragment {

    private ImageView mTopPickArtworkImageView;
    private TextView mTopPickShowIdTextView;
    private TextView mTopPickTitleTextView;
    private TextView mTopPickArtistTextView;
    private TextView mTopPickMediaTextView;
    private TextView mTopPickRatingTextView;
    private ImageView mTopPickRatingStarImageView;

    private EditText mIdEditText;
    private Button mTakenButton;

    private ImageView mArtworkImageView;
    private OnShowFragmentInteractionListener mListener;


    public ShowFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowFragment.
     */
    public static ShowFragment newInstance() {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_the_show, container, false);

        // Initialize top rated Artwork view objects
        mTopPickArtworkImageView = (ImageView) view.findViewById(R.id.top_pick_artwork_image);
        mTopPickShowIdTextView = (TextView) view.findViewById(R.id.top_pick_show_id);
        mTopPickTitleTextView = (TextView) view.findViewById(R.id.top_pick_title);
        mTopPickArtistTextView = (TextView) view.findViewById(R.id.top_pick_artist);
        mTopPickMediaTextView = (TextView) view.findViewById(R.id.top_pick_media);
        mTopPickRatingTextView = (TextView) view.findViewById(R.id.top_pick_rating_text_view);
        mTopPickRatingStarImageView = (ImageView) view.findViewById(R.id.top_pick_rating_star_image_view);

        // Initialize ShowFragment view objects
        mIdEditText = (EditText) view.findViewById(R.id.id_edit_text);
        mTakenButton = (Button) view.findViewById(R.id.mark_as_taken_button);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTopRatedArtwork();
    }

    private void setTopRatedArtwork() {
        ArtWork topRatedArtwork = Gallery.get(getActivity()).getTopPickArtwork();
        if (topRatedArtwork == null) {
            Toast.makeText(getActivity(), "Rate Your Favorite Artworks", Toast.LENGTH_LONG).show();
        }
        else {
            String smallImagePath = topRatedArtwork.getSmallImagePath();
            if (smallImagePath != null) {
                Bitmap smallArtWorkImage = Gallery.get(getActivity()).getArtWorkImage(smallImagePath);
                mTopPickArtworkImageView.setImageBitmap(smallArtWorkImage);
            }
            mTopPickShowIdTextView.setText("(" + topRatedArtwork.getShowId() + ")");
            mTopPickTitleTextView.setText(topRatedArtwork.getTitle());
            mTopPickArtistTextView.setText(topRatedArtwork.getArtist());
            mTopPickMediaTextView.setText(topRatedArtwork.getMedia());

            if (topRatedArtwork.getStars() > 0) {
                mTopPickRatingStarImageView.setImageResource(R.drawable.ic_star_border_black_18dp);
                mTopPickRatingTextView.setText(Integer.toString(topRatedArtwork.getStars()));
                mTopPickRatingStarImageView.setVisibility(View.VISIBLE);
                mTopPickRatingTextView.setVisibility(View.VISIBLE);
            }
            else {
                mTopPickRatingStarImageView.setVisibility(View.GONE);
                mTopPickRatingTextView.setVisibility(View.GONE);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onShowFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShowFragmentInteractionListener) {
            mListener = (OnShowFragmentInteractionListener) context;
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
    public interface OnShowFragmentInteractionListener {
        // TODO: Update argument type and name
        void onShowFragmentInteraction(Uri uri);
    }

}
