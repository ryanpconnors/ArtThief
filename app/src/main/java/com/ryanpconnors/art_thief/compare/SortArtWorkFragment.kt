package com.ryanpconnors.art_thief.compare

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.ShareActionProvider
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.Collections

import com.ryanpconnors.art_thief.compare.SortArtWorkFragment.ArtworkChoice.ALPHA
import com.ryanpconnors.art_thief.compare.SortArtWorkFragment.ArtworkChoice.BETA

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SortArtWorkFragment.OnSortArtworkFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SortArtWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SortArtWorkFragment : Fragment() {

    private var mArtWorks: List<ArtWork>? = null

    private var mRating: Int = 0

    private var mCurrentIndex = 0

    private var mArtworkImageViewAlpha: ImageView? = null
    private var mArtworkImageViewBeta: ImageView? = null

    private var mShareActionProvider: ShareActionProvider? = null
    private var mListener: OnSortArtworkFragmentInteractionListener? = null

    /**
     * Create a ShareIntent of both ArtWorks
     *
     * @return ShareIntent for both artworks to send to another application
     * to allow the user to share the two current artworks to get another option on which
     * one they prefer.
     */
    private val shareIntent: Intent?
        get() {
            val bitmapAlpha: Bitmap
            val bitmapBeta: Bitmap

            val artWorkAlpha = mArtWorks!![mCurrentIndex]
            val artWorkBeta = mArtWorks!![mCurrentIndex + 1]
            val imgFileAlpha = File(artWorkAlpha.largeImagePath!!)
            val imgFileBeta = File(artWorkBeta.largeImagePath!!)

            if (imgFileAlpha.exists() && imgFileBeta.exists()) {
                bitmapAlpha = BitmapFactory.decodeFile(imgFileAlpha.absolutePath)
                bitmapBeta = BitmapFactory.decodeFile(imgFileBeta.absolutePath)
            } else {
                mShareActionProvider = null
                return null
            }
            try {
                val fileAlpha = File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), artWorkAlpha.title!! + ".png")
                val fileBeta = File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), artWorkBeta.title!! + ".png")
                val outAlpha = FileOutputStream(fileAlpha)
                val outBeta = FileOutputStream(fileBeta)
                bitmapAlpha.compress(Bitmap.CompressFormat.PNG, 90, outAlpha)
                bitmapBeta.compress(Bitmap.CompressFormat.PNG, 90, outBeta)
                outAlpha.close()
                outBeta.close()
            } catch (e: IOException) {
                e.printStackTrace()
                mShareActionProvider = null
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                mShareActionProvider = null
                return null
            }

            if (isAdded) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                shareIntent.type = "image/*"

                val uris = ArrayList<Uri>()
                uris.add(FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".provider", imgFileAlpha))
                uris.add(FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".provider", imgFileBeta))

                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.prefer_text))
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                return shareIntent
            } else {
                return null
            }
        }

    /**
     * ArtworkChoice enumerated type used for identifying the user's choice
     * of which artwork they prefer for sorting purposes
     */
    protected enum class ArtworkChoice {
        ALPHA,
        BETA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mRating = arguments!!.getInt(ARG_NUMBER_OF_STARS)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sort_art_work, container, false)

        // Add toolbar and up button navigation
        val toolbar = v.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as SortArtWorkActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as SortArtWorkActivity).supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener { NavUtils.navigateUpFromSameTask(activity!!) }

        // Initialize ArtWork Image Views
        mArtworkImageViewAlpha = v.findViewById<View>(R.id.artwork_large_image_view_alpha) as ImageView
        mArtworkImageViewAlpha!!.setOnClickListener { sortArtwork(ALPHA) }
        mArtworkImageViewAlpha!!.isLongClickable = true
        mArtworkImageViewAlpha!!.setOnLongClickListener {
            Toast.makeText(activity, mArtWorks!![mCurrentIndex].showId, Toast.LENGTH_SHORT).show()
            true
        }

        mArtworkImageViewBeta = v.findViewById<View>(R.id.artwork_large_image_view_beta) as ImageView
        mArtworkImageViewBeta!!.setOnClickListener { sortArtwork(BETA) }
        mArtworkImageViewBeta!!.isLongClickable = true
        mArtworkImageViewBeta!!.setOnLongClickListener {
            Toast.makeText(activity, mArtWorks!![mCurrentIndex + 1].showId, Toast.LENGTH_SHORT).show()
            true
        }

        mArtWorks = Gallery.get(activity).getArtWorks(mRating, false)
        normalizeArtworkOrdering()

        displayArtwork(mArtworkImageViewAlpha, mCurrentIndex)
        displayArtwork(mArtworkImageViewBeta, mCurrentIndex + 1)
        return v
    }

    /**
     * @param choice
     */
    private fun sortArtwork(choice: ArtworkChoice) {
        if (choice == ALPHA) {
            swapOrderingAlphaBeta(mCurrentIndex, mCurrentIndex + 1)
            if (mCurrentIndex > 0) {
                mCurrentIndex -= 1
            } else if (mCurrentIndex < mArtWorks!!.size - 2) {
                mCurrentIndex += 1
            } else {
                Gallery.get(activity).setSorted(Math.round(mRating.toFloat()), true)
                Toast.makeText(activity, R.string.artworks_sorted, Toast.LENGTH_LONG).show()
                activity!!.finish()
            }
        } else if (choice == BETA) {
            if (mCurrentIndex >= mArtWorks!!.size - 2) {
                Gallery.get(activity).setSorted(Math.round(mRating.toFloat()), true)
                Toast.makeText(activity, R.string.artworks_sorted, Toast.LENGTH_LONG).show()
                activity!!.finish()
            } else {
                mCurrentIndex += 1
            }
        }

        displayArtwork(mArtworkImageViewAlpha, mCurrentIndex)
        displayArtwork(mArtworkImageViewBeta, mCurrentIndex + 1)
    }

    /**
     * Sorts and normalizes the ordering of each artwork in mArtWorks
     * such that ordering is from 0, 1, ..., N
     */
    private fun normalizeArtworkOrdering() {
        if (mArtWorks!!.isEmpty()) {
            return
        }

        for (i in mArtWorks!!.indices) {
            Collections.sort(mArtWorks!!)
            val artWork = mArtWorks!![i]
            if (artWork.ordering != i) {
                artWork.ordering = i
                Gallery.get(activity).updateArtWork(artWork)
            }
        }
    }

    /**
     * Swaps the ORDERING field of the alpha and beta artworks
     * Swaps the two artworks in the local mArtWorks
     * Updates the changes in the database
     */
    private fun swapOrderingAlphaBeta(alpha: Int, beta: Int) {
        val artWorkAlpha = mArtWorks!![alpha]
        val artWorkBeta = mArtWorks!![beta]

        artWorkAlpha.swapOrder(artWorkBeta)
        Collections.swap(mArtWorks!!, alpha, beta)

        Gallery.get(activity).updateArtWork(artWorkAlpha)
        Gallery.get(activity).updateArtWork(artWorkBeta)
    }


    /**
     * @param imageView
     * @param index
     */
    private fun displayArtwork(imageView: ImageView?, index: Int) {
        if (index < 0 || index >= mArtWorks!!.size) {
            Log.d(TAG, "loadArtwork called with invalid index : $index")
            throw IndexOutOfBoundsException()
        }
        val largeImagePath = mArtWorks!![index].largeImagePath
        if (largeImagePath != null) {
            val largeArtWorkImage = Gallery.get(activity).getArtWorkImage(largeImagePath)
            imageView!!.setImageBitmap(largeArtWorkImage)
        } else {
            // TODO: Handle the case where the artwork has no image
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {

        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater!!.inflate(R.menu.fragment_artwork, menu)

        // Setup ShareActionProvider menu item
        val shareItem = menu!!.findItem(R.id.menu_item_share)

        mShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        setShareIntentTask().execute()
        mShareActionProvider!!.setOnShareTargetSelectedListener { source, intent ->
            startActivity(intent)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.activity!!.onBackPressed()
                return true
            }

            R.id.menu_item_share -> return super.onOptionsItemSelected(item)
        // Share menu item not accessible here?

            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Asynchronous task for setting this ArtWorkFragments share intent
     */
    private inner class setShareIntentTask : AsyncTask<Void, Void, Void>() {

        internal var shareIntent: Intent? = null

        override fun doInBackground(vararg params: Void): Void? {
            shareIntent = shareIntent
            return null
        }

        override fun onPostExecute(result: Void) {
            if (isAdded && mShareActionProvider != null) {
                mShareActionProvider!!.setShareIntent(shareIntent)
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSortArtworkFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(String.format("%s must implement OnFragmentInteractionListener", context!!.toString()))
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
    interface OnSortArtworkFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        private val TAG = "SortArtWorkFragment"
        private val ARG_NUMBER_OF_STARS = "number_of_stars"

        fun newInstance(numberOfStars: Int): SortArtWorkFragment {
            val fragment = SortArtWorkFragment()
            val args = Bundle()
            args.putInt(ARG_NUMBER_OF_STARS, numberOfStars)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
