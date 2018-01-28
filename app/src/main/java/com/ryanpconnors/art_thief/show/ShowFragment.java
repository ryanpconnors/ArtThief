package com.ryanpconnors.art_thief.show;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryanpconnors.art_thief.R;
import com.ryanpconnors.art_thief.artgallery.ArtWork;
import com.ryanpconnors.art_thief.artgallery.Gallery;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowFragment.OnShowFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowFragment extends Fragment {

    private OnShowFragmentInteractionListener mListener;

    private ImageView mTopPickArtworkImageView;
    private TextView mTopPickShowIdTextView;
    private TextView mTopPickTitleTextView;
    private TextView mTopPickArtistTextView;
    private TextView mTopPickMediaTextView;
    private TextView mTopPickRatingTextView;
    private ImageView mTopPickRatingStarImageView;
    private TextView mTopPickEmptyText;

    private EditText mIdEditText;
    private Button mMarkTakenButton;
    private Button mTakenButton;

    private ImageView mCurrentArtworkImageView;
    private TextView mCurrentArtworkTextView;
    private ArtWork mCurrentArtwork;


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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
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
        mTopPickEmptyText = (TextView) view.findViewById(R.id.top_pick_empty_text);

        // Initialize ShowFragment view objects
        mIdEditText = (EditText) view.findViewById(R.id.id_edit_text);
        mIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing here
            }

            /**
             * Called when user changes the text in mIdEditText. Gets the current artwork from the Gallery
             * that matches the supplied ID in mIdEditText iff it exists, assigns it to mCurrentArtwork and calls
             * `setCurrentArtworkImageView()` to apply the updated current artwork to the imageView.
             * If an artwork does not exist for the given ID in the Gallery, the taken button is displayed with text
             * alerting the user that artowrk for the given ID was not found.
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().matches("")) {
                    mCurrentArtwork = Gallery.get(getActivity()).getArtWork(s.toString());

                    if (mCurrentArtwork == null) {

                        mCurrentArtworkImageView.setImageBitmap(null);
                        mCurrentArtworkTextView.setVisibility(View.INVISIBLE);

                        mTakenButton.setText(String.format(Locale.US, getString(R.string.artwork_not_found), s.toString()));
                        mTakenButton.setVisibility(View.VISIBLE);

                        setTakenButton();
                        return;
                    }
                }
                else {
                    mCurrentArtwork = null;
                    mTakenButton.setVisibility(View.INVISIBLE);
                }
                setCurrentArtworkImageView();
                setTakenButton();
            }
        });

        mTakenButton = (Button) view.findViewById(R.id.taken_button);
        mTakenButton.setVisibility(View.INVISIBLE);

        mMarkTakenButton = (Button) view.findViewById(R.id.mark_as_taken_button);
        mMarkTakenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hide the keyboard if it is open
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (mCurrentArtwork != null) {
                    mCurrentArtwork.setTaken(!mCurrentArtwork.isTaken());
                    Gallery.get(getActivity()).updateArtWork(mCurrentArtwork); // reverse the current artworks `taken` status
                    setCurrentArtworkImageView();
                    setTopRatedArtwork(); //refresh the current top rated artwork
                    setTakenButton();
                }
            }
        });
        setTakenButton();

        mCurrentArtworkImageView = (ImageView) view.findViewById(R.id.current_artwork_image_view);
        mCurrentArtworkTextView = (TextView) view.findViewById(R.id.current_artwork_text_view);

        return view;
    }

    private void setTakenButton() {

        if (mCurrentArtwork == null) {
            mMarkTakenButton.setClickable(false);
            mMarkTakenButton.setAlpha(.5f);
        }
        else {
            mMarkTakenButton.setText(mCurrentArtwork.isTaken() ? R.string.mark_not_taken : R.string.mark_taken);
            mMarkTakenButton.setClickable(true);
            mMarkTakenButton.setAlpha(1f);

            mTakenButton.setText(String.format(Locale.US, getString(R.string.artwork_taken), mCurrentArtwork.getShowId()));
            mTakenButton.setVisibility(mCurrentArtwork.isTaken() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     *
     */
    private void setCurrentArtworkImageView() {

        if (mCurrentArtwork == null) {
            mCurrentArtworkTextView.setText(R.string.enter_artwork_id);
            mCurrentArtworkTextView.setVisibility(View.VISIBLE);
            mCurrentArtworkImageView.setImageBitmap(null);
            return;
        }

        String largeImagePath = mCurrentArtwork.getLargeImagePath();
        if (largeImagePath != null) {
                Bitmap largeArtworkImage = Gallery.get(getActivity()).getArtWorkImage(largeImagePath);
                mCurrentArtworkImageView.setImageBitmap(largeArtworkImage);
        }
        else {
            mCurrentArtworkImageView.setImageBitmap(null);
            mTakenButton.setText(String.format(Locale.US, getString(R.string.artwork_image_not_found), mCurrentArtwork.getShowId()));
            mTakenButton.setVisibility(View.VISIBLE);
            mCurrentArtworkTextView.setVisibility(View.INVISIBLE);
        }

        if (mCurrentArtwork.isTaken()) {
            mTakenButton.setText(String.format(Locale.US, getString(R.string.artwork_taken), mCurrentArtwork.getShowId()));
            mTakenButton.setVisibility(View.VISIBLE);
            mCurrentArtworkTextView.setVisibility(View.INVISIBLE);
        }
        else {
            mCurrentArtworkTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setTopRatedArtwork();
    }

    private void setTopRatedArtwork() {
        ArtWork topRatedArtwork = Gallery.get(getActivity()).getTopPickArtwork();
        if (topRatedArtwork == null) {
            mTopPickArtworkImageView.setVisibility(View.INVISIBLE);
            mTopPickShowIdTextView.setVisibility(View.INVISIBLE);
            mTopPickTitleTextView.setVisibility(View.INVISIBLE);
            mTopPickArtistTextView.setVisibility(View.INVISIBLE);
            mTopPickMediaTextView.setVisibility(View.INVISIBLE);
            mTopPickRatingTextView.setVisibility(View.INVISIBLE);
            mTopPickRatingStarImageView.setVisibility(View.INVISIBLE);
            mTopPickEmptyText.setVisibility(View.VISIBLE);
        }
        else {
            String smallImagePath = topRatedArtwork.getSmallImagePath();
            if (smallImagePath != null) {
                Bitmap smallArtWorkImage = Gallery.get(getActivity()).getArtWorkImage(smallImagePath);
                mTopPickArtworkImageView.setImageBitmap(smallArtWorkImage);
            }
            mTopPickShowIdTextView.setText(topRatedArtwork.getShowId().equals("x") ? "*" : "(" + topRatedArtwork.getShowId() + ")");
            mTopPickTitleTextView.setText(topRatedArtwork.getTitle());
            mTopPickArtistTextView.setText(topRatedArtwork.getArtist());
            mTopPickMediaTextView.setText(topRatedArtwork.getMedia());

            if (topRatedArtwork.getStars() > 0) {
                mTopPickRatingStarImageView.setImageResource(R.drawable.ic_star_border_black_18dp);
                mTopPickRatingTextView.setText(String.format(Locale.US, "%s", Integer.toString(topRatedArtwork.getStars())));
                mTopPickRatingStarImageView.setVisibility(View.VISIBLE);
                mTopPickRatingTextView.setVisibility(View.VISIBLE);
            }
            else {
                mTopPickRatingStarImageView.setVisibility(View.GONE);
                mTopPickRatingTextView.setVisibility(View.GONE);
            }
            mTopPickArtworkImageView.setVisibility(View.VISIBLE);
            mTopPickShowIdTextView.setVisibility(View.VISIBLE);
            mTopPickTitleTextView.setVisibility(View.VISIBLE);
            mTopPickArtistTextView.setVisibility(View.VISIBLE);
            mTopPickMediaTextView.setVisibility(View.VISIBLE);
            mTopPickRatingTextView.setVisibility(View.VISIBLE);
            mTopPickRatingStarImageView.setVisibility(View.VISIBLE);
            mTopPickEmptyText.setVisibility(View.INVISIBLE);
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
