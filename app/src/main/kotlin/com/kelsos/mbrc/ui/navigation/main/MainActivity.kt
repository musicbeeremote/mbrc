package com.kelsos.mbrc.ui.navigation.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.kelsos.mbrc.R
import com.kelsos.mbrc.UpdateRequiredActivity
import com.kelsos.mbrc.changelog.ChangelogDialog
import com.kelsos.mbrc.content.active_status.PlayerState
import com.kelsos.mbrc.content.active_status.PlayerState.State
import com.kelsos.mbrc.content.active_status.Repeat
import com.kelsos.mbrc.content.active_status.Repeat.Mode
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.databinding.ActivityMainBinding
import com.kelsos.mbrc.databinding.ContentMainBinding
import com.kelsos.mbrc.events.OnMainFragmentOptionsInflated
import com.kelsos.mbrc.events.ShuffleChange
import com.kelsos.mbrc.events.ShuffleChange.ShuffleState
import com.kelsos.mbrc.events.UpdatePositionEvent
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import com.kelsos.mbrc.ui.navigation.main.LfmRating.Rating
import com.kelsos.mbrc.ui.navigation.main.ProgressSeekerHelper.ProgressUpdate
import com.squareup.picasso.Picasso
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivity : BaseActivity(), MainView, ProgressUpdate {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

  // Injects
  @Inject
  lateinit var presenter: MainViewPresenter

  @Inject
  lateinit var progressHelper: ProgressSeekerHelper

  private var mShareActionProvider: ShareActionProvider? = null

  private var changeLogDialog: AlertDialog? = null
  private var outOfDateDialog: AlertDialog? = null

  private var menu: Menu? = null
  private var volumeChangeListener: SeekBarThrottler? = null
  private var positionChangeListener: SeekBarThrottler? = null

  private lateinit var scope: Scope
  private lateinit var binding: ContentMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), MainModule())
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementsUseOverlay = false
    super.onCreate(savedInstanceState)
    val mainBinding = ActivityMainBinding.inflate(layoutInflater)
    binding = ContentMainBinding.bind(mainBinding.root)
    setContentView(mainBinding.root)
    Toothpick.inject(this, scope)

    binding.mainMuteButton.setOnClickListener { presenter.mute() }
    binding.mainShuffleButton.setOnClickListener { presenter.shuffle() }
    binding.mainRepeatButton.setOnClickListener { presenter.repeat() }

    binding.mainButtonPlayPause.setOnClickListener { presenter.play() }
    binding.mainButtonPlayPause.setOnLongClickListener { presenter.stop() }
    binding.mainButtonPrevious.setOnClickListener { presenter.previous() }
    binding.mainButtonNext.setOnClickListener { presenter.next() }
    binding.trackInfoArea.setOnClickListener { navigate(R.id.nav_now_playing) }

    super.setup()
    presenter.attach(this)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    if (intent?.getBooleanExtra(EXIT_APP, false) == true) {
      exitApplication()
      return
    }
  }

  override fun showChangeLog() {
    changeLogDialog = ChangelogDialog.show(this, R.raw.changelog)
  }

  override fun notifyPluginOutOfDate() {
    val snackBar = Snackbar.make(
      binding.root,
      R.string.main__dialog_plugin_outdated_message,
      Snackbar.LENGTH_INDEFINITE
    )
    snackBar.setAction(android.R.string.ok) { snackBar.dismiss() }
    snackBar.show()
  }

  override fun showPluginUpdateRequired(minimumRequired: String) {
    val intent = Intent(this, UpdateRequiredActivity::class.java)
    intent.putExtra(UpdateRequiredActivity.VERSION, minimumRequired)
    startActivity(intent)
  }

  override fun onStart() {
    super.onStart()

    if (!presenter.isAttached) {
      presenter.attach(this)
    }

    progressHelper.setProgressListener(this)
    volumeChangeListener = SeekBarThrottler { presenter.changeVolume(it) }
    positionChangeListener = SeekBarThrottler { presenter.seek(it) }
    binding.mainVolumeSeeker.setOnSeekBarChangeListener(volumeChangeListener)
    binding.mainTrackProgressSeeker.setOnSeekBarChangeListener(positionChangeListener)
    binding.mainArtistLabel.isSelected = true
    binding.mainTitleLabel.isSelected = true
    binding.mainLabelAlbum.isSelected = true

    if (!isConnected) {
      onConnectClick()
    }
  }

  override fun onResume() {
    super.onResume()
    presenter.requestNowPlayingPosition()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_lastfm_scrobble -> {
        presenter.toggleScrobbling()
        true
      }
      R.id.menu_rating_dialog -> {
        val ratingDialog = RatingDialogFragment()
        ratingDialog.show(supportFragmentManager, "RatingDialog")
        true
      }
      R.id.menu_lastfm_love -> {
        presenter.lfmLove()
      }
      else -> false
    }
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
    bus.unregister(this)
    progressHelper.setProgressListener(null)
    volumeChangeListener?.terminate()
    volumeChangeListener = null
    positionChangeListener?.terminate()
    positionChangeListener = null
    binding.mainVolumeSeeker.setOnSeekBarChangeListener(null)
    binding.mainTrackProgressSeeker.setOnSeekBarChangeListener(null)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    this.menu = menu
    val shareItem = menu.findItem(R.id.actionbar_share)
    mShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
    mShareActionProvider!!.setShareIntent(shareIntent)
    bus.post(OnMainFragmentOptionsInflated())
    return super.onCreateOptionsMenu(menu)
  }

  private val shareIntent: Intent
    get() {
      val shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      val payload = String.format(
        "Now Playing: %s - %s",
        binding.mainLabelAlbum.text,
        binding.mainTitleLabel.text
      )
      shareIntent.putExtra(Intent.EXTRA_TEXT, payload)
      return shareIntent
    }

  override fun updateCover(path: String) {
    val file = File(path)

    if (!file.exists()) {
      binding.mainAlbumCoverImageView.setImageResource(R.drawable.ic_image_no_cover)
      return
    }

    val dimens = getDimens()
    Picasso.get()
      .load(file)
      .noFade()
      .error(R.drawable.ic_image_no_cover)
      .config(Bitmap.Config.RGB_565)
      .resize(dimens, dimens)
      .centerCrop()
      .into(binding.mainAlbumCoverImageView)
  }

  override fun updateShuffleState(@ShuffleState shuffleState: String) {
    val shuffle = ShuffleChange.OFF != shuffleState
    val autoDj = ShuffleChange.AUTODJ == shuffleState

    val color = getColor(if (shuffle) R.color.accent else R.color.button_dark)
    binding.mainShuffleButton.setColorFilter(color)
    val resId = if (autoDj) R.drawable.ic_headset_black_24dp else R.drawable.ic_shuffle_black_24dp
    binding.mainShuffleButton.setImageResource(resId)
  }

  override fun updateRepeat(@Mode mode: String) {
    @ColorRes var colorId = R.color.accent
    @DrawableRes var resId = R.drawable.ic_repeat_black_24dp

    //noinspection StatementWithEmptyBody
    when {
      Repeat.ALL.equals(mode, ignoreCase = true) -> Unit
      Repeat.ONE.equals(mode, ignoreCase = true) -> resId = R.drawable.ic_repeat_one_black_24dp
      else -> colorId = R.color.button_dark
    }

    val repeatButton = binding.mainRepeatButton
    repeatButton.setImageResource(resId)
    repeatButton.setColorFilter(getColor(colorId))
  }

  override fun updateVolume(volume: Int, mute: Boolean) {
    binding.mainVolumeSeeker.progress = volume
    val muteButton = binding.mainMuteButton
    muteButton.setColorFilter(getColor(R.color.button_dark))
    val resId = if (mute) {
      R.drawable.ic_volume_off_black_24dp
    } else {
      R.drawable.ic_volume_up_black_24dp
    }
    muteButton.setImageResource(resId)
  }

  override fun updatePlayState(@State state: String) {
    val accentColor = getColor(R.color.accent)
    val tag = tag(state)

    if (binding.mainButtonPlayPause.tag == tag) {
      return
    }
    val progressBar = binding.mainTrackProgressSeeker
    @DrawableRes val resId: Int = when (state) {
      PlayerState.PLAYING -> {
        /* Start the animation if the track is playing*/
        presenter.requestNowPlayingPosition()
        trackProgressAnimation(progressBar.progress.toLong(), progressBar.max.toLong())
        R.drawable.ic_pause_circle_filled_black_24dp
      }
      PlayerState.PAUSED -> {
        /* Stop the animation if the track is paused*/
        progressHelper.stop()
        R.drawable.ic_play_circle_filled_black_24dp
      }
      PlayerState.STOPPED -> {
        /* Stop the animation if the track is paused*/
        progressHelper.stop()
        activateStoppedState()
        R.drawable.ic_play_circle_filled_black_24dp
      }
      else -> {
        R.drawable.ic_play_circle_filled_black_24dp
      }
    }

    binding.mainButtonPlayPause.setColorFilter(accentColor)
    binding.mainButtonPlayPause.setImageResource(resId)
    binding.mainButtonPlayPause.tag = tag
  }

  /**
   * Starts the progress animation when called. If It was previously running then it restarts it.
   */
  private fun trackProgressAnimation(current: Long, total: Long) {
    progressHelper.stop()

    val tag = binding.mainButtonPlayPause.tag
    if (STOPPED == tag || PAUSED == tag) {
      return
    }

    progressHelper.update(current, total)
  }

  private fun activateStoppedState() {
    binding.mainTrackProgressSeeker.progress = 0
    binding.mainTrackProgressCurrent.text = getString(R.string.playback_progress, 0, 0)
  }

  override fun updateTrackInfo(info: TrackInfo) {
    binding.mainArtistLabel.text = info.artist
    binding.mainTitleLabel.text = info.title
    binding.mainLabelAlbum.text = if (TextUtils.isEmpty(info.year)) info.album
    else String.format("%s [%s]", info.album, info.year)

    if (mShareActionProvider != null) {
      mShareActionProvider!!.setShareIntent(shareIntent)
    }
  }

  override fun updateConnection(status: Int) {
    if (status == Connection.OFF) {
      progressHelper.stop()
      activateStoppedState()
    }
  }

  /**
   * Responsible for updating the displays and seekbar responsible for the display of the track
   * duration and the
   * current progress of playback
   */

  override fun updateProgress(duration: UpdatePositionEvent) {
    updateProgress(duration.current, duration.total)
  }

  private fun updateProgress(
    current: Long,
    total: Long,
  ) {
    var currentSeconds = current / 1000
    var totalSeconds = total / 1000

    val currentMinutes = currentSeconds / 60
    val totalMinutes = totalSeconds / 60

    currentSeconds %= 60
    totalSeconds %= 60
    val finalTotalSeconds = totalSeconds
    val finalCurrentSeconds = currentSeconds

    binding.mainTrackDurationTotal.text = getString(
      R.string.playback_progress,
      totalMinutes,
      finalTotalSeconds
    )
    binding.mainTrackProgressCurrent.text = getString(
      R.string.playback_progress,
      currentMinutes,
      finalCurrentSeconds
    )

    binding.mainTrackProgressSeeker.max = total.toInt()
    binding.mainTrackProgressSeeker.progress = current.toInt()

    trackProgressAnimation(current, total)
  }

  override fun updateScrobbleStatus(active: Boolean) {
    if (menu == null) {
      return
    }
    val scrobbleMenuItem = menu!!.findItem(R.id.menu_lastfm_scrobble) ?: return

    scrobbleMenuItem.isChecked = active
  }

  override fun updateLfmStatus(@Rating status: Int) {
    if (menu == null) {
      return
    }
    val favoriteMenuItem = menu!!.findItem(R.id.menu_lastfm_love) ?: return

    when (status) {
      LfmRating.LOVED -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp)
      else -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp)
    }
  }

  override fun active(): Int {
    return R.id.nav_home
  }

  override fun progress(position: Long, duration: Long) {
    val progressBar = binding.mainTrackProgressSeeker
    val currentProgress = progressBar.progress / 1000
    val currentMinutes = currentProgress / 60
    val currentSeconds = currentProgress % 60

    progressBar.progress = progressBar.progress + 1000
    binding.mainTrackProgressCurrent.text = getString(
      R.string.playback_progress,
      currentMinutes,
      currentSeconds
    )
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finishAfterTransition()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    if (isFinishing) {
      // when we leave the presenter flow,
      // we close its scope
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    outOfDateDialog?.dismiss()
    changeLogDialog?.dismiss()
    super.onDestroy()
  }

  companion object {
    private const val PAUSED = "Paused"
    private const val STOPPED = "Stopped"
    private const val PLAYING = "Playing"

    fun tag(@PlayerState.State state: String): String = when (state) {
      PlayerState.PLAYING -> PLAYING
      PlayerState.PAUSED -> PAUSED
      else -> STOPPED
    }
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}
