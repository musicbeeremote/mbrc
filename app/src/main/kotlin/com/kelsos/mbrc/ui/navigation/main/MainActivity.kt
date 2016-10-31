package com.kelsos.mbrc.ui.navigation.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.events.ui.OnMainFragmentOptionsInflated
import com.kelsos.mbrc.events.ui.ShuffleChange
import com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState
import com.kelsos.mbrc.events.ui.UpdatePosition
import com.kelsos.mbrc.extensions.coverFile
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.helper.ProgressSeekerHelper
import com.kelsos.mbrc.helper.ProgressSeekerHelper.ProgressUpdate
import com.kelsos.mbrc.helper.SeekBarThrottler
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import com.squareup.picasso.Picasso
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainActivity : BaseActivity(), MainView, ProgressUpdate {

  private val PRESENTER_SCOPE: Class<*> = Presenter::class.java
  // Injects
  @Inject lateinit var presenter: MainViewPresenter
  @Inject lateinit var progressHelper: ProgressSeekerHelper
  // Inject elements of the view
  @BindView(R.id.main_artist_label) lateinit var artistLabel: TextView
  @BindView(R.id.main_title_label) lateinit var titleLabel: TextView
  @BindView(R.id.main_label_album) lateinit var albumLabel: TextView
  @BindView(R.id.main_track_progress_current) lateinit var trackProgressCurrent: TextView
  @BindView(R.id.main_track_duration_total) lateinit var trackDuration: TextView
  @BindView(R.id.main_button_play_pause) lateinit var playPauseButton: ImageButton
  @BindView(R.id.main_volume_seeker) lateinit var volumeBar: SeekBar
  @BindView(R.id.main_track_progress_seeker) lateinit var progressBar: SeekBar
  @BindView(R.id.main_mute_button) lateinit var muteButton: ImageButton
  @BindView(R.id.main_shuffle_button) lateinit var shuffleButton: ImageButton
  @BindView(R.id.main_repeat_button) lateinit var repeatButton: ImageButton
  @BindView(R.id.main_album_cover_image_view) lateinit var albumCover: ImageView
  private var mShareActionProvider: ShareActionProvider? = null

  private var menu: Menu? = null

  private var volumeChangeListener: SeekBarThrottler? = null
  private var positionChangeListener: SeekBarThrottler? = null

  @OnClick(R.id.main_button_play_pause)
  internal fun playButtonPressed() {
    presenter.play()
  }

  @OnClick(R.id.main_button_previous)
  internal fun onPreviousButtonPressed() {
    presenter.previous()
  }

  @OnClick(R.id.main_button_next)
  internal fun onNextButtonPressed() {
    presenter.next()
  }

  @OnLongClick(R.id.main_button_play_pause)
  internal fun onPlayerStopPressed(): Boolean {
    return presenter.stop()
  }

  @OnClick(R.id.main_mute_button)
  internal fun onMuteButtonPressed() {
    presenter.mute()
  }

  @OnClick(R.id.main_shuffle_button)
  internal fun onShuffleButtonClicked() {
    presenter.shuffle()
  }

  @OnClick(R.id.main_repeat_button)
  internal fun onRepeatButtonPressed() {
    presenter.repeat()
  }

  private lateinit var scope: Scope

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)
    super.setup()
    scope = Toothpick.openScopes(application, PRESENTER_SCOPE, this)
    scope.installModules(SmoothieActivityModule(this), MainModule())
    Toothpick.inject(this, scope)
    volumeChangeListener = SeekBarThrottler { presenter.changeVolume(it) }
    positionChangeListener = SeekBarThrottler { presenter.seek(it) }
    volumeBar.setOnSeekBarChangeListener(volumeChangeListener)
    progressBar.setOnSeekBarChangeListener(positionChangeListener)
    progressHelper.setProgressListener(this)
    presenter.attach(this)
    presenter.load()
  }

  public override fun onStart() {
    super.onStart()
    presenter.attach(this)
    artistLabel.isSelected = true
    titleLabel.isSelected = true
    albumLabel.isSelected = true
  }

  public override fun onResume() {
    super.onResume()
    presenter.load()
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

  public override fun onStop() {
    super.onStop()
    presenter.detach()
    bus.unregister(this)
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
      val payload = String.format("Now Playing: %s - %s", artistLabel.text, titleLabel.text)
      shareIntent.putExtra(Intent.EXTRA_TEXT, payload)
      return shareIntent
    }

  override fun updateCover() {
    val file = this.coverFile()

    if (!file.exists()) {
      Picasso.with(this).invalidate(file)
      albumCover.tag = 0L
      albumCover.setImageResource(R.drawable.ic_image_no_cover)
      return
    }

    val lastModified = if (albumCover.tag != null) albumCover.tag as Long else 0L

    if (lastModified == 0L || lastModified < file.lastModified()) {
      albumCover.tag = file.lastModified()
      Picasso.with(this).invalidate(file)
    } else {
      return
    }

    val dimens = getDimens()
    Picasso.with(this)
        .load(file)
        .placeholder(R.drawable.ic_image_no_cover)
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
    if (Repeat.ALL.equals(mode, ignoreCase = true)) {
      // Do nothing already set above
    } else if (Repeat.ONE.equals(mode, ignoreCase = true)) {
      resId = R.drawable.ic_repeat_one_black_24dp
    } else {
      colorId = R.color.button_dark
    }

    val color = ContextCompat.getColor(this, colorId)
    repeatButton.setImageResource(resId)
    repeatButton.setColorFilter(color)
  }

  override fun updateVolume(volume: Int, mute: Boolean) {

    if (!volumeChangeListener!!.fromUser) {
      volumeBar.progress = volume
    }

    val color = ContextCompat.getColor(this, R.color.button_dark)
    muteButton.setColorFilter(color)
    muteButton.setImageResource(if (mute) R.drawable.ic_volume_off_black_24dp else R.drawable.ic_volume_up_black_24dp)
  }

  override fun updatePlayState(@State state: String) {
    val accentColor = ContextCompat.getColor(this, R.color.accent)
    @DrawableRes val resId: Int
    val tag: String

    if (PlayerState.PLAYING == state) {
      resId = R.drawable.ic_pause_circle_filled_black_24dp
      tag = "Playing"
      /* Start the animation if the track is playing*/
      presenter.requestNowPlayingPosition()
      trackProgressAnimation(progressBar.progress, progressBar.max)
    } else if (PlayerState.PAUSED == state) {
      resId = R.drawable.ic_play_circle_filled_black_24dp
      tag = PAUSED
      /* Stop the animation if the track is paused*/
      progressHelper.stop()
    } else if (PlayerState.STOPPED == state) {
      resId = R.drawable.ic_play_circle_filled_black_24dp
      tag = STOPPED
      /* Stop the animation if the track is paused*/
      progressHelper.stop()
      activateStoppedState()
    } else {
      resId = R.drawable.ic_play_circle_filled_black_24dp
      tag = STOPPED
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

  override fun updateProgress(position: UpdatePosition) {
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

  override fun updateLfmStatus(status: LfmStatus) {
    if (menu == null) {
      return
    }
    val favoriteMenuItem = menu!!.findItem(R.id.menu_lastfm_love) ?: return

    when (status) {
      LfmStatus.LOVED -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp)
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

