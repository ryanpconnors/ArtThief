package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnArtWorkListFragmentInteractionListener}
 * interface.
 */
public class ArtWorkListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnArtWorkListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtWorkListFragment() {
        // Required empty public constructor
    }

    public static ArtWorkListFragment newInstance(int columnCount) {
        ArtWorkListFragment fragment = new ArtWorkListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artwork_list, container, false);

        TextView emptyTextViewLabel = (TextView) view.getRootView().findViewById(R.id.empty_view);
        RecyclerView list = (RecyclerView) view.getRootView().findViewById(R.id.list);

        if (Gallery.get(getActivity()).isEmpty()) {
            list.setVisibility(View.GONE);
            emptyTextViewLabel.setVisibility(View.VISIBLE);
        }
        else {
            list.setVisibility(View.VISIBLE);
            emptyTextViewLabel.setVisibility(View.GONE);
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ArtWorkRecyclerViewAdapter(
                    Gallery.get(getActivity()).getArtWorks(),
                    mListener)
            );
        }
        return view;
    }


    public void updateUI() {
        if (getView()== null) {
            return;
        }

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.list);

        if (recyclerView != null) {
            ArtWorkRecyclerViewAdapter recyclerViewAdapter = (ArtWorkRecyclerViewAdapter) recyclerView.getAdapter();

            if (recyclerViewAdapter == null) {
                recyclerViewAdapter = new ArtWorkRecyclerViewAdapter(
                        Gallery.get(getActivity()).getArtWorks(),
                        mListener
                );
                recyclerView.setAdapter(recyclerViewAdapter);
            }
            recyclerViewAdapter.updateArtWorks(Gallery.get(getActivity()).getArtWorks());
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArtWorkListFragmentInteractionListener) {
            mListener = (OnArtWorkListFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnArtWorkListFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnArtWorkListFragmentInteractionListener {

        // TODO: Update argument type and name
        void onArtWorkListFragmentInteraction(ArtWork artWork);
    }
}
