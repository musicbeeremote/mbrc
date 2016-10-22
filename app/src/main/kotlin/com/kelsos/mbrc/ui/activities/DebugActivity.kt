package com.kelsos.mbrc.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.OnClick
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.utilities.LibrarySyncManager
import roboguice.RoboGuice

class DebugActivity : AppCompatActivity() {

  @Inject private lateinit var manager: LibrarySyncManager

  @OnClick(R.id.debug_action) internal fun onAction() {
    manager.sync()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_debug)
    ButterKnife.bind(this)
    RoboGuice.getInjector(this).injectMembers(this)
  }
}
