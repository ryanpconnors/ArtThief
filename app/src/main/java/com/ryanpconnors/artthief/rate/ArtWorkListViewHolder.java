package com.ryanpconnors.artthief.rate;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.rate.ArtWorkListFragment.OnArtWorkListFragmentInteractionListener;

/**
 * Created by Ryan Connors on 2/17/16.
 */
public class ArtWorkListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mArtWorkImageView;
    private TextView mShowIdTextView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private TextView mMediaTextView;

    private OnArtWorkListFragmentInteractionListener mListener;

    private ArtWork mArtWork;

    public ArtWorkListViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

//        mArtWorkImageView = (ImageView) itemView.findViewById(R.id.artwork_list_item_artwork_image);
        mShowIdTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_show_id);
        mTitleTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_title);
        mArtistTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_artist);
        mMediaTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_media);
    }

    public void bindArtWork(ArtWork artWork, OnArtWorkListFragmentInteractionListener listener) {
        mArtWork = artWork;

        //TODO implement proper imageView here

        mShowIdTextView.setText("(" + artWork.getShowId() + ")");
        mTitleTextView.setText(artWork.getTitle());
        mArtistTextView.setText(artWork.getArtist());
        mMediaTextView.setText(artWork.getMedia());

        mListener = listener;
    }

    public void onClick(View view) {

        if (mListener != null) {

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onArtWorkListFragmentInteraction(mArtWork);
        }

        // TODO implement onClick to start a new ArtWorkDetailActivity to show the detailed view of the artwork
        //  Intent intent = ArtWorkDetailActivity.newIntent(getActivity(), mArtWork.getId());
        //  startActivity(intent);
    }
}
