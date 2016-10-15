package com.ryanpconnors.artthief;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ryanpconnors.artthief.artgallery.ArtWork;
import com.ryanpconnors.artthief.artgallery.Gallery;
import com.ryanpconnors.artthief.compare.CompareArtWorkFragment;
import com.ryanpconnors.artthief.rate.ArtWorkFragment;
import com.ryanpconnors.artthief.rate.ArtWorkListFragment;
import com.ryanpconnors.artthief.rate.ArtWorkPagerActivity;
import com.ryanpconnors.artthief.show.ShowFragment;
import com.ryanpconnors.artthief.update.UpdateArtWorkFragment;
import com.ryanpconnors.artthief.vote.VoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
        UpdateArtWorkFragment.OnUpdateArtWorkFragmentInteractionListener,
        CompareArtWorkFragment.OnCompareArtFragmentInteractionListener,
        ShowFragment.OnShowFragmentInteractionListener,
        VoteFragment.OnVoteFragmentInteractionListener,
        ArtWorkFragment.OnArtWorkFragmentInteractionListener,
        ArtWorkListFragment.OnArtWorkListFragmentInteractionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<ArtWork> mDummyArtWorks;

    private static final int ARTWORK_LIST_COLUMN_COUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        setupTabIcons();

        mDummyArtWorks = Gallery.get(this).getArtWorks();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void setupViewPager(ViewPager viewPager) {

        // Create the adapter that will return a fragment for each of the sections of the activity.
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(UpdateArtWorkFragment.newInstance(), getString(R.string.update_art_title));
        adapter.addFragment(ArtWorkListFragment.newInstance(ARTWORK_LIST_COLUMN_COUNT), getString(R.string.rate_art_title));
        adapter.addFragment(CompareArtWorkFragment.newInstance(), getString(R.string.compare_art_title));
        adapter.addFragment(ShowFragment.newInstance(), getString(R.string.the_show_title));
        adapter.addFragment(VoteFragment.newInstance(), getString(R.string.vote_title));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        TextView updateTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        updateTab.setText(R.string.update_art_tab_label);
        updateTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cloud_download_white_24dp, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(updateTab);

        TextView rateTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        rateTab.setText(R.string.rate_art_tab_label);
        rateTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_view_list_white_24dp, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(rateTab);

        TextView compareTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        compareTab.setText(R.string.compare_art_tab_label);
        compareTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grade_white_24dp, 0, 0);
        mTabLayout.getTabAt(2).setCustomView(compareTab);

        TextView showTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        showTab.setText(R.string.the_show_tab_label);
        showTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event_seat_white_24dp, 0, 0);
        mTabLayout.getTabAt(3).setCustomView(showTab);

        TextView voteTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        voteTab.setText(R.string.vote_tab_label);
        voteTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_poll_white_24dp, 0, 0);
        mTabLayout.getTabAt(4).setCustomView(voteTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }


    /****************************************************************************
     * Fragment Communication Methods
     * http://developer.android.com/training/basics/fragments/communicating.html
     ****************************************************************************/

    // communication from the CompareArtWorkFragment
    public void onCompareArtFragmentInteraction(Uri uri) {

    }

    // communication from the ShowFragment
    public void onShowFragmentInteraction(Uri uri) {

    }

    // communication from the VoteFragment
    public void onVoteFragmentInteraction(Uri uri) {

    }

    // communication from the ArtWorkFragment
    public void onArtWorkFragmentInteraction(Uri uri) {

    }


    // communication from the ArtWorkListFragment
    public void onArtWorkListFragmentInteraction(ArtWork artWork) {
        Intent intent = ArtWorkPagerActivity.newIntent(this, artWork.getId());
        startActivity(intent);
    }

    // Communication from the UpdateArtWorkFragment
    public void onArtWorkDataSourceUpdate() {

        // Communicate to the ArtWorkListFragment that the data source has updated
        SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();
        ArtWorkListFragment artWorkListFragment = (ArtWorkListFragment) sectionsPagerAdapter.getItem(1);

        if (artWorkListFragment != null) {
            artWorkListFragment.updateUI();
        }
    }
}
