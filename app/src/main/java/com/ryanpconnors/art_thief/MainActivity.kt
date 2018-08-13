package com.ryanpconnors.art_thief

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.compare.CompareArtWorkFragment
import com.ryanpconnors.art_thief.rate.ArtWorkFragment
import com.ryanpconnors.art_thief.rate.ArtWorkListFragment
import com.ryanpconnors.art_thief.rate.ArtWorkPagerActivity
import com.ryanpconnors.art_thief.show.ShowFragment
import com.ryanpconnors.art_thief.update.UpdateArtWorkFragment
import com.ryanpconnors.art_thief.vote.ScannerActivity
import com.ryanpconnors.art_thief.vote.VoteFragment

import java.util.ArrayList

class MainActivity : AppCompatActivity(), UpdateArtWorkFragment.OnUpdateArtWorkFragmentInteractionListener, CompareArtWorkFragment.OnCompareArtFragmentInteractionListener, ShowFragment.OnShowFragmentInteractionListener, VoteFragment.OnVoteFragmentInteractionListener, ArtWorkFragment.OnArtWorkFragmentInteractionListener, ArtWorkListFragment.OnArtWorkListFragmentInteractionListener {

    private var mToolbar: Toolbar? = null
    private var mTabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(mToolbar)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById<View>(R.id.viewpager)
        setupViewPager(mViewPager!!)

        mTabLayout = findViewById<View>(R.id.tabs)
        mTabLayout!!.setupWithViewPager(mViewPager)

        setupTabIcons()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (requestCode) {
            TICKET_NUMBER_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val ticketCode = intent.getStringExtra(TICKET_CODE)
                    val spa = mViewPager!!.adapter as SectionsPagerAdapter?
                    val voteFragment = spa!!.getItem(getString(R.string.vote_title)) as VoteFragment
                    voteFragment.vote(ticketCode)
                }
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {

        // Create the adapter that will return a fragment for each of the sections of the activity.
        val adapter = SectionsPagerAdapter(supportFragmentManager)

        adapter.addFragment(UpdateArtWorkFragment.newInstance(), getString(R.string.update_art_title))
        adapter.addFragment(ArtWorkListFragment.newInstance(ARTWORK_LIST_COLUMN_COUNT), getString(R.string.rate_art_title))
        adapter.addFragment(CompareArtWorkFragment.newInstance(), getString(R.string.compare_art_title))
        adapter.addFragment(ShowFragment.newInstance(), getString(R.string.the_show_title))
        adapter.addFragment(VoteFragment.newInstance(), getString(R.string.vote_title))
        viewPager.adapter = adapter
    }

    private fun setupTabIcons() {
        val updateTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        updateTab.setText(R.string.update_art_tab_label)
        updateTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cloud_download_white_24dp, 0, 0)
        mTabLayout!!.getTabAt(0)!!.customView = updateTab

        val rateTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        rateTab.setText(R.string.rate_art_tab_label)
        rateTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_view_list_white_24dp, 0, 0)
        mTabLayout!!.getTabAt(1)!!.customView = rateTab

        val compareTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        compareTab.setText(R.string.compare_art_tab_label)
        compareTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grade_white_24dp, 0, 0)
        mTabLayout!!.getTabAt(2)!!.customView = compareTab

        val showTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        showTab.setText(R.string.the_show_tab_label)
        showTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event_seat_white_24dp, 0, 0)
        mTabLayout!!.getTabAt(3)!!.customView = showTab

        val voteTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        voteTab.setText(R.string.vote_tab_label)
        voteTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_poll_white_24dp, 0, 0)
        mTabLayout!!.getTabAt(4)!!.customView = voteTab
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        //TODO: Removes the Menu (settings) from upper right corner of Toolbar
        //getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return id == R.id.action_settings || super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun getItem(title: String): Fragment {
            return mFragmentList[mFragmentTitleList.lastIndexOf(title)]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    /**
     * Checks for granted permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            ZXING_CAMERA_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewScannerActivity()
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
                Log.d(TAG, "onRequestPermissionResult called for requestCode : $requestCode")
                return
            }
        }
    }


    /****************************************************************************
     * Fragment Communication Methods
     * http://developer.android.com/training/basics/fragments/communicating.html
     */

    // communication from the CompareArtWorkFragment
    override fun onCompareArtFragmentInteraction(uri: Uri) {

    }

    // communication from the ShowFragment
    override fun onShowFragmentInteraction(uri: Uri) {

    }

    // communication from the VoteFragment
    override fun onScanTicketButtonClick() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ZXING_CAMERA_PERMISSION)
        } else {
            startNewScannerActivity()
        }
    }

    fun startNewScannerActivity() {
        val scannerIntent = Intent(this, ScannerActivity::class.java)
        startActivityForResult(scannerIntent, TICKET_NUMBER_REQUEST_CODE)
    }


    // communication from the ArtWorkFragment
    override fun onArtWorkFragmentInteraction(uri: Uri) {

    }

    // communication from the ArtWorkListFragment
    override fun onArtWorkListFragmentInteraction(artWork: ArtWork) {
        val intent = ArtWorkPagerActivity.newIntent(this, artWork.id)
        startActivity(intent)
    }

    // Communication from the UpdateArtWorkFragment
    override fun onArtWorkDataSourceUpdate() {

        // Communicate to the ArtWorkListFragment that the data source has updated
        val sectionsPagerAdapter = mViewPager!!.adapter as SectionsPagerAdapter?
        val artWorkListFragment = sectionsPagerAdapter!!.getItem(1) as ArtWorkListFragment

        artWorkListFragment.updateUI()
    }

    companion object {

        private val TAG = "MainActivity"

        private val ARTWORK_LIST_COLUMN_COUNT = 1

        private val TICKET_NUMBER_REQUEST_CODE = 2
        val TICKET_CODE = "ticketCode"

        private val ZXING_CAMERA_PERMISSION = 1
    }
}
