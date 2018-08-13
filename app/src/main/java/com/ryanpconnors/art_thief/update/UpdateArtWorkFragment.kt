package com.ryanpconnors.art_thief.update

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery
import com.ryanpconnors.art_thief.artgallery.GalleryFetcher

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UpdateArtWorkFragment.OnUpdateArtWorkFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UpdateArtWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateArtWorkFragment : Fragment() {

    private var mUpdateArtWorksButton: Button? = null
    private var mLastUpdateTextView: TextView? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mArtWorkUpdateListener: OnUpdateArtWorkFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retain this fragment to ensure that the asynchronous DownloadArtWorksTask completes
        retainInstance = true

        if (arguments != null) {
            // store given arguments in this class instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_update_art, container, false)

        // Setup the Update ArtWorks Button
        mUpdateArtWorksButton = v.findViewById<View>(R.id.update_artworks_button) as Button
        mUpdateArtWorksButton!!.setOnClickListener {
            mUpdateArtWorksButton!!.isEnabled = false
            mUpdateArtWorksButton!!.alpha = .5f
            UpdateArtWorksTask().execute()
        }

        // Setup the last update date textView
        mLastUpdateTextView = v.findViewById<View>(R.id.last_update_date_text) as TextView
        mLastUpdateTextView!!.setText(String.format("%s %s", getString(R.string.update_art_last_update), Gallery.get(activity).lastUpdateDate))

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnUpdateArtWorkFragmentInteractionListener) {
            mArtWorkUpdateListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mArtWorkUpdateListener = null
    }

    override fun onPause() {
        super.onPause()
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }


    //TODO: perform image downloads in separate async tasks???
    private fun downloadImageFiles(fetcher: GalleryFetcher, artWork: ArtWork) {

        // Obtain the small ArtWork image
        val smallImageUrl = artWork.smallImageUrl
        if (smallImageUrl != null) {
            val artWorkImage = fetcher.fetchArtWorkImage(smallImageUrl)
            val smallImagePath = Gallery.get(activity).saveToInternalStorage(
                    artWorkImage,
                    artWork.artThiefID.toString() + getString(R.string.small_image_extension)
            )
            artWork.smallImagePath = smallImagePath
        }

        // Obtain the large ArtWork image
        val largeImageUrl = artWork.largeImageUrl
        if (largeImageUrl != null) {
            val artWorkImage = fetcher.fetchArtWorkImage(largeImageUrl)
            val largeImagePath = Gallery.get(activity).saveToInternalStorage(
                    artWorkImage,
                    artWork.artThiefID.toString() + getString(R.string.large_image_extension)
            )
            artWork.largeImagePath = largeImagePath
        }
    }

    private fun updateInfoTable(showYear: Int, dataVersion: Int) {

        if (showYear == -1 || dataVersion == -1) {
            Log.d(TAG, "Error in updateInfoTable() showYear= " + showYear +
                    " | dataVersion= " + dataVersion)
        }

        // Update the InfoTable in the database to reflect the current data
        val sdf = SimpleDateFormat(getString(R.string.date_format), resources.configuration.locale)
        sdf.timeZone = Calendar.getInstance().timeZone
        val todayDate = sdf.format(Date())
        Gallery.get(activity).updateInfo(todayDate, showYear, dataVersion)
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
    interface OnUpdateArtWorkFragmentInteractionListener {

        fun onArtWorkDataSourceUpdate()
    }

    /**
     *
     */
    private inner class UpdateArtWorksTask : AsyncTask<Void, String, Boolean>() {

        internal var mCurrentArtworkUpdating: Int = 0
        internal var mNewArtWorkTotal: Int = 0

        override fun onPreExecute() {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setTitle("Updating Artworks")
            mProgressDialog!!.setMessage("Download in progress...")
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.show()
        }

        /**
         * Fetch new artworks and update the gallery accordingly.
         *
         * @param args
         * @return
         */
        override fun doInBackground(vararg args: Void): Boolean? {

            val fetcher = GalleryFetcher()
            val existingArtWorks = Gallery.get(activity).artWorks

            val loot = fetcher.fetchLoot(context)

            val newArtworks: List<ArtWork>
            if (loot != null && loot[getString(R.string.art_works)] is List<*>) {
                newArtworks = loot[getString(R.string.art_works)] as List<ArtWork>
            } else {
                return false
            }

            val info = Gallery.get(activity).info

            // Check if the showYear has been updated
            // If so, delete all previous artwork from previous show
            if (info != null && loot[getString(R.string.show_year)] as Int > info[getString(R.string.show_year)] as Int) {
                Gallery.get(activity).clearArtwork()
            }

            var gallerySize = existingArtWorks.size
            mNewArtWorkTotal = newArtworks.size
            mCurrentArtworkUpdating = 0

            //TODO: Only update the artWorks that have changed
            for (artWork in newArtworks) {

                mCurrentArtworkUpdating += 1
                val existingArtWork = Gallery.get(activity).getArtWork(artWork.artThiefID)

                if (existingArtWork == null) {

                    // download image files if they exist and store the paths in newArtWork
                    downloadImageFiles(fetcher, artWork)

                    // Set the order of the new artwork to the current size of the gallery
                    artWork.ordering = gallerySize

                    // Initialize the number of stars to Zero
                    artWork.stars = 0

                    // insert the newArtWork into the Gallery database
                    Gallery.get(activity).addArtWork(artWork)
                    gallerySize++
                } else {

                    // update the existing artwork if it is not equal to newArtWork
                    if (artWork != existingArtWork) {
                        Gallery.get(activity).updateArtWork(artWork)
                    } else if (existingArtWork.smallImagePath == null || existingArtWork.largeImagePath == null) {
                        // download image files if they exist and store the paths in newArtWork
                        downloadImageFiles(fetcher, artWork)
                        Gallery.get(activity).updateArtWork(artWork)
                    }// Check the image paths of the artwork
                    // otherwise do nothing, the artwork does not need to be updated
                }
                publishProgress(artWork.title)
            }

            /* TODO: Remove existing artWork from the database if it is no longer in loot.json
                As this is written, it will not work as the `equals` method won't ever yield a positive
                result for a new artwork with an existing one. This might not even be necessary. */
            for (existingArtWork in existingArtWorks) {
                if (!newArtworks.contains(existingArtWork)) {
                    Log.i(TAG, "Deleting Artwork : " + existingArtWork.title + " [ " + existingArtWork.artThiefID + " ]")
                    Gallery.get(activity).deleteArtWork(existingArtWork)
                    gallerySize--

                    // TODO : Reorder ALL artwork >= the one deleted to maintain order
                }
            }
            updateInfoTable(loot["showYear"] as Int, loot["dataVersion"] as Int)
            return true
        }

        override fun onProgressUpdate(vararg args: String) {
            for (s in args) {
                if (!mProgressDialog!!.isShowing) {
                    mProgressDialog!!.show()
                }
                mProgressDialog!!.setMessage(String.format(Locale.US, "(%d of %d) %s", mCurrentArtworkUpdating, mNewArtWorkTotal, s))
            }
        }

        override fun onPostExecute(success: Boolean?) {

            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
            }

            if (success!!) {
                mArtWorkUpdateListener!!.onArtWorkDataSourceUpdate()
                mLastUpdateTextView!!.setText(String.format(Locale.US, "%s %s", getString(R.string.update_art_last_update), Gallery.get(activity).lastUpdateDate))
            }

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(if (success) getString(R.string.success) else getString(R.string.error))
                    .setMessage(if (success) getString(R.string.update_artwork_success) else getString(R.string.update_artwork_error))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                        // Dismiss the AlertDialog
                    }
            val alert = builder.create()
            alert.show()

            mUpdateArtWorksButton!!.isEnabled = !success
            mUpdateArtWorksButton!!.alpha = if (success) 0.5f else 1.0f
        }
    }

    companion object {

        private val TAG = "UpdateArtWorkFragment"

        /**
         * Create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UpdateArtWorkFragment.
         */
        fun newInstance(): UpdateArtWorkFragment {

            val fragment = UpdateArtWorkFragment()
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

}// Required empty public constructor
