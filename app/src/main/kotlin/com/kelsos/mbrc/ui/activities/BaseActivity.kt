package com.kelsos.mbrc.ui.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import javax.inject.Inject
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.controller.Controller
import com.kelsos.mbrc.events.ChangeWebSocketStatusEvent
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.ui.DisplayDialog
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.ui.dialogs.SetupDialogFragment
import com.kelsos.mbrc.ui.dialogs.UpgradeDialogFragment
import com.kelsos.mbrc.ui.navigation.LibraryActivity
import com.kelsos.mbrc.ui.navigation.LyricsActivity
import com.kelsos.mbrc.ui.navigation.MainActivity
import com.kelsos.mbrc.ui.navigation.NowPlayingActivity
import com.kelsos.mbrc.ui.navigation.PlaylistListActivity
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.viewmodels.ConnectionStatusModel
import roboguice.RoboGuice
import rx.Subscription
import timber.log.Timber

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  private lateinit var toolbar: Toolbar
  private lateinit var drawer: DrawerLayout
  private lateinit var navigationView: NavigationView
  @Inject private lateinit var rxBus: RxBus
  @Inject private lateinit var handler: Handler
  @Inject private lateinit var model: ConnectionStatusModel
  private var toggle: ActionBarDrawerToggle? = null
  private var mDialog: DialogFragment? = null
  private val subscription: Subscription? = null

  /**
   * Sends an event object.
   */
  protected fun post(`object`: Any) {
    rxBus.post(`object`)
  }

  private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.name == service.service.className) {
        return true
      }
    }
    return false
  }

  protected fun setCurrentSelection(@IdRes id: Int) {
    navigationView.menu.findItem(id).isChecked = true
  }

  protected fun initialize(toolbar: Toolbar,
                           drawer: DrawerLayout,
                           navigation: NavigationView) {
    this.toolbar = toolbar
    this.drawer = drawer
    this.navigationView = navigation
    setSupportActionBar(toolbar)
    navigationView.setNavigationItemSelectedListener(this)

    if (BuildConfig.DEBUG) {
      navigationView.menu.add(DEBUG_ITEM_GROUP, DEBUG_ITEM_ID, DEBUG_ORDER, R.string.debug)
    }

    if (!isMyServiceRunning(Controller::class.java)) {
      startService(Intent(this, Controller::class.java))
    }

    toggle = ActionBarDrawerToggle(this,
        drawer,
        toolbar,
        R.string.drawer_open,
        R.string.drawer_close)
    toggle!!.syncState()

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)

  }

  override fun onResume() {
    super.onResume()
    rxBus.registerOnMain(this,
        ConnectionStatusChangeEvent::class.java,
        { this.handleConnectionStatusChange(it) })
    rxBus.registerOnMain(this, DisplayDialog::class.java, { this.showSetupDialog(it) })
    rxBus.registerOnMain(this, NotifyUser::class.java, { this.handleUserNotification(it) })

    updateStatus(model.status)
  }

  override fun onPause() {
    super.onPause()
    rxBus.unregister(this)
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
    super.onCreate(savedInstanceState, persistentState)
    RoboGuice.getInjector(this).injectMembers(this)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    toggle!!.onConfigurationChanged(newConfig)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    toggle!!.syncState()
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> return true
      KeyEvent.KEYCODE_VOLUME_DOWN -> return true
      else -> return super.onKeyUp(keyCode, event)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return super.onOptionsItemSelected(item)
  }

  fun showSetupDialog(event: DisplayDialog) {
    if (mDialog != null) {
      return
    }
    if (event.dialogType == DisplayDialog.SETUP) {
      mDialog = SetupDialogFragment()
      mDialog!!.show(supportFragmentManager, "SetupDialogFragment")
    } else if (event.dialogType == DisplayDialog.UPGRADE) {
      mDialog = UpgradeDialogFragment()
      mDialog!!.show(supportFragmentManager, "UpgradeDialogFragment")
    } else if (event.dialogType == DisplayDialog.INSTALL) {
      mDialog = UpgradeDialogFragment()
      (mDialog as UpgradeDialogFragment).setNewInstall(true)
      mDialog!!.show(supportFragmentManager, "UpgradeDialogFragment")
    }
  }

  private fun handleUserNotification(event: NotifyUser) {
    val message = if (event.isFromResource) getString(event.resId) else event.message
    Snackbar.make(toolbar, message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    when (keyCode) {
      KeyEvent.KEYCODE_VOLUME_UP -> {
        rxBus.post(MessageEvent.newInstance(UserInputEventType.KeyVolumeUp))
        return true
      }
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        rxBus.post(MessageEvent.newInstance(UserInputEventType.KeyVolumeDown))
        return true
      }
      else -> return super.onKeyDown(keyCode, event)
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    handler.postDelayed({ navigate(item.itemId) }, NAVIGATION_DELAY.toLong())
    drawer.closeDrawer(GravityCompat.START)
    return true
  }

  fun navigate(id: Int) {
    if (id == R.id.drawer_menu_home) {
      createBackStack(Intent(this, MainActivity::class.java))
    } else if (id == R.id.drawer_menu_library) {
      createBackStack(Intent(this, LibraryActivity::class.java))
    } else if (id == R.id.drawer_menu_playlist) {
      createBackStack(Intent(this, PlaylistListActivity::class.java))
    } else if (id == R.id.drawer_menu_now_playing) {
      createBackStack(Intent(this, NowPlayingActivity::class.java))
    } else if (id == R.id.drawer_menu_lyrics) {
      createBackStack(Intent(this, LyricsActivity::class.java))
    } else if (id == R.id.drawer_menu_settings) {
      createBackStack(Intent(this, SettingsActivity::class.java))
    } else if (id == R.id.drawer_menu_exit) {
      onExitClicked()
    } else if (id == R.id.drawer_menu_connect) {
      onConnectClick()
    } else if (id == R.id.drawer_menu_help) {
      createBackStack(Intent(this, HelpActivity::class.java))
    } else if (id == DEBUG_ITEM_ID) {
      createBackStack(Intent(this, DebugActivity::class.java))
    }
  }

  private fun createBackStack(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      val builder = TaskStackBuilder.create(this)
      builder.addNextIntentWithParentStack(intent)
      builder.startActivities()
    } else {
      startActivity(intent)
      finish()
    }
  }

  private fun onConnectClick() {
    rxBus.post(ChangeWebSocketStatusEvent.newInstance(ChangeWebSocketStatusEvent.CONNECT))
  }

  private fun onExitClicked() {
    Timber.v("[Menu] User pressed exit")
    stopService(Intent(this, Controller::class.java))
    finish()
  }

  private fun handleConnectionStatusChange(change: ConnectionStatusChangeEvent) {

    val status = change.status
    Timber.v("Connection event received %s", status)
    model.status = status
    updateStatus(status)
  }

  private fun updateStatus(@Connection.Status status: Long) {
    val item = navigationView.menu.findItem(R.id.drawer_menu_connect)

    if (item == null) {
      Timber.v("Connection event received but view item null")
      return
    }

    if (status == Connection.ON) {
      item.setTitle(R.string.drawer_connection_status_active)
    } else {
      item.setTitle(R.string.drawer_connection_status_off)
    }
  }

  companion object {

    const val NAVIGATION_DELAY = 250
    const val DEBUG_ORDER = 999
    const val DEBUG_ITEM_ID = 890
    const val DEBUG_ITEM_GROUP = 0

    /**
     * This utility method handles Up navigation intents by searching for a parent activity and
     * navigating there if defined. When using this for an activity make sure to define both the
     * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
     * when the activity has a single parent activity. If the activity doesn't have a single parent
     * activity then don't define one and this method will use back button functionality. If "Up"
     * functionality is still desired for activities without parents then use
     * `syntheticParentActivity` to define one dynamically.

     * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar
     * in Material Design guidelines.

     * @param currentActivity Activity in use when navigate Up action occurred.
     * *
     * @param syntheticParentActivity Parent activity to use when one is not already configured.
     */
    fun navigateUpOrBack(currentActivity: Activity, syntheticParentActivity: Class<out Activity>?) {
      // Retrieve parent activity from AndroidManifest.
      var intent: Intent? = NavUtils.getParentActivityIntent(currentActivity)

      // Synthesize the parent activity when a natural one doesn't exist.
      if (intent == null && syntheticParentActivity != null) {
        try {
          intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity)
        } catch (e: PackageManager.NameNotFoundException) {
          e.printStackTrace()
        }

      }

      if (intent == null) {
        // No parent defined in manifest. This indicates the activity may be used by
        // in multiple flows throughout the app and doesn't have a strict parent. In
        // this case the navigation up button should act in the same manner as the
        // back button. This will result in users being forwarded back to other
        // applications if currentActivity was invoked from another application.
        currentActivity.onBackPressed()
      } else {
        if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
          // Need to synthesize a backstack since currentActivity was probably invoked by a
          // different app. The preserves the "Up" functionality within the app according to
          // the activity hierarchy defined in AndroidManifest.xml via parentActivity
          // attributes.
          val builder = TaskStackBuilder.create(currentActivity)
          builder.addNextIntentWithParentStack(intent)
          builder.startActivities()
        } else {
          // Navigate normally to the manifest defined "Up" activity.
          NavUtils.navigateUpTo(currentActivity, intent)
        }
      }
    }
  }
}
