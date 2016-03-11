package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import roboguice.RoboGuice;

public class LibraryActivity extends BaseActivity {
  @Bind(R.id.library_pager) ViewPager pager;
  @Bind(R.id.library_pager_tabs) TabLayout tabLayout;
  private BrowsePagerAdapter adapter;

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return false;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_library);
    RoboGuice.getInjector(this).injectMembers(this);
    initialize();
    setCurrentSelection(R.id.drawer_menu_library);
    ButterKnife.bind(this);
    adapter = new BrowsePagerAdapter(this);
    pager.setAdapter(adapter);
    tabLayout.setupWithViewPager(pager);
  }

  @Override public void onBackPressed() {
    ActivityCompat.finishAfterTransition(this);
  }
}
