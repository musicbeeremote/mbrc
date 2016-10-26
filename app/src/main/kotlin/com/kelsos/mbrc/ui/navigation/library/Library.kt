package com.kelsos.mbrc.ui.navigation.library

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.BrowsePagerAdapter
import com.kelsos.mbrc.ui.activities.BaseActivity
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class Library : BaseActivity(), LibraryView {
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.drawer_layout) lateinit  var drawer: DrawerLayout
  @BindView(R.id.navigation_view) lateinit  var navigationView: NavigationView
  @BindView(R.id.library_pager) lateinit var pager: ViewPager
  @BindView(R.id.library_pager_tabs) lateinit var tabLayout: TabLayout

  @Inject lateinit var adapter: BrowsePagerAdapter
  @Inject lateinit var presenter: LibraryActivityPresenter

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return false
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_library)
    val scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), LibraryModule())
    Toothpick.inject(this, scope)
    presenter.attach(this)
    ButterKnife.bind(this)
    initialize(toolbar,drawer,navigationView)
    setCurrentSelection(R.id.drawer_menu_library)

    pager.adapter = adapter
    tabLayout.setupWithViewPager(pager)
    presenter.checkLibrary()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }

  override fun onBackPressed() {
    ActivityCompat.finishAfterTransition(this)
  }
}
