package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
import com.kelsos.mbrc.presenters.LibraryActivityPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.views.LibraryActivityView;
import roboguice.RoboGuice;

public class LibraryActivity extends BaseActivity implements LibraryActivityView {
  @Bind(R.id.library_pager) ViewPager pager;
  @Bind(R.id.library_pager_tabs) TabLayout tabLayout;

  @Inject private BrowsePagerAdapter adapter;
  @Inject private LibraryActivityPresenter presenter;

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return false;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_library);
    RoboGuice.getInjector(this).injectMembers(this);
    presenter.bind(this);
    initialize();
    setCurrentSelection(R.id.drawer_menu_library);
    ButterKnife.bind(this);
    pager.setAdapter(adapter);
    tabLayout.setupWithViewPager(pager);
    presenter.checkLibrary();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void onBackPressed() {
    ActivityCompat.finishAfterTransition(this);
  }
}
