package com.kelsos.mbrc.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import com.kelsos.mbrc.R
import com.kelsos.mbrc.ui.activities.BaseActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule

class SettingsActivity : BaseActivity() {

  private lateinit var scope: Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this))
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setContentView(R.layout.activity_settings)

    setupToolbar(getString(R.string.nav_settings))

    val fragment = SettingsFragment.newInstance()
    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
