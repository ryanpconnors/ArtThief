package com.ryanpconnors.artthief;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ryanpconnors.artthief.compare.CompareArtFragment;
import com.ryanpconnors.artthief.rate.RateArtFragment;
import com.ryanpconnors.artthief.show.ShowFragment;
import com.ryanpconnors.artthief.update.UpdateArtFragment;
import com.ryanpconnors.artthief.vote.VoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements UpdateArtFragment.OnUpdateArtFragmentInteractionListener,
        CompareArtFragment.OnCompareArtFragmentInteractionListener,
        RateArtFragment.OnRateArtFragmentInteractionListener,
        ShowFragment.OnShowFragmentInteractionListener,
        VoteFragment.OnVoteFragmentInteractionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


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

    }

    private void setupViewPager(ViewPager viewPager) {

        // Create the adapter that will return a fragment for each of the sections of the activity.
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(UpdateArtFragment.newInstance(), getString(R.string.update_art_title));
        adapter.addFragment(RateArtFragment.newInstance(), getString(R.string.rate_art_title));
        adapter.addFragment(CompareArtFragment.newInstance(), getString(R.string.compare_art_title));
        adapter.addFragment(ShowFragment.newInstance(), getString(R.string.the_show_title));
        adapter.addFragment(VoteFragment.newInstance(), getString(R.string.vote_title));

        viewPager.setAdapter(adapter);
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

        if (id == R.id.action_settings) {
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
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

    public void onUpdateArtFragmentInteraction(Uri uri) {
        // communicate with the UpdateArtFragment
    }

    public void onCompareArtFragmentInteraction(Uri uri) {
        // communicate with the CompareArtFragment
    }

    public void onRateArtFragmentInteraction(Uri uri) {
        // communicate with the RateArtFragment
    }

    public void onShowFragmentInteraction(Uri uri) {
        // communicate with the ShowFragment
    }

    public void onVoteFragmentInteraction(Uri uri) {
        // communicate with the VoteFragment
    }

}
