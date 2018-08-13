package com.ryanpconnors.art_thief.rate

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery
import com.ryanpconnors.art_thief.rate.ArtWorkListFragment.OnArtWorkListFragmentInteractionListener

import java.util.Locale

/**
 * Created by Ryan Connors on 2/17/16.
 */
class ArtWorkListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val mArtWorkImageView: ImageView
    private val mArtworkTakenButton: Button
    private val mShowIdTextView: TextView
    private val mTitleTextView: TextView
    private val mArtistTextView: TextView
    private val mMediaTextView: TextView
    private val mArtWorkRatingStarImageView: ImageView
    private val mArtWorkRatingTextView: TextView

    private var mListener: OnArtWorkListFragmentInteractionListener? = null

    private var mArtWork: ArtWork? = null

    init {
        itemView.setOnClickListener(this)

        mArtWorkImageView = itemView.findViewById<View>(R.id.artwork_list_item_artwork_image) as ImageView
        mArtworkTakenButton = itemView.findViewById<View>(R.id.artwork_list_item_taken_button) as Button
        mShowIdTextView = itemView.findViewById<View>(R.id.artwork_list_item_show_id) as TextView
        mTitleTextView = itemView.findViewById<View>(R.id.artwork_list_item_title) as TextView
        mArtistTextView = itemView.findViewById<View>(R.id.artwork_list_item_artist) as TextView
        mMediaTextView = itemView.findViewById<View>(R.id.artwork_list_item_media) as TextView
        mArtWorkRatingStarImageView = itemView.findViewById<View>(R.id.artwork_list_item_rating_star_image) as ImageView
        mArtWorkRatingTextView = itemView.findViewById<View>(R.id.artwork_list_item_rating_text_view) as TextView
    }

    fun bindArtWork(artWork: ArtWork,
                    listener: OnArtWorkListFragmentInteractionListener,
                    context: Context) {
        mArtWork = artWork

        //TODO: The application may be doing too much work on its main thread.
        val smallImagePath = mArtWork!!.smallImagePath
        if (smallImagePath != null) {
            val smallArtWorkImage = Gallery.get(context).getArtWorkImage(smallImagePath)
            mArtWorkImageView.setImageBitmap(smallArtWorkImage)
        }

        mArtworkTakenButton.visibility = if (artWork.isTaken) View.VISIBLE else View.INVISIBLE

        mShowIdTextView.text = if (artWork.showId == "x") "*" else "(" + artWork.showId + ")"
        mTitleTextView.text = artWork.title
        mArtistTextView.text = artWork.artist
        mMediaTextView.text = if (mArtWork!!.height!!.isEmpty() && mArtWork!!.width!!.isEmpty())
            mArtWork!!.media
        else
            String.format("%s (%s\" x %s\")", mArtWork!!.media, mArtWork!!.width, mArtWork!!.height)

        if (mArtWork!!.stars > 0) {
            mArtWorkRatingStarImageView.setImageResource(R.drawable.ic_star_border_black_18dp)
            mArtWorkRatingTextView.text = String.format(Locale.US, "%s", Integer.toString(mArtWork!!.stars))
            mArtWorkRatingStarImageView.visibility = View.VISIBLE
            mArtWorkRatingTextView.visibility = View.VISIBLE
        } else {
            mArtWorkRatingStarImageView.visibility = View.GONE
            mArtWorkRatingTextView.visibility = View.GONE
        }

        mListener = listener
    }

    override fun onClick(view: View) {

        if (mListener != null) {

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener!!.onArtWorkListFragmentInteraction(mArtWork)
        }
    }
}
