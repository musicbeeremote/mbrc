package com.kelsos.mbrc.ui.navigation.main


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.ShareActionProvider
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.Repeat.Mode
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.events.OnMainFragmentOptionsInflated
import com.kelsos.mbrc.events.ShuffleChange
import com.kelsos.mbrc.events.ShuffleChange.ShuffleState
import com.kelsos.mbrc.events.UpdatePositionEvent
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.ui.activities.BaseNavigationActivity
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import com.kelsos.mbrc.ui.navigation.main.LfmRating.Rating
import com.kelsos.mbrc.ui.navigation.main.ProgressSeekerHelper.ProgressUpdate
import com.squareup.picasso.Picasso
import kotterknife.bindView
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivity : BaseNavigationActivity(), MainView, ProgressUpdate {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  // Injects
  @Inject
  lateinit var presenter: MainViewPresenter
  @Inject
  lateinit var progressHelper: ProgressSeekerHelper
  // Inject elements of the view
  private val artistLabel: TextView by bindView(R.id.main_artist_label)
  private val titleLabel: TextView by bindView(R.id.main_title_label)
  private val albumLabel: TextView by bindView(R.id.main_label_album)
  private val trackProgressCurrent: TextView by bindView(R.id.main_track_progress_current)
  private val trackDuration: TextView by bindView(R.id.main_track_duration_total)
  private val playPauseButton: ImageButton by bindView(R.id.main_button_play_pause)
  private val volumeBar: SeekBar by bindView(R.id.main_volume_seeker)
  private val progressBar: SeekBar by bindView(R.id.main_track_progress_seeker)
  private val muteButton: ImageButton by bindView(R.id.main_mute_button)
  private val shuffleButton: ImageButton by bindView(R.id.main_shuffle_button)
  private val repeatButton: ImageButton by bindView(R.id.main_repeat_button)
  private val albumCover: ImageView by bindView(R.id.main_album_cover_image_view)
  private val previousButton: ImageButton by bindView(R.id.main_button_previous)
  private val nextButton: ImageButton by bindView(R.id.main_button_next)
  private val trackInfo: View by bindView(R.id.track_info_area)

  private var mShareActionProvider: ShareActionProvider? = null

  private var changeLogDialog: AlertDialog? = null
  private var outOfDateDialog: AlertDialog? = null

  private var menu: Menu? = null
  private var volumeChangeListener: SeekBarThrottler? = null

  private var positionChangeListener: SeekBarThrottler? = null
  private lateinit var scope: Scope

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), MainModule())
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    Toothpick.inject(this, scope)
    playPauseButton.setOnClickListener { presenter.play() }
    playPauseButton.setOnLongClickListener { presenter.stop() }
    previousButton.setOnClickListener { presenter.previous() }
    nextButton.setOnClickListener { presenter.next() }
    muteButton.setOnClickListener { presenter.mute() }
    shuffleButton.setOnClickListener { presenter.shuffle() }
    repeatButton.setOnClickListener { presenter.repeat() }
    trackInfo.setOnClickListener { navigate(R.id.nav_now_playing) }

    super.setup()
    presenter.attach(this)
    presenter.load()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    if (intent?.getBooleanExtra(EXIT_APP, false) == true) {
      exitApplication()
      return
    }
  }

  override fun showChangeLog() {
    changeLogDialog = AlertDialog.Builder(this)
      .setTitle(R.string.main__dialog_change_log)
      .setView(R.layout.change_log_dialog)
      .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
      .show()
  }

  override fun notifyPluginOutOfDate() {
    outOfDateDialog = AlertDialog.Builder(this)
      .setTitle(R.string.main__dialog_plugin_outdated_title)
      .setMessage(R.string.main__dialog_plugin_outdated_message)
      .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
      .show()
  }

  override fun onStart() {
    super.onStart()

    if (!presenter.isAttached) {
      presenter.attach(this)
      presenter.load()
    }

    progressHelper.setProgressListener(this)
    volumeChangeListener = SeekBarThrottler { presenter.changeVolume(it) }
    positionChangeListener = SeekBarThrottler { presenter.seek(it) }
    volumeBar.setOnSeekBarChangeListener(volumeChangeListener)
    progressBar.setOnSeekBarChangeListener(positionChangeListener)
    artistLabel.isSelected = true
    titleLabel.isSelected = true
    albumLabel.isSelected = true
  }

  override fun onResume() {
    super.onResume()
    presenter.requestNowPlayingPosition()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_lastfm_scrobble -> {
        presenter.toggleScrobbling()
        return true
      }
      R.id.menu_rating_dialog -> {
        val ratingDialog = RatingDialogFragment()
        ratingDialog.show(supportFragmentManager, "RatingDialog")
        return true
      }
      R.id.menu_lastfm_love -> {
        return presenter.lfmLove()
      }
      else -> return false
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
    volumeBar.setOnSeekBarChangeListener(null)
    progressBar.setOnSeekBarChangeListener(null)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    this.menu = menu
//    val shareItem = menu.findItem(R.id.actionbar_share)
//    mShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
//    mShareActionProvider!!.setShareIntent(shareIntent)
    bus.post(OnMainFragmentOptionsInflated())
    return super.onCreateOptionsMenu(menu)
  }

  private val shareIntent: Intent
    get() {
      val shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      val payload = String.format("Now Playing: %s - %s", artistLabel.text, titleLabel.text)
      shareIntent.putExtra(Intent.EXTRA_TEXT, payload)
      return shareIntent
    }

  override fun updateCover(path: String) {
    val file = File(path)

    if (!file.exists()) {
      albumCover.setImageResource(R.drawable.ic_image_no_cover)
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
      .into(albumCover)

  }

  override fun updateShuffleState(@ShuffleState shuffleState: String) {
    val shuffle = ShuffleChange.OFF != shuffleState
    val autoDj = ShuffleChange.AUTODJ == shuffleState

    val color = ContextCompat.getColor(this, if (shuffle) R.color.accent else R.color.button_dark)
    shuffleButton.setColorFilter(color)

    shuffleButton.setImageResource(if (autoDj) R.drawable.ic_headset_black_24dp else R.drawable.ic_shuffle_black_24dp)
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

    val color = ContextCompat.getColor(this, colorId)
    repeatButton.setImageResource(resId)
    repeatButton.setColorFilter(color)
  }

  override fun updateVolume(volume: Int, mute: Boolean) {
    volumeBar.progress = volume
    val color = ContextCompat.getColor(this, R.color.button_dark)
    muteButton.setColorFilter(color)
    muteButton.setImageResource(if (mute) R.drawable.ic_volume_off_black_24dp else R.drawable.ic_volume_up_black_24dp)
  }

  override fun updatePlayState(@State state: String) {
    val accentColor = ContextCompat.getColor(this, R.color.accent)
    @DrawableRes val resId: Int
    val tag: String

    when (state) {
      PlayerState.PLAYING -> {
        resId = R.drawable.ic_pause_circle_filled_black_24dp
        tag = "Playing"
        /* Start the animation if the track is playing*/
        presenter.requestNowPlayingPosition()
        trackProgressAnimation(progressBar.progress, progressBar.max)
      }
      PlayerState.PAUSED -> {
        resId = R.drawable.ic_play_circle_filled_black_24dp
        tag = PAUSED
        /* Stop the animation if the track is paused*/
        progressHelper.stop()
      }
      PlayerState.STOPPED -> {
        resId = R.drawable.ic_play_circle_filled_black_24dp
        tag = STOPPED
        /* Stop the animation if the track is paused*/
        progressHelper.stop()
        activateStoppedState()
      }
      else -> {
        resId = R.drawable.ic_play_circle_filled_black_24dp
        tag = STOPPED
      }
    }

    playPauseButton.setColorFilter(accentColor)
    playPauseButton.setImageResource(resId)
    playPauseButton.tag = tag
  }

  /**
   * Starts the progress animation when called. If It was previously running then it restarts it.
   */
  private fun trackProgressAnimation(current: Int, total: Int) {
    progressHelper.stop()

    val tag = playPauseButton.tag
    if (STOPPED == tag || PAUSED == tag) {
      return
    }


    progressHelper.update(current, total)
  }

  private fun activateStoppedState() {
    progressBar.progress = 0
    trackProgressCurrent.text = getString(R.string.playback_progress, 0, 0)
  }

  override fun updateTrackInfo(info: TrackInfo) {
    artistLabel.text = info.artist
    titleLabel.text = info.title
    albumLabel.text = if (TextUtils.isEmpty(info.year)) info.album
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

  override fun updateProgress(position: UpdatePositionEvent) {
    val total = position.total
    val current = position.current

    var currentSeconds = current / 1000
    var totalSeconds = total / 1000

    val currentMinutes = currentSeconds / 60
    val totalMinutes = totalSeconds / 60

    currentSeconds %= 60
    totalSeconds %= 60
    val finalTotalSeconds = totalSeconds
    val finalCurrentSeconds = currentSeconds

    trackDuration.text = getString(R.string.playback_progress, totalMinutes, finalTotalSeconds)
    trackProgressCurrent.text = getString(R.string.playback_progress,
      currentMinutes,
      finalCurrentSeconds)

    progressBar.max = total
    progressBar.progress = current

    trackProgressAnimation(position.current, position.total)
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

  override fun progress(position: Int, duration: Int) {
    val currentProgress = progressBar.progress / 1000
    val currentMinutes = currentProgress / 60
    val currentSeconds = currentProgress % 60

    progressBar.progress = progressBar.progress + 1000
    trackProgressCurrent.text = getString(R.string.playback_progress,
      currentMinutes,
      currentSeconds)
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    if (isFinishing) {
      //when we leave the presenter flow,
      //we close its scope
      Toothpick.closeScope(PRESENTER_SCOPE)
    }
    outOfDateDialog?.dismiss()
    changeLogDialog?.dismiss()
    super.onDestroy()
  }

  companion object {
    private val PAUSED = "Paused"
    private val STOPPED = "Stopped"
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter
}

