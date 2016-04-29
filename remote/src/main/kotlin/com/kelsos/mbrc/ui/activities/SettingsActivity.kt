package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.Bind
import butterknife.ButterKnife
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.fragments.SettingsFragment
import com.kelsos.mbrc.utilities.RxBus

class SettingsActivity : AppCompatActivity() {

  @Bind(R.id.toolbar) internal lateinit var toolbar: Toolbar
  @Inject private lateinit var bus: RxBus

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.settings_activity)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)

    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true)
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setTitle(R.string.menu_settings)
    }


    val fragment = SettingsFragment.newInstance(bus)
    supportFragmentManager.beginTransaction().replace(R.id.content_wrapper, fragment).commitAllowingStateLoss()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
