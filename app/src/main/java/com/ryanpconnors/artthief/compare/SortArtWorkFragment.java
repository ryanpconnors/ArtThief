package com.ryanpconnors.artthief.compare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ryanpconnors.artthief.compare.SortArtWorkFragment.ArtworkChoice.ALPHA;
import static com.ryanpconnors.artthief.compare.SortArtWorkFragment.ArtworkChoice.BETA;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SortArtWorkFragment.OnSortArtworkFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SortArtWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortArtWorkFragment extends Fragment {

    private static final String TAG = "SortArtWorkFragment";
    private static final String ARG_NUMBER_OF_STARS = "number_of_stars";

    private List<ArtWork> mArtWorks;

    private int mRating;

    private int mCurrentIndex = 0;

    private ImageView mArtworkImageViewAlpha;
    private ImageView mArtworkImageViewBeta;

    private ShareActionProvider mShareActionProvider;
    private OnSortArtworkFragmentInteractionListener mListener;

    /**
     * ArtworkChoice enumerated type used for identifying the user's choice
     * of which artwork they prefer for sorting purposes
     */
    protected enum ArtworkChoice {
        ALPHA,
        BETA
    }

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
            mRating = getArguments().getInt(ARG_NUMBER_OF_STARS);
            Gallery.get(getActivity()).setSorted(Math.round(mRating), false);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sort_art_work, container, false);

        // Add toolbar and up button navigation
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((SortArtWorkActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((SortArtWorkActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        // Initialize ArtWork Image Views
        mArtworkImageViewAlpha = (ImageView) v.findViewById(R.id.artwork_large_image_view_alpha);
        mArtworkImageViewAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortArtwork(ALPHA);
            }
        });

        mArtworkImageViewBeta = (ImageView) v.findViewById(R.id.artwork_large_image_view_beta);
        mArtworkImageViewBeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortArtwork(BETA);
            }
        });

        mArtWorks = Gallery.get(getActivity()).getArtWorks(mRating, false);
        normalizeArtworkOrdering();

        displayArtwork(mArtworkImageViewAlpha, mCurrentIndex);
        displayArtwork(mArtworkImageViewBeta, mCurrentIndex + 1);
        return v;
    }

    /**
     * @param choice
     */
    private void sortArtwork(ArtworkChoice choice) {
        if (choice.equals(ALPHA)) {
            swapOrderingAlphaBeta(mCurrentIndex, mCurrentIndex + 1);
            if (mCurrentIndex > 0) {
                mCurrentIndex -= 1;
            }
            else if (mCurrentIndex < mArtWorks.size() - 2) {
                mCurrentIndex += 1;
            }
            else {
                Gallery.get(getActivity()).setSorted(Math.round(mRating), true);
                Toast.makeText(getActivity(), R.string.artworks_sorted, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
        else if (choice.equals(BETA)) {
            if (mCurrentIndex >= mArtWorks.size() - 2) {
                Gallery.get(getActivity()).setSorted(Math.round(mRating), true);
                Toast.makeText(getActivity(), R.string.artworks_sorted, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            else {
                mCurrentIndex += 1;
            }
        }

        displayArtwork(mArtworkImageViewAlpha, mCurrentIndex);
        displayArtwork(mArtworkImageViewBeta, mCurrentIndex + 1);
    }

    /**
     * Sorts and normalizes the ordering of each artwork in mArtWorks
     * such that ordering is from 0, 1, ..., N
     */
    private void normalizeArtworkOrdering() {
        if (mArtWorks.isEmpty()) {
            return;
        }

        for (int i = 0; i < mArtWorks.size(); i++) {
            Collections.sort(mArtWorks);
            ArtWork artWork = mArtWorks.get(i);
            if (artWork.getOrdering() != i) {
                artWork.setOrdering(i);
                Gallery.get(getActivity()).updateArtWork(artWork);
            }
        }
    }

    /**
     * Swaps the ORDERING field of the alpha and beta artworks
     * Swaps the two artworks in the local mArtWorks
     * Updates the changes in the database
     */
    private void swapOrderingAlphaBeta(int alpha, int beta) {
        ArtWork artWorkAlpha = mArtWorks.get(alpha);
        ArtWork artWorkBeta = mArtWorks.get(beta);

        artWorkAlpha.swapOrder(artWorkBeta);
        Collections.swap(mArtWorks, alpha, beta);

        Gallery.get(getActivity()).updateArtWork(artWorkAlpha);
        Gallery.get(getActivity()).updateArtWork(artWorkBeta);
    }


    /**
     * @param imageView
     * @param index
     */
    private void displayArtwork(ImageView imageView, int index) {
        if (index < 0 || index >= mArtWorks.size()) {
            Log.d(TAG, "loadArtwork called with invalid index : " + index);
            throw new IndexOutOfBoundsException();
        }
        String largeImagePath = mArtWorks.get(index).getLargeImagePath();
        if (largeImagePath != null) {
            Bitmap largeArtWorkImage = Gallery.get(getActivity()).getArtWorkImage(largeImagePath);
            imageView.setImageBitmap(largeArtWorkImage);
        }
        else {
            // TODO: Handle the case where the artwork has no image
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_artwork, menu);

        // Setup ShareActionProvider menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        new setShareIntentTask().execute();
        mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                startActivity(intent);
                return true;
            }
        });
    }

    /**
     * Asynchronous task for setting this ArtWorkFragments share intent
     */
    private class setShareIntentTask extends AsyncTask<Void, Void, Void> {

        Intent shareIntent;

        @Override
        protected Void doInBackground(Void... params) {
            shareIntent = getShareIntent();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isAdded() && mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

    /**
     * Create a ShareIntent of both ArtWorks
     *
     * @return ShareIntent for both artworks to send to another application
     * to allow the user to share the two current artworks to get another option on which
     * one they prefer.
     */
    private Intent getShareIntent() {
        Bitmap bitmapAlpha;
        Bitmap bitmapBeta;

        ArtWork artWorkAlpha = mArtWorks.get(mCurrentIndex);
        ArtWork artWorkBeta = mArtWorks.get(mCurrentIndex + 1);
        File imgFileAlpha = new File(artWorkAlpha.getLargeImagePath());
        File imgFileBeta = new File(artWorkBeta.getLargeImagePath());

        if (imgFileAlpha.exists() && imgFileBeta.exists()) {
            bitmapAlpha = BitmapFactory.decodeFile(imgFileAlpha.getAbsolutePath());
            bitmapBeta = BitmapFactory.decodeFile(imgFileBeta.getAbsolutePath());
        }
        else {
            mShareActionProvider = null;
            return null;
        }
        try {
            File fileAlpha = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), artWorkAlpha.getTitle() + ".png");
            File fileBeta = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), artWorkBeta.getTitle() + ".png");
            FileOutputStream outAlpha = new FileOutputStream(fileAlpha);
            FileOutputStream outBeta = new FileOutputStream(fileBeta);
            bitmapAlpha.compress(Bitmap.CompressFormat.PNG, 90, outAlpha);
            bitmapBeta.compress(Bitmap.CompressFormat.PNG, 90, outBeta);
            outAlpha.close();
            outBeta.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            mShareActionProvider = null;
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            mShareActionProvider = null;
            return null;
        }

        if (isAdded()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("image/*");

            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(Uri.fromFile(imgFileAlpha));
            uris.add(Uri.fromFile(imgFileBeta));

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.prefer_text));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return shareIntent;
        }
        else {
            return null;
        }
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
        }
        else {
            throw new RuntimeException(String.format("%s must implement OnFragmentInteractionListener", context.toString()));
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
