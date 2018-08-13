package com.ryanpconnors.art_thief.vote

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [VoteFragment.OnVoteFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [VoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VoteFragment : Fragment() {

    private var mListener: OnVoteFragmentInteractionListener? = null
    private var mScanTicketButton: Button? = null

    private var mProgressDialog: ProgressDialog? = null

    private var mTicketCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            // Do something with args
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_vote, container, false)
        mScanTicketButton = view.findViewById<View>(R.id.scan_ticket_button) as Button
        mScanTicketButton!!.setOnClickListener { mListener!!.onScanTicketButtonClick() }
        return view
    }

    /**
     * @param ticketCode
     */
    fun vote(ticketCode: String) {
        mTicketCode = ticketCode
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.cast_your_vote))
                .setMessage(getString(R.string.transmit_vote_message))
                .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                    val votePackage = getVotingPackage(mTicketCode)
                    VotePostTask().execute(votePackage)
                }
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, id -> return@OnClickListener })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnVoteFragmentInteractionListener) {
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
     * @param ticketCode String
     * @return
     */
    private fun getVotingPackage(ticketCode: String?): JSONObject {

        val jsonPackage = JSONObject()
        val list = JSONArray()

        for (i in 5 downTo 1) {
            val artworks = Gallery.get(activity).getArtWorks(i, getString(R.string.descending))

            for (artWork in artworks) {
                val artworkJson = JSONObject()
                try {
                    artworkJson.put(getString(R.string.vote_package_art_thief_id), artWork.artThiefID)
                    artworkJson.put(getString(R.string.vote_package_show_id), artWork.showId)
                    artworkJson.put(getString(R.string.vote_package_rating), artWork.stars)
                    list.put(artworkJson)
                } catch (e: JSONException) {
                    Log.e(TAG, e.message)
                }

            }
        }
        try {
            jsonPackage.put(getString(R.string.vote_package_list), list)
            jsonPackage.put(getString(R.string.vote_package_uuid), UUID.randomUUID().toString())
            jsonPackage.put(getString(R.string.vote_package_scan_data), ticketCode)
        } catch (e: JSONException) {
            Log.e(TAG, e.message)
        }

        return jsonPackage
    }

    private inner class VotePostTask : AsyncTask<JSONObject, String, Boolean>() {

        internal var voteJson: JSONObject

        override fun onPreExecute() {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setTitle(getString(R.string.casting_vote))
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.show()
        }

        override fun doInBackground(vararg args: JSONObject): Boolean? {

            voteJson = args[0]

            val url: URL
            var urlConnection: HttpURLConnection? = null

            try {
                url = URL(getString(R.string.vote_url))
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.doOutput = true
                urlConnection.setChunkedStreamingMode(0)

                val out = BufferedOutputStream(urlConnection.outputStream)
                writeStream(out, voteJson.toString())

                val `in` = BufferedInputStream(urlConnection.inputStream)
                readStream(`in`)

                if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException(urlConnection.responseMessage + ": with " + url)
                }
            } catch (e: MalformedURLException) {
                Log.e(TAG, e.message)
                return false
            } catch (e: IOException) {
                Log.e(TAG, e.message)
                return false
            } finally {
                urlConnection?.disconnect()
            }
            return true
        }


        override fun onPostExecute(success: Boolean?) {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
            }
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(if (success) getString(R.string.success) else getString(R.string.error))
                    .setMessage(if (success) getString(R.string.vote_success) else getString(R.string.vote_error))
                    .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                        // Dismiss the AlertDialog
                    }
            builder.create()
            builder.show()

            mScanTicketButton!!.isEnabled = (!success)!!
            mScanTicketButton!!.alpha = if (success) 0.5f else 1.0f
        }
    }

    @Throws(IOException::class)
    private fun readStream(`in`: InputStream) {
        val br = BufferedReader(InputStreamReader(`in`))
        val sb = StringBuilder()
        var line: String
        while ((line = br.readLine()) != null) {
            sb.append(String.format("%s\n", line))
        }
        br.close()
        println("" + sb.toString())
    }

    private fun writeStream(out: OutputStream, data: String) {
        val writer = OutputStreamWriter(out)
        try {
            writer.write(data)
            writer.flush()
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        }

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
    interface OnVoteFragmentInteractionListener {

        fun onScanTicketButtonClick()
    }

    companion object {

        val TAG = "VoteFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment VoteFragment.
         */
        fun newInstance(): VoteFragment {
            val fragment = VoteFragment()

            // For arguments passed into the new instance
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
