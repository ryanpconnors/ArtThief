package com.ryanpconnors.art_thief.rate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu

import com.ryanpconnors.art_thief.R
import com.ryanpconnors.art_thief.artgallery.ArtWork
import com.ryanpconnors.art_thief.artgallery.Gallery
import com.ryanpconnors.art_thief.rate.ArtWorkFragment.OnArtWorkFragmentInteractionListener
import java.util.UUID

class ArtWorkPagerActivity : AppCompatActivity(), OnArtWorkFragmentInteractionListener {

    private var mViewPager: ViewPager? = null
    private var mArtWorks: List<ArtWork>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_work_pager)

        val artWorkId = intent.getSerializableExtra(EXTRA_ARTWORK_ID) as UUID

        mViewPager = findViewById<View>(R.id.activity_art_work_pager_view_pager)
        mArtWorks = Gallery.get(this).artworksSortedByShowId

        val fragmentManager = supportFragmentManager
        mViewPager!!.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                val artWork = mArtWorks!![position]
                return ArtWorkFragment.newInstance(artWork.id)
            }

            override fun getCount(): Int {
                return mArtWorks!!.size
            }
        }

        mViewPager!!.offscreenPageLimit = PAGING_LIMIT

        // Set the displayed artWork in the ViewPager to the selected artWork
        for (i in mArtWorks!!.indices) {
            if (mArtWorks!![i].id == artWorkId) {
                mViewPager!!.currentItem = i
                break
            }
        }
    }


    // TODO
    override fun onArtWorkFragmentInteraction(uri: Uri) {

    }

    companion object {

        private val EXTRA_ARTWORK_ID = "com.ryanpconnors.artthief.artwork_id"
        private val PAGING_LIMIT = 4

        fun newIntent(packageContext: Context, artWorkId: UUID): Intent {
            val intent = Intent(packageContext, ArtWorkPagerActivity::class.java)
            intent.putExtra(EXTRA_ARTWORK_ID, artWorkId)
            return intent
        }
    }
}
