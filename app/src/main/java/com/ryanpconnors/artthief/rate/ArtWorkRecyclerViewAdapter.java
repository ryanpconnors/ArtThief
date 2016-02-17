package com.ryanpconnors.artthief.rate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.rate.ArtWorkListFragment.OnArtWorkListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ArtWork} and makes a call to the
 * specified {@link OnArtWorkListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ArtWorkRecyclerViewAdapter extends RecyclerView.Adapter<ArtWorkRecyclerViewAdapter.ViewHolder> {

    private final List<ArtWork> mValues;
    private final OnArtWorkListFragmentInteractionListener mListener;

    public ArtWorkRecyclerViewAdapter(List<ArtWork> items, OnArtWorkListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artwork_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //TODO rewrite this method for displaying ArtWork
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getArtist());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onArtWorkListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ArtWork mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
