package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {

  @BindView(R.id.toolbar) internal lateinit var toolbar: Toolbar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.settings_activity)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)

    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setTitle(R.string.menu_settings)

    val fragment = SettingsFragment.newInstance()
    supportFragmentManager.beginTransaction()
        .replace(R.id.content_wrapper, fragment)
        .commitAllowingStateLoss()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
