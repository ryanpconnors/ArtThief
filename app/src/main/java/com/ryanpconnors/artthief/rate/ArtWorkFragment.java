package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

import java.io.File;
import java.util.UUID;


/**
 * Created by Ryan Connors on 2/14/16.
 */
public class ArtWorkFragment extends Fragment {

    private ArtWork mArtWork;

    private ImageView mArtWorkLargeImageView;
    private RatingBar mArtworkRatingBar;
    private TextView mArtWorkTitleTextView;
    private TextView mArtWorkArtistTextView;
    private TextView mArtworkMediaTextView;

    private OnArtWorkFragmentInteractionListener mListener;

    private ShareActionProvider mShareActionProvider;

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
        setHasOptionsMenu(true);
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
                Toast.makeText(getActivity(),
                        String.valueOf(ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mArtWorkTitleTextView = (TextView) v.findViewById(R.id.artwork_title);
        mArtWorkTitleTextView.setText(mArtWork.getTitle());

        mArtWorkArtistTextView = (TextView) v.findViewById(R.id.artwork_artist);
        mArtWorkArtistTextView.setText(mArtWork.getArtist());

        mArtworkMediaTextView = (TextView) v.findViewById(R.id.artwork_media);
        mArtworkMediaTextView.setText(mArtWork.getMedia());

        setShareIntent();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_artwork, menu);

        // Setup ShareActionProvider menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent();
        mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.getActivity().onBackPressed();
                return true;

            case R.id.menu_item_share:
                // TODO why does this not show up here?

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Call to update the share intent
    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");

            File file = new File(mArtWork.getLargeImagePath());
            Uri imageUri = Uri.fromFile(file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)  + " : " +  mArtWork.getTitle());
            mShareActionProvider.setShareIntent(shareIntent);
        }
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
