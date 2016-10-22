package com.kelsos.mbrc.ui.navigation

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.ShareActionProvider
import android.support.v7.widget.Toolbar
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
import javax.inject.Inject
import javax.inject.Singleton
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.presenters.MainViewPresenter
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import com.kelsos.mbrc.ui.views.MainView
import com.kelsos.mbrc.utilities.FontUtils
import roboguice.RoboGuice

@Singleton class MainActivity : BaseActivity(), MainView {

  // Inject elements of the view
  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.drawer_layout) lateinit var drawer: DrawerLayout
  @BindView(R.id.navigation_view) lateinit var navigationView: NavigationView
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

  @Inject private lateinit var presenter: MainViewPresenter

  private var mShareActionProvider: ShareActionProvider? = null

  private var menu: Menu? = null

  private val volumeBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (fromUser) {
        presenter.onVolumeChange(progress)
      }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }
  }

  private val progressBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (fromUser) {
        presenter.onPositionChange(progress)
      }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }

  @OnClick(R.id.main_button_play_pause) fun playButtonPressed() {
    presenter.onPlayPausePressed()
  }

  @OnClick(R.id.main_button_previous) fun onPreviousButtonPressed() {
    presenter.onPreviousPressed()
  }

  @OnClick(R.id.main_button_next) fun onNextButtonPressed() {
    presenter.onNextPressed()
  }

  @OnLongClick(R.id.main_button_play_pause) fun onStopPressed(): Boolean {
    presenter.onStopPressed()
    return true
  }

  @OnClick(R.id.main_mute_button) fun onMuteButtonPressed() {
    presenter.onMutePressed()
  }

  @OnClick(R.id.main_shuffle_button) fun onShuffleButtonClicked() {
    presenter.onShufflePressed()
  }

  @OnClick(R.id.main_repeat_button) fun onRepeatButtonPressed() {
    presenter.onRepeatPressed()
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    RoboGuice.getInjector(this).injectMembers(this)
    ButterKnife.bind(this)
    initialize(toolbar, drawer, navigationView)

    setCurrentSelection(R.id.drawer_menu_home)


    artistLabel.isSelected = true
    titleLabel.isSelected = true
    albumLabel.isSelected = true

    val robotoRegular = FontUtils.getRobotoRegular(baseContext)
    val robotoMedium = FontUtils.getRobotoMedium(baseContext)

    artistLabel.typeface = robotoRegular
    titleLabel.typeface = robotoMedium
    albumLabel.typeface = robotoMedium
    trackProgressCurrent.typeface = robotoMedium
    trackDuration.typeface = robotoMedium

    progressBar.setOnSeekBarChangeListener(progressBarChangeListener)
    volumeBar.setOnSeekBarChangeListener(volumeBarChangeListener)
  }

  public override fun onPause() {
    super.onPause()
    presenter.detachView()
  }

  public override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_lastfm_scrobble -> {
        presenter.onScrobbleToggle()
        return true
      }
      R.id.menu_rating_dialog -> {
        val ratingDialog = RatingDialogFragment()
        ratingDialog.show(supportFragmentManager, "RatingDialog")
        return true
      }
      R.id.menu_lastfm_love -> {
        presenter.onLfmLoveToggle()
        return true
      }
      else -> return false
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    this.menu = menu
    val shareItem = menu.findItem(R.id.actionbar_share)
    mShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
    mShareActionProvider!!.setShareIntent(shareIntent)
    return true
  }

  private val shareIntent: Intent
    get() {
      val shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      val payload = String.format("Now Playing: %s - %s", artistLabel.text, titleLabel.text)
      shareIntent.putExtra(Intent.EXTRA_TEXT, payload)
      return shareIntent
    }

  override fun updateCover(bitmap: Bitmap?) {
    if (bitmap != null) {
      albumCover.setImageBitmap(bitmap)
    } else {
      albumCover.setImageResource(R.drawable.ic_image_no_cover)
    }
  }

  override fun updateShuffle(@Shuffle.State state: String) {
    val resource = if (Shuffle.OFF != state) R.color.accent else R.color.button_dark
    val color = ContextCompat.getColor(baseContext, resource)

    shuffleButton.setColorFilter(color)

    val imageResource = if (Shuffle.AUTODJ == state) {
      R.drawable.ic_headset_black_24dp
    } else {
      R.drawable.ic_shuffle_black_24dp
    }

    shuffleButton.setImageResource(imageResource)
  }

  override fun updateRepeat(@Repeat.Mode mode: String) {
    val colorId = if (Repeat.ALL == mode) R.color.accent else R.color.button_dark
    val color = ContextCompat.getColor(baseContext, colorId)
    repeatButton.setColorFilter(color)
  }

  override fun updateScrobbling(enabled: Boolean) {
    val scrobbleMenuItem = menu!!.findItem(R.id.menu_lastfm_scrobble) ?: return
    scrobbleMenuItem.isChecked = enabled
  }

  override fun updateLoved(status: LfmStatus) {
    val favoriteMenuItem = menu!!.findItem(R.id.menu_lastfm_love) ?: return
    when (status) {
      LfmStatus.LOVED -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp)
      else -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp)
    }
  }

  override fun updateVolume(volume: Int) {
    volumeBar.progress = volume
  }

  override fun updatePlayState(@State playstate: String) {
    when (playstate) {
      PlayerState.PLAYING -> playPauseButton.setImageResource(R.drawable.ic_pause_circle_fill)
      PlayerState.PAUSED -> playPauseButton.setImageResource(R.drawable.ic_play_circle_fill)
      PlayerState.STOPPED -> playPauseButton.setImageResource(R.drawable.ic_play_circle_fill)
      else -> playPauseButton.setImageResource(R.drawable.ic_play_circle_fill)
    }
  }

  override fun updateMute(enabled: Boolean) {
    muteButton.setImageResource(if (enabled) R.drawable.ic_volume_off_black_24dp else R.drawable.ic_volume_up_black_24dp)
    muteButton.setColorFilter(ContextCompat.getColor(this, R.color.button_dark))
  }

  override fun updatePosition(position: TrackPosition) {
    trackProgressCurrent.text = String.format("%02d:%02d",
        position.currentMinutes,
        position.currentSeconds)

    trackDuration.text = String.format("%02d:%02d", position.totalMinutes, position.totalSeconds)

    progressBar.progress = position.current
    progressBar.max = position.total
  }

  override val currentProgress: Int
    get() = progressBar.progress

  override fun setStoppedState() {
    progressBar.progress = 0
    trackProgressCurrent.text = "00:00"
  }

  override fun updateTrackInfo(info: TrackInfo) {
    if (info.isEmpty()) {
      return
    }

    artistLabel.text = info.artist
    titleLabel.text = info.title
    albumLabel.text = if (TextUtils.isEmpty(info.year)) info.album
    else String.format("%s [%s]", info.album, info.year)

    if (mShareActionProvider != null) {
      mShareActionProvider!!.setShareIntent(shareIntent)
    }
  }
}
