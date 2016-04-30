package com.kelsos.mbrc.ui.navigation

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.BrowsePagerAdapter
import com.kelsos.mbrc.presenters.LibraryActivityPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.views.LibraryActivityView
import roboguice.RoboGuice

class LibraryActivity : BaseActivity(), LibraryActivityView {
  @BindView(R.id.library_pager) lateinit var pager: ViewPager
  @BindView(R.id.library_pager_tabs) lateinit var tabLayout: TabLayout

  @Inject private lateinit var adapter: BrowsePagerAdapter
  @Inject private lateinit var presenter: LibraryActivityPresenter

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return false
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_library)
    RoboGuice.getInjector(this).injectMembers(this)
    presenter.bind(this)
    initialize()
    setCurrentSelection(R.id.drawer_menu_library)
    ButterKnife.bind(this)
    pager.adapter = adapter
    tabLayout.setupWithViewPager(pager)
    presenter.checkLibrary()
  }

  override fun onPause() {
    super.onPause()
    presenter.onPause()
  }

  override fun onResume() {
    super.onResume()
    presenter.onResume()
  }

  override fun onBackPressed() {
    ActivityCompat.finishAfterTransition(this)
  }
}
