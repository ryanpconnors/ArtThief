package com.ryanpconnors.art_thief.compare;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.ryanpconnors.art_thief.SingleFragmentActivity;

public class SortArtWorkActivity extends SingleFragmentActivity implements SortArtWorkFragment.OnSortArtworkFragmentInteractionListener {

    public static final String EXTRA_STAR_COUNT = "com.ryanpconnors.artthief.star_count";

    @Override
    protected Fragment createFragment() {
        int starCount = (Integer) getIntent().getSerializableExtra(EXTRA_STAR_COUNT);
        return SortArtWorkFragment.newInstance(starCount);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO:
    }
}
