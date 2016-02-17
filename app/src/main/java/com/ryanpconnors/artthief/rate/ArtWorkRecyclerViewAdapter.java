package com.ryanpconnors.artthief.rate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.rate.ArtWorkListFragment.OnArtWorkListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ArtWork} and makes a call to the
 * specified {@link OnArtWorkListFragmentInteractionListener}.
 */
public class ArtWorkRecyclerViewAdapter extends RecyclerView.Adapter<ArtWorkListHolder> {

    private final List<ArtWork> mArtWorks;
    private final OnArtWorkListFragmentInteractionListener mListener;

    public ArtWorkRecyclerViewAdapter(List<ArtWork> items, OnArtWorkListFragmentInteractionListener listener) {
        mArtWorks = items;
        mListener = listener;
    }

    @Override
    public ArtWorkListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artwork_list_item, parent, false);
        return new ArtWorkListHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ArtWorkListHolder holder, int position) {

        ArtWork artWork = mArtWorks.get(position);
        holder.bindArtWork(artWork, mListener);
    }

    @Override
    public int getItemCount() {
        return mArtWorks.size();
    }
}