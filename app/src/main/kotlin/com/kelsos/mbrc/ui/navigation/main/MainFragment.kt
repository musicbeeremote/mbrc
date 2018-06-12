package com.kelsos.mbrc.ui.navigation.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R
import com.kelsos.mbrc.changelog.ChangelogDialog
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.Repeat.Mode
import com.kelsos.mbrc.content.activestatus.TrackPositionData
import com.kelsos.mbrc.content.activestatus.TrackRatingModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.databinding.FragmentMainBinding
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.events.ShuffleMode.Shuffle
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import com.kelsos.mbrc.ui.navigation.main.ProgressSeekerHelper.ProgressUpdate
import com.squareup.picasso.Picasso
import toothpick.Scope
import toothpick.Toothpick
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainFragment : Fragment(), MainView, ProgressUpdate {

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
  private var _binding: FragmentMainBinding? = null
  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    scope = Toothpick.openScopes(requireActivity().application, PRESENTER_SCOPE, this)
    scope.installModules(MainModule())
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.mainMuteButton.setOnClickListener { presenter.mute() }
    binding.mainShuffleButton.setOnClickListener { presenter.shuffle() }
    binding.mainRepeatButton.setOnClickListener { presenter.repeat() }

    binding.mainButtonPlayPause.setOnClickListener { presenter.play() }
    binding.mainButtonPlayPause.setOnLongClickListener { presenter.stop() }
    binding.mainButtonPrevious.setOnClickListener { presenter.previous() }
    binding.mainButtonNext.setOnClickListener { presenter.next() }

    progressHelper.setProgressListener(this)
    volumeChangeListener = SeekBarThrottler { presenter.changeVolume(it) }
    positionChangeListener = SeekBarThrottler { presenter.seek(it) }
    binding.mainVolumeSeeker.setOnSeekBarChangeListener(volumeChangeListener)
    binding.mainTrackProgressSeeker.setOnSeekBarChangeListener(positionChangeListener)
    binding.mainArtistLabel.isSelected = true
    binding.mainTitleLabel.isSelected = true
    binding.mainAlbumLabel.isSelected = true

    presenter.attach(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun showChangeLog() {
    changeLogDialog = ChangelogDialog.show(requireContext(), R.raw.changelog)
  }

  override fun notifyPluginOutOfDate() {
    val snackBar = Snackbar.make(
      requireView(),
      R.string.main__dialog_plugin_outdated_message,
      Snackbar.LENGTH_INDEFINITE
    )
    snackBar.setAction(android.R.string.ok) { snackBar.dismiss() }
    snackBar.show()
  }

  override fun showPluginUpdateRequired(minimumRequired: String) {
    // TODO re-introduce update required screen
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
    binding.mainAlbumLabel.isSelected = true
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
        RatingDialogFragment.create(requireActivity() as AppCompatActivity).show()
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
    progressHelper.setProgressListener(null)
    volumeChangeListener?.terminate()
    volumeChangeListener = null
    positionChangeListener?.terminate()
    positionChangeListener = null
    binding.mainVolumeSeeker.setOnSeekBarChangeListener(null)
    binding.mainTrackProgressSeeker.setOnSeekBarChangeListener(null)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu, menu)
    this.menu = menu
  }

  private val shareIntent: Intent
    get() {
      val shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      val payload = String.format(
        "Now Playing: %s - %s",
        binding.mainAlbumLabel.text,
        binding.mainTitleLabel.text
      )
      shareIntent.putExtra(Intent.EXTRA_TEXT, payload)
      return shareIntent
    }

  override fun updateRating(rating: TrackRatingModel) {
    menu?.run {
      val favoriteMenuItem = findItem(R.id.menu_lastfm_love) ?: return

      when (rating.lfmRating) {
        LfmRating.LOVED -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp)
        else -> favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp)
      }
    }
  }

  override fun updateStatus(playerStatus: PlayerStatusModel) {
    with(playerStatus) {
      updateShuffleState(shuffle)
      updateRepeat(repeat)
      updateVolume(volume, mute)
      updatePlayState(playState)
      updateScrobbleStatus(scrobbling)
    }
  }

  private fun updateCover(path: String) {
    val file = File(path)

    val mainAlbumCoverImageView = binding.mainAlbumCoverImageView
    if (!file.exists()) {
      mainAlbumCoverImageView.setImageResource(R.drawable.ic_image_no_cover)
      return
    }

    if (mainAlbumCoverImageView.tag == path) {
      return
    }

    mainAlbumCoverImageView.tag = path

    val dimens = requireActivity().getDimens()

    Picasso.get()
      .load(file)
      .noFade()
      .error(R.drawable.ic_image_no_cover)
      .config(Bitmap.Config.RGB_565)
      .resize(dimens, dimens)
      .centerCrop()
      .into(mainAlbumCoverImageView)
  }

  private fun updateShuffleState(@Shuffle shuffleState: String) {
    val shuffle = ShuffleMode.OFF != shuffleState
    val autoDj = ShuffleMode.AUTODJ == shuffleState

    val color = requireContext().getColor(if (shuffle) R.color.accent else R.color.button_dark)
    val resId = if (autoDj) R.drawable.ic_headset_black_24dp else R.drawable.ic_shuffle_black_24dp
    binding.mainShuffleButton.setColorFilter(color)
    binding.mainShuffleButton.setImageResource(resId)
  }

  private fun updateRepeat(@Mode mode: String) {
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
    repeatButton.setColorFilter(requireContext().getColor(colorId))
  }

  private fun updateVolume(volume: Int, mute: Boolean) {
    binding.mainVolumeSeeker.progress = volume
    val resId = if (mute) {
      R.drawable.ic_volume_off_black_24dp
    } else {
      R.drawable.ic_volume_up_black_24dp
    }
    with(binding.mainMuteButton) {
      setColorFilter(requireContext().getColor(R.color.button_dark))
      setImageResource(resId)
    }
  }

  private fun updatePlayState(@State state: String) {
    val accentColor = requireContext().getColor(R.color.accent)
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

  override fun updateTrackInfo(info: PlayingTrackModel) {
    binding.mainArtistLabel.text = info.artist
    binding.mainTitleLabel.text = info.title
    binding.mainAlbumLabel.text = if (TextUtils.isEmpty(info.year)) info.album
    else String.format("%s [%s]", info.album, info.year)

    if (mShareActionProvider != null) {
      mShareActionProvider!!.setShareIntent(shareIntent)
    }

    updateCover(info.coverUrl)
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

  override fun updateProgress(duration: TrackPositionData) {
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

  private fun updateScrobbleStatus(active: Boolean) {
    if (menu == null) {
      return
    }
    val scrobbleMenuItem = menu!!.findItem(R.id.menu_lastfm_scrobble) ?: return

    scrobbleMenuItem.isChecked = active
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

  override fun onDestroy() {
    Toothpick.closeScope(this)
    outOfDateDialog?.dismiss()
    changeLogDialog?.dismiss()
    super.onDestroy()
  }

  @javax.inject.Scope
  @Target(AnnotationTarget.TYPE)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Presenter

  companion object {
    private const val PAUSED = "Paused"
    private const val STOPPED = "Stopped"
    private const val PLAYING = "Playing"
    private val PRESENTER_SCOPE: Class<*> = Presenter::class.java

    fun tag(@State state: String): String = when (state) {
      PlayerState.PLAYING -> PLAYING
      PlayerState.PAUSED -> PAUSED
      else -> STOPPED
    }
  }
}
