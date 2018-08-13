package com.ryanpconnors.art_thief.compare

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.Gallery

import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CompareArtWorkFragment.OnCompareArtFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CompareArtWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompareArtWorkFragment : Fragment() {

    private var mCompareArtWorkListener: OnCompareArtFragmentInteractionListener? = null
    private var mFiveStarButton: Button? = null
    private var mFourStarButton: Button? = null
    private var mThreeStarButton: Button? = null
    private var mTwoStarButton: Button? = null
    private var mOneStarButton: Button? = null

    private var mFiveStarDoneImageView: ImageView? = null
    private var mFourStarDoneImageView: ImageView? = null
    private var mThreeStarDoneImageView: ImageView? = null
    private var mTwoStarDoneImageView: ImageView? = null
    private var mOneStarDoneImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // Retain this fragment to ensure that the asynchronous UpdateArtWorksTask completes
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            //TODO: store given arguments in this class instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_compare_art, container, false)

        mOneStarButton = v.findViewById<View>(R.id.compare_artwork_one_star_button) as Button
        mOneStarButton!!.setOnClickListener { startSortArtWorkActivity(1) }

        mOneStarDoneImageView = v.findViewById<View>(R.id.one_star_done) as ImageView

        mTwoStarButton = v.findViewById<View>(R.id.compare_artwork_two_star_button) as Button
        mTwoStarButton!!.setOnClickListener { startSortArtWorkActivity(2) }

        mTwoStarDoneImageView = v.findViewById<View>(R.id.two_star_done) as ImageView

        mThreeStarButton = v.findViewById<View>(R.id.compare_artwork_three_star_button) as Button
        mThreeStarButton!!.setOnClickListener { startSortArtWorkActivity(3) }

        mThreeStarDoneImageView = v.findViewById<View>(R.id.three_star_done) as ImageView

        mFourStarButton = v.findViewById<View>(R.id.compare_artwork_four_star_button) as Button
        mFourStarButton!!.setOnClickListener { startSortArtWorkActivity(4) }

        mFourStarDoneImageView = v.findViewById<View>(R.id.four_star_done) as ImageView

        mFiveStarButton = v.findViewById<View>(R.id.compare_artwork_five_star_button) as Button
        mFiveStarButton!!.setOnClickListener { startSortArtWorkActivity(5) }

        mFiveStarDoneImageView = v.findViewById<View>(R.id.five_star_done) as ImageView

        return v
    }

    private fun startSortArtWorkActivity(starCount: Int) {

        val intent = Intent(activity, SortArtWorkActivity::class.java)
        intent.putExtra(SortArtWorkActivity.EXTRA_STAR_COUNT, starCount)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshStarButtons()
    }

    private fun refreshStarButtons() {
        for (i in 1..5) {
            setStarButtonStatus(i)
        }
    }

    private fun setStarButtonStatus(numberOfStars: Int) {

        val starButton: Button?
        val starButtonText: String
        val starCount: Int

        when (numberOfStars) {
            1 -> {
                starButton = mOneStarButton
                starCount = Gallery.get(activity).getStarCount(getString(R.string.one_star))
                starButtonText = resources
                        .getString(R.string.compare_one_star_button_label, starCount)
                mOneStarDoneImageView!!.visibility = if (Gallery.get(activity).isSorted(1)) View.VISIBLE else View.INVISIBLE
            }

            2 -> {
                starButton = mTwoStarButton
                starCount = Gallery.get(activity).getStarCount(getString(R.string.two_stars))
                starButtonText = resources
                        .getString(R.string.compare_two_star_button_label, starCount)
                mTwoStarDoneImageView!!.visibility = if (Gallery.get(activity).isSorted(2)) View.VISIBLE else View.INVISIBLE
            }

            3 -> {
                starButton = mThreeStarButton
                starCount = Gallery.get(activity).getStarCount(getString(R.string.three_stars))
                starButtonText = resources
                        .getString(R.string.compare_three_star_button_label, starCount)
                mThreeStarDoneImageView!!.visibility = if (Gallery.get(activity).isSorted(3)) View.VISIBLE else View.INVISIBLE
            }

            4 -> {
                starButton = mFourStarButton
                starCount = Gallery.get(activity).getStarCount(getString(R.string.four_stars))
                starButtonText = resources
                        .getString(R.string.compare_four_star_button_label, starCount)
                mFourStarDoneImageView!!.visibility = if (Gallery.get(activity).isSorted(4)) View.VISIBLE else View.INVISIBLE
            }

            5 -> {
                starButton = mFiveStarButton
                starCount = Gallery.get(activity).getStarCount(getString(R.string.five_stars))
                starButtonText = resources
                        .getString(R.string.compare_five_star_button_label, starCount)
                mFiveStarDoneImageView!!.visibility = if (Gallery.get(activity).isSorted(5)) View.VISIBLE else View.INVISIBLE
            }

            else -> {
                LOG.warning("$TAG: Invalid number of stars: $numberOfStars")
                return
            }
        }

        starButton!!.text = starButtonText

        /* Enable the star button iff there are at least 2 artworks available */
        starButton.isEnabled = starCount >= 2
        starButton.alpha = if (starButton.isEnabled) 1f else .5f

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mCompareArtWorkListener != null) {
            mCompareArtWorkListener!!.onCompareArtFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnCompareArtFragmentInteractionListener) {
            mCompareArtWorkListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCompareArtWorkListener = null
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
    interface OnCompareArtFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onCompareArtFragmentInteraction(uri: Uri)
    }

    companion object {

        private val TAG = "CompareArtWorkFragment"

        private val LOG = Logger.getLogger(CompareArtWorkFragment.TAG)

        /**
         * Create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CompareArtWorkFragment.
         */
        fun newInstance(): CompareArtWorkFragment {

            val fragment = CompareArtWorkFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }
}// Required empty public constructor
