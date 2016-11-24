package com.ryanpconnors.artthief.show;

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
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

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

    private ImageView mTopPickArtworkImageView;
    private TextView mTopPickShowIdTextView;
    private TextView mTopPickTitleTextView;
    private TextView mTopPickArtistTextView;
    private TextView mTopPickMediaTextView;
    private TextView mTopPickRatingTextView;
    private ImageView mTopPickRatingStarImageView;

    private EditText mIdEditText;
    private Button mTakenButton;

    private ImageView mCurrentArtworkImageView;
    private OnShowFragmentInteractionListener mListener;

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
             * that matches the suppied ID in mIdEditText iff it exists, assigns it to mCurrentArtwork and calls
             * `setCurrentArtworkImageView()` to apply the updated current artwork to the imageView.
             * If an artwork does not exist for the given ID in the Gallery, a Toast is shown to the user
             * and the ImageView is set to INVISIBLE.
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("")) {
                    mCurrentArtwork = Gallery.get(getActivity()).getArtWork(Integer.parseInt(s.toString()));
                    if (mCurrentArtwork == null) {
                        Toast.makeText(getActivity(), String.format(Locale.US, "Artwork [%s] Not Found", s), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    mCurrentArtwork = null;
                }
                setCurrentArtworkImageView();
                setTakenButton();
            }
        });

        mTakenButton = (Button) view.findViewById(R.id.mark_as_taken_button);
        mTakenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hide the keyboard if it is open
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (mCurrentArtwork != null) {
                    mCurrentArtwork.setTaken(!mCurrentArtwork.isTaken());
                    Gallery.get(getActivity()).updateArtWork(mCurrentArtwork); // reverse the current artworks `taken` status
                    Toast.makeText(getActivity(), String.format(Locale.US, "[ %d ] marked : %s", mCurrentArtwork.getArtThiefID(), mCurrentArtwork.isTaken() ? "Taken" : "Not Taken"), Toast.LENGTH_LONG).show();
                    setTopRatedArtwork(); //refresh the current top rated artwork
                    setTakenButton();
                }
            }
        });
        setTakenButton();

        mCurrentArtworkImageView = (ImageView) view.findViewById(R.id.current_artwork_image_view);
        mCurrentArtworkImageView.setVisibility(View.INVISIBLE);

        return view;
    }

    private void setTakenButton() {
        if (mCurrentArtwork == null) {
            mTakenButton.setClickable(false);
            mTakenButton.setAlpha(.5f);
        }
        else {
            mTakenButton.setText(mCurrentArtwork.isTaken() ? R.string.mark_not_taken : R.string.mark_taken);
            mTakenButton.setClickable(true);
            mTakenButton.setAlpha(1f);
        }
    }

    private void setCurrentArtworkImageView() {
        if (mCurrentArtwork == null) {
            mCurrentArtworkImageView.setVisibility(View.INVISIBLE);
        }
        else {
            String largeImagePath = mCurrentArtwork.getLargeImagePath();
            if (largeImagePath != null) {
                Bitmap largeArtworkImage = Gallery.get(getActivity()).getArtWorkImage(largeImagePath);
                mCurrentArtworkImageView.setImageBitmap(largeArtworkImage);
                mCurrentArtworkImageView.setVisibility(View.VISIBLE);
            }
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
