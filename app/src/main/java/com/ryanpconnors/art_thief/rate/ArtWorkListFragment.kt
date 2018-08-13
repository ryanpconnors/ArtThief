package com.ryanpconnors.art_thief.rate

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnArtWorkListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ArtWorkListFragment : Fragment() {

    private var mColumnCount = 1
    private var mListener: OnArtWorkListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_artwork_list, container, false)

        val emptyTextViewLabel = view.rootView.findViewById<View>(R.id.empty_view) as TextView
        val list = view.rootView.findViewById<View>(R.id.list) as RecyclerView

        if (Gallery.get(activity).isEmpty) {
            list.visibility = View.GONE
            emptyTextViewLabel.visibility = View.VISIBLE
        } else {
            list.visibility = View.VISIBLE
            emptyTextViewLabel.visibility = View.GONE
        }

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()

            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            val artWorks = Gallery.get(activity).artworksSortedByShowId
            view.adapter = ArtWorkRecyclerViewAdapter(artWorks, mListener)
        }
        return view
    }


    fun updateUI() {
        if (view == null) {
            return
        }

        val recyclerView = view!!.findViewById<View>(R.id.list) as RecyclerView

        if (recyclerView != null) {
            var recyclerViewAdapter: ArtWorkRecyclerViewAdapter? = recyclerView.adapter as ArtWorkRecyclerViewAdapter

            if (recyclerViewAdapter == null) {
                val artWorks = Gallery.get(activity).artworksSortedByShowId
                recyclerViewAdapter = ArtWorkRecyclerViewAdapter(artWorks, mListener)
                recyclerView.adapter = recyclerViewAdapter
            } else {
                recyclerViewAdapter.updateArtWorks(Gallery.get(activity).artworksSortedByShowId)
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnArtWorkListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnArtWorkListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnArtWorkListFragmentInteractionListener {

        // TODO: Update argument type and name
        fun onArtWorkListFragmentInteraction(artWork: ArtWork)
    }

    companion object {

        private val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int): ArtWorkListFragment {
            val fragment = ArtWorkListFragment()

            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args

            return fragment
        }
    }
}// Required empty public constructor
