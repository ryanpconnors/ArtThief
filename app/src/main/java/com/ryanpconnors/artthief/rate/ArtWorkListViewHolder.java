package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;
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
    private ImageView mArtWorkRatingStarImageView;
    private TextView mArtWorkRatingTextView;

    private OnArtWorkListFragmentInteractionListener mListener;

    private ArtWork mArtWork;

    public ArtWorkListViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mArtWorkImageView = (ImageView) itemView.findViewById(R.id.artwork_list_item_artwork_image);
        mShowIdTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_show_id);
        mTitleTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_title);
        mArtistTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_artist);
        mMediaTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_media);
        mArtWorkRatingStarImageView = (ImageView) itemView.findViewById(R.id.artwork_list_item_rating_star_image);
        mArtWorkRatingTextView = (TextView) itemView.findViewById(R.id.artwork_list_item_rating_text_view);
    }

    public void bindArtWork(ArtWork artWork,
                            OnArtWorkListFragmentInteractionListener listener,
                            Context context) {
        mArtWork = artWork;

        //TODO: The application may be doing too much work on its main thread.
        String smallImagePath = mArtWork.getSmallImagePath();
        if (smallImagePath != null) {
            Bitmap smallArtWorkImage = Gallery.get(context).getArtWorkImage(smallImagePath);
            mArtWorkImageView.setImageBitmap(smallArtWorkImage);
        }

        // mShowIdTextView.setText("(" + artWork.getShowId() + ")");
        mShowIdTextView.setText("(" + artWork.getOrdering() + ")");
        mTitleTextView.setText(artWork.getTitle());
        mArtistTextView.setText(artWork.getArtist());
        mMediaTextView.setText(artWork.getMedia());

        if (mArtWork.getStars() > 0) {
            mArtWorkRatingStarImageView.setImageResource(R.drawable.ic_star_border_black_18dp);
            mArtWorkRatingTextView.setText(Integer.toString(mArtWork.getStars()));
            mArtWorkRatingStarImageView.setVisibility(View.VISIBLE);
            mArtWorkRatingTextView.setVisibility(View.VISIBLE);
        }
        else {
            mArtWorkRatingStarImageView.setVisibility(View.GONE);
            mArtWorkRatingTextView.setVisibility(View.GONE);
        }

        mListener = listener;
    }

    @Override
    public void onClick(View view) {

        if (mListener != null) {

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onArtWorkListFragmentInteraction(mArtWork);
        }
    }
}
