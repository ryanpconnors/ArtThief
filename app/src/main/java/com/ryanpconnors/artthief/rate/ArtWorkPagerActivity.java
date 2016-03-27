package com.ryanpconnors.artthief.rate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.ryanpconnors.artthief.R;
import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;
import com.ryanpconnors.artthief.rate.ArtWorkFragment.OnArtWorkFragmentInteractionListener;

import java.util.List;
import java.util.UUID;

public class ArtWorkPagerActivity extends AppCompatActivity implements OnArtWorkFragmentInteractionListener {

    private static final String EXTRA_ARTWORK_ID = "com.ryanpconnors.artthief.artwork_id";
    private static final int PAGING_LIMIT = 4;

    private ViewPager mViewPager;
    private List<ArtWork> mArtWorks;

    private ShareActionProvider mShareActionProvider;

    public static Intent newIntent(Context packageContext, UUID artWorkId) {
        Intent intent = new Intent(packageContext, ArtWorkPagerActivity.class);
        intent.putExtra(EXTRA_ARTWORK_ID, artWorkId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_work_pager);

        UUID artWorkId = (UUID) getIntent().getSerializableExtra(EXTRA_ARTWORK_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_art_work_pager_view_pager);
        mArtWorks = Gallery.get(this).getArtWorks();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                ArtWork artWork = mArtWorks.get(position);
                return ArtWorkFragment.newInstance(artWork.getId());
            }

            @Override
            public int getCount() {
                return mArtWorks.size();
            }
        });

        mViewPager.setOffscreenPageLimit(PAGING_LIMIT);

        // Set the displayed artWork in the ViewPager to the selected artWork
        for (int i = 0; i < mArtWorks.size(); i++) {
            if (mArtWorks.get(i).getId().equals(artWorkId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // TODO is it best to handle this here in the ArtWorkPagerActivity or in the ArtWorkFragment
        getMenuInflater().inflate(R.menu.fragment_artwork, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO implement sharing feature
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    // TODO
    public void onArtWorkFragmentInteraction(Uri uri) {

    }
}
