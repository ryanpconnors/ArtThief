package com.ryanpconnors.art_thief.rate

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID


/**
 * Created by Ryan Connors on 2/14/16.
 */
class ArtWorkFragment : Fragment() {
    private var mArtWork: ArtWork? = null
    private var mArtWorkLargeImageView: ImageView? = null
    private var mTakenButton: Button? = null
    private var mArtworkRatingBar: RatingBar? = null
    private var mArtWorkTitleTextView: TextView? = null
    private var mArtWorkArtistTextView: TextView? = null
    private var mArtworkMediaTextView: TextView? = null
    private var mListener: OnArtWorkFragmentInteractionListener? = null
    private var mShareActionProvider: ShareActionProvider? = null


    /**
     * @return
     */
    private val shareIntent: Intent?
        get() {
            val bitmap: Bitmap

            var imgFile: File? = null
            if (mArtWork != null && mArtWork!!.largeImagePath != null) {
                imgFile = File(mArtWork!!.largeImagePath!!)
            }

            if (imgFile != null && imgFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            } else {
                mShareActionProvider = null
                return null
            }

            val imageUri: Uri
            try {
                val file = File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), mArtWork!!.title!! + ".png")
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.close()

                imageUri = FileProvider.getUriForFile(activity!!, activity!!.applicationContext.packageName + ".provider", file)

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
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " : " + mArtWork!!.title)
                return shareIntent
            } else {
                return null
            }
        }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        val artWorkId = arguments!!.getSerializable(ARG_ARTWORK_ID) as UUID
        mArtWork = Gallery.get(activity).getArtWork(artWorkId)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_artwork, container, false)

        //TODO: The application may be doing too much work on its main thread.
        mArtWorkLargeImageView = view.findViewById<View>(R.id.artwork_large_image_view) as ImageView
        val largeImagePath = mArtWork!!.largeImagePath
        if (largeImagePath != null) {
            try {
                val largeArtWorkImage = Gallery.get(activity).getArtWorkImage(largeImagePath)
                mArtWorkLargeImageView!!.setImageBitmap(largeArtWorkImage)
            } catch (e: OutOfMemoryError) {
                Toast.makeText(activity, "There was an error displaying the Artwork", Toast.LENGTH_LONG).show()
            }

        }

        mTakenButton = view.findViewById<View>(R.id.artwork_taken_button) as Button
        mTakenButton!!.visibility = if (mArtWork!!.isTaken) View.VISIBLE else View.INVISIBLE

        mArtworkRatingBar = view.findViewById<View>(R.id.artwork_rating_bar) as RatingBar
        mArtworkRatingBar!!.rating = mArtWork!!.stars.toFloat()
        mArtworkRatingBar!!.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            // Set the sorted flag to false for the previous and new rating
            Gallery.get(activity).setSorted(mArtWork!!.stars, false)
            Gallery.get(activity).setSorted(Math.round(rating), false)

            // Update the artwork rating
            mArtWork!!.stars = Math.round(rating)
            Gallery.get(activity).updateArtWork(mArtWork)
        }

        mArtWorkTitleTextView = view.findViewById<View>(R.id.artwork_title) as TextView
        mArtWorkTitleTextView!!.setText(String.format("(%s) %s", mArtWork!!.showId, mArtWork!!.title))

        mArtWorkArtistTextView = view.findViewById<View>(R.id.artwork_artist) as TextView
        mArtWorkArtistTextView!!.text = mArtWork!!.artist

        mArtworkMediaTextView = view.findViewById<View>(R.id.artwork_media) as TextView
        mArtworkMediaTextView!!.text = if (mArtWork!!.height!!.isEmpty() && mArtWork!!.width!!.isEmpty())
            mArtWork!!.media
        else
            String.format("%s (%s\" x %s\")", mArtWork!!.media, mArtWork!!.width, mArtWork!!.height)

        return view
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnArtWorkFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onPause() {
        super.onPause()
        Gallery.get(activity).updateArtWork(mArtWork)
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
    interface OnArtWorkFragmentInteractionListener {

        // TODO: Update argument type and name
        fun onArtWorkFragmentInteraction(uri: Uri)
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

    companion object {

        private val ARG_ARTWORK_ID = "artwork_id"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ArtWorkFragment.
         */
        fun newInstance(artWorkId: UUID): ArtWorkFragment {

            val args = Bundle()
            args.putSerializable(ARG_ARTWORK_ID, artWorkId)
            val artWorkFragment = ArtWorkFragment()
            artWorkFragment.arguments = args
            return artWorkFragment
        }
    }

}// Required empty public constructor
