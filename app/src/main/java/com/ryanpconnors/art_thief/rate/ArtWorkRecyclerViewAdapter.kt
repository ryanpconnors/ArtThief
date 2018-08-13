package com.ryanpconnors.art_thief.rate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.rate.ArtWorkListFragment.OnArtWorkListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [ArtWork] and makes a call to the
 * specified [OnArtWorkListFragmentInteractionListener].
 */
class ArtWorkRecyclerViewAdapter(private var mArtWorks: List<ArtWork>?, private val mListener: OnArtWorkListFragmentInteractionListener) : RecyclerView.Adapter<ArtWorkListViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtWorkListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_artwork_list_item, parent, false)
        mContext = parent.context
        return ArtWorkListViewHolder(view)
    }

    /**
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ArtWorkListViewHolder, position: Int) {

        val artWork = mArtWorks!![position]
        holder.bindArtWork(artWork, mListener, mContext)
    }

    fun updateArtWorks(artWorks: List<ArtWork>) {
        mArtWorks = artWorks
    }

    override fun getItemCount(): Int {
        return mArtWorks!!.size
    }
}
