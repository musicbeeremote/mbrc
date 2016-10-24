package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.OnClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.utilities.LibrarySyncManager
import toothpick.Toothpick
import javax.inject.Inject

class DebugActivity : AppCompatActivity() {

  @Inject lateinit var manager: LibrarySyncManager

  @OnClick(R.id.debug_action) internal fun onAction() {
    manager.sync()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_debug)
    ButterKnife.bind(this)
    val scope = Toothpick.openScopes(application, this)
    Toothpick.inject(this, scope)
  }

  override fun onDestroy() {
    super.onDestroy()
    Toothpick.closeScope(this)
  }
}
