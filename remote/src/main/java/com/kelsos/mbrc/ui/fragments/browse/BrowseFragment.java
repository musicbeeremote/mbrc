package com.kelsos.mbrc.ui.fragments.browse;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
import roboguice.fragment.RoboFragment;

public class BrowseFragment extends RoboFragment {
  @Bind(R.id.library_pager) ViewPager pager;
  @Bind(R.id.library_pager_tabs) TabLayout tabLayout;
  private BrowsePagerAdapter mAdapter;

  public static BrowseFragment newInstance() {
    return new BrowseFragment();
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_browse, container, false);
    ButterKnife.bind(this, view);
    pager.setAdapter(mAdapter);
    tabLayout.setupWithViewPager(pager);
    return view;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return false;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    mAdapter = new BrowsePagerAdapter(getActivity());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mAdapter = null;
  }
}
