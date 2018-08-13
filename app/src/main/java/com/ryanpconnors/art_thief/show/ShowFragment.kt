package com.ryanpconnors.art_thief.show

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery

import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ShowFragment.OnShowFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ShowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowFragment : Fragment() {

    private var mListener: OnShowFragmentInteractionListener? = null

    private var mTopPickArtworkImageView: ImageView? = null
    private var mTopPickShowIdTextView: TextView? = null
    private var mTopPickTitleTextView: TextView? = null
    private var mTopPickArtistTextView: TextView? = null
    private var mTopPickMediaTextView: TextView? = null
    private var mTopPickRatingTextView: TextView? = null
    private var mTopPickRatingStarImageView: ImageView? = null
    private var mTopPickEmptyText: TextView? = null

    private var mIdEditText: EditText? = null
    private var mMarkTakenButton: Button? = null
    private var mTakenButton: Button? = null

    private var mCurrentArtworkImageView: ImageView? = null
    private var mCurrentArtworkTextView: TextView? = null
    private var mCurrentArtwork: ArtWork? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            //            mParam1 = getArguments().getString(ARG_PARAM1);
            //            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_the_show, container, false)

        // Initialize top rated Artwork view objects
        mTopPickArtworkImageView = view.findViewById<View>(R.id.top_pick_artwork_image) as ImageView
        mTopPickShowIdTextView = view.findViewById<View>(R.id.top_pick_show_id) as TextView
        mTopPickTitleTextView = view.findViewById<View>(R.id.top_pick_title) as TextView
        mTopPickArtistTextView = view.findViewById<View>(R.id.top_pick_artist) as TextView
        mTopPickMediaTextView = view.findViewById<View>(R.id.top_pick_media) as TextView
        mTopPickRatingTextView = view.findViewById<View>(R.id.top_pick_rating_text_view) as TextView
        mTopPickRatingStarImageView = view.findViewById<View>(R.id.top_pick_rating_star_image_view) as ImageView
        mTopPickEmptyText = view.findViewById<View>(R.id.top_pick_empty_text) as TextView

        // Initialize ShowFragment view objects
        mIdEditText = view.findViewById<View>(R.id.id_edit_text) as EditText
        mIdEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing here
            }

            /**
             * Called when user changes the text in mIdEditText. Gets the current artwork from the Gallery
             * that matches the supplied ID in mIdEditText iff it exists, assigns it to mCurrentArtwork and calls
             * `setCurrentArtworkImageView()` to apply the updated current artwork to the imageView.
             * If an artwork does not exist for the given ID in the Gallery, the taken button is displayed with text
             * alerting the user that artowrk for the given ID was not found.
             *
             * @param s
             */
            override fun afterTextChanged(s: Editable) {

                if (!s.toString().matches("".toRegex())) {
                    mCurrentArtwork = Gallery.get(activity).getArtWork(s.toString())

                    if (mCurrentArtwork == null) {

                        mCurrentArtworkImageView!!.setImageBitmap(null)
                        mCurrentArtworkTextView!!.visibility = View.INVISIBLE

                        mTakenButton!!.setText(String.format(Locale.US, getString(R.string.artwork_not_found), s.toString()))
                        mTakenButton!!.visibility = View.VISIBLE

                        setTakenButton()
                        return
                    }
                } else {
                    mCurrentArtwork = null
                    mTakenButton!!.visibility = View.INVISIBLE
                }
                setCurrentArtworkImageView()
                setTakenButton()
            }
        })

        mTakenButton = view.findViewById<View>(R.id.taken_button) as Button
        mTakenButton!!.visibility = View.INVISIBLE

        mMarkTakenButton = view.findViewById<View>(R.id.mark_as_taken_button) as Button
        mMarkTakenButton!!.setOnClickListener { view ->
            // Hide the keyboard if it is open
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            if (mCurrentArtwork != null) {
                mCurrentArtwork!!.isTaken = !mCurrentArtwork!!.isTaken
                Gallery.get(activity).updateArtWork(mCurrentArtwork) // reverse the current artworks `taken` status
                setCurrentArtworkImageView()
                setTopRatedArtwork() //refresh the current top rated artwork
                setTakenButton()
            }
        }
        setTakenButton()

        mCurrentArtworkImageView = view.findViewById<View>(R.id.current_artwork_image_view) as ImageView
        mCurrentArtworkTextView = view.findViewById<View>(R.id.current_artwork_text_view) as TextView

        return view
    }

    private fun setTakenButton() {

        if (mCurrentArtwork == null) {
            mMarkTakenButton!!.isClickable = false
            mMarkTakenButton!!.alpha = .5f
        } else {
            mMarkTakenButton!!.setText(if (mCurrentArtwork!!.isTaken) R.string.mark_not_taken else R.string.mark_taken)
            mMarkTakenButton!!.isClickable = true
            mMarkTakenButton!!.alpha = 1f

            mTakenButton!!.setText(String.format(Locale.US, getString(R.string.artwork_taken), mCurrentArtwork!!.showId))
            mTakenButton!!.visibility = if (mCurrentArtwork!!.isTaken) View.VISIBLE else View.INVISIBLE
        }
    }

    /**
     *
     */
    private fun setCurrentArtworkImageView() {

        if (mCurrentArtwork == null) {
            mCurrentArtworkTextView!!.setText(R.string.enter_artwork_id)
            mCurrentArtworkTextView!!.visibility = View.VISIBLE
            mCurrentArtworkImageView!!.setImageBitmap(null)
            return
        }

        val largeImagePath = mCurrentArtwork!!.largeImagePath
        if (largeImagePath != null) {
            val largeArtworkImage = Gallery.get(activity).getArtWorkImage(largeImagePath)
            mCurrentArtworkImageView!!.setImageBitmap(largeArtworkImage)
        } else {
            mCurrentArtworkImageView!!.setImageBitmap(null)
            mTakenButton!!.setText(String.format(Locale.US, getString(R.string.artwork_image_not_found), mCurrentArtwork!!.showId))
            mTakenButton!!.visibility = View.VISIBLE
            mCurrentArtworkTextView!!.visibility = View.INVISIBLE
        }

        if (mCurrentArtwork!!.isTaken) {
            mTakenButton!!.setText(String.format(Locale.US, getString(R.string.artwork_taken), mCurrentArtwork!!.showId))
            mTakenButton!!.visibility = View.VISIBLE
            mCurrentArtworkTextView!!.visibility = View.INVISIBLE
        } else {
            mCurrentArtworkTextView!!.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        setTopRatedArtwork()
    }

    private fun setTopRatedArtwork() {
        val topRatedArtwork = Gallery.get(activity).topPickArtwork
        if (topRatedArtwork == null) {
            mTopPickArtworkImageView!!.visibility = View.INVISIBLE
            mTopPickShowIdTextView!!.visibility = View.INVISIBLE
            mTopPickTitleTextView!!.visibility = View.INVISIBLE
            mTopPickArtistTextView!!.visibility = View.INVISIBLE
            mTopPickMediaTextView!!.visibility = View.INVISIBLE
            mTopPickRatingTextView!!.visibility = View.INVISIBLE
            mTopPickRatingStarImageView!!.visibility = View.INVISIBLE
            mTopPickEmptyText!!.visibility = View.VISIBLE
        } else {
            val smallImagePath = topRatedArtwork.smallImagePath
            if (smallImagePath != null) {
                val smallArtWorkImage = Gallery.get(activity).getArtWorkImage(smallImagePath)
                mTopPickArtworkImageView!!.setImageBitmap(smallArtWorkImage)
            }
            mTopPickShowIdTextView!!.text = if (topRatedArtwork.showId == "x") "*" else "(" + topRatedArtwork.showId + ")"
            mTopPickTitleTextView!!.text = topRatedArtwork.title
            mTopPickArtistTextView!!.text = topRatedArtwork.artist
            mTopPickMediaTextView!!.text = topRatedArtwork.media

            if (topRatedArtwork.stars > 0) {
                mTopPickRatingStarImageView!!.setImageResource(R.drawable.ic_star_border_black_18dp)
                mTopPickRatingTextView!!.setText(String.format(Locale.US, "%s", Integer.toString(topRatedArtwork.stars)))
                mTopPickRatingStarImageView!!.visibility = View.VISIBLE
                mTopPickRatingTextView!!.visibility = View.VISIBLE
            } else {
                mTopPickRatingStarImageView!!.visibility = View.GONE
                mTopPickRatingTextView!!.visibility = View.GONE
            }
            mTopPickArtworkImageView!!.visibility = View.VISIBLE
            mTopPickShowIdTextView!!.visibility = View.VISIBLE
            mTopPickTitleTextView!!.visibility = View.VISIBLE
            mTopPickArtistTextView!!.visibility = View.VISIBLE
            mTopPickMediaTextView!!.visibility = View.VISIBLE
            mTopPickRatingTextView!!.visibility = View.VISIBLE
            mTopPickRatingStarImageView!!.visibility = View.VISIBLE
            mTopPickEmptyText!!.visibility = View.INVISIBLE
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onShowFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnShowFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
    interface OnShowFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onShowFragmentInteraction(uri: Uri)
    }

    companion object {

        /**
         * Create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ShowFragment.
         */
        fun newInstance(): ShowFragment {
            val fragment = ShowFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

}// Required empty public constructor
