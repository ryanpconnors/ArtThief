package com.ryanpconnors.art_thief.compare

import android.net.Uri
import android.support.v4.app.Fragment

import com.ryanpconnors.art_thief.SingleFragmentActivity

class SortArtWorkActivity : SingleFragmentActivity(), SortArtWorkFragment.OnSortArtworkFragmentInteractionListener {

    override fun createFragment(): Fragment {
        val starCount = intent.getSerializableExtra(EXTRA_STAR_COUNT) as Int
        return SortArtWorkFragment.newInstance(starCount)
    }

    override fun onFragmentInteraction(uri: Uri) {
        // TODO:
    }

    companion object {

        val EXTRA_STAR_COUNT = "com.ryanpconnors.artthief.star_count"
    }
}
