package com.kelsos.mbrc.ui.navigation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kelsos.mbrc.R
import com.kelsos.mbrc.changelog.ChangelogDialog
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import toothpick.Toothpick
import javax.inject.Inject

class PlayerFragment : Fragment(), PlayerView {

  @Inject
  lateinit var presenter: PlayerPresenter

  private var _binding: FragmentPlayerBinding? = null
  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    val scope = Toothpick.openScopes(requireActivity().application, requireActivity(), this)
    scope.installModules(mainModule)
    super.onCreate(savedInstanceState)
    Toothpick.inject(this, scope)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentPlayerBinding.inflate(
      inflater,
      container,
      false
    )
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.playerScreenFavoriteButton.setOnClickListener { presenter.lfmLove() }
    binding.playerScreenShuffle.setOnClickListener { presenter.shuffle() }
    binding.playerScreenRepeat.setOnClickListener { presenter.repeat() }
    binding.playerScreenMute.setOnClickListener { presenter.mute() }
    binding.playerScreenPlay.apply {
      setOnClickListener { presenter.play() }
      setOnLongClickListener { presenter.stop() }
    }
    binding.playerScreenPlayNext.setOnClickListener { presenter.next() }
    binding.playerScreenPlayPrevious.setOnClickListener { presenter.previous() }
    binding.playerScreenVolume.setOnSeekBarChangeListener { volume ->
      presenter.changeVolume(volume)
    }
    binding.playerScreenProgress.setOnSeekBarChangeListener {
      progress ->
      presenter.seek(progress)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
    presenter.load()
  }

  override fun showChangeLog() {
    ChangelogDialog.show(requireContext(), R.raw.changelog)
  }

  override fun notifyPluginOutOfDate() {
    showPluginOutOfDateDialog()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_lastfm_scrobble -> {
        presenter.toggleScrobbling()
        true
      }
      R.id.menu_rating_dialog -> {
        findNavController().navigate(R.id.rating_dialog)
        true
      }
      else -> false
    }
  }

  override fun onStop() {
    presenter.detach()
    super.onStop()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu, menu)
    // todo fix rating.
  }

  override fun updateRating(rating: TrackRating) {
    binding.playerScreenFavoriteButton.setIcon(
      enabled = rating.isFavorite(),
      onRes = R.drawable.ic_favorite_black_24dp,
      offRes = R.drawable.ic_favorite_border_black_24dp
    )
  }

  override fun updateStatus(playerStatus: PlayerStatusModel) {
    binding.playerScreenPlay.setIcon(
      enabled = playerStatus.isPlaying(),
      onRes = R.drawable.ic_pause_circle_filled_black_24dp,
      offRes = R.drawable.ic_play_circle_filled_black_24dp
    )
    binding.playerScreenRepeat.setIcon(
      enabled = playerStatus.isRepeatOne(),
      onRes = R.drawable.ic_repeat_one_black_24dp,
      offRes = R.drawable.ic_repeat_black_24dp
    )
    binding.playerScreenShuffle.setIcon(
      enabled = playerStatus.isShuffleAutoDj(),
      onRes = R.drawable.ic_headset_black_24dp,
      offRes = R.drawable.ic_shuffle_black_24dp
    )
    binding.playerScreenMute.setIcon(
      enabled = playerStatus.mute,
      onRes = R.drawable.ic_volume_up_black_24dp,
      offRes = R.drawable.ic_volume_off_black_24dp
    )
    binding.playerScreenVolume.progress = if (playerStatus.mute) 0 else playerStatus.volume
    binding.playerScreenRepeat.setStatusColor(!playerStatus.isRepeatOff())
    binding.playerScreenMute.setStatusColor(playerStatus.mute)
    binding.playerScreenShuffle.setStatusColor(!playerStatus.isShuffleOff())
  }

  override fun updateTrackInfo(playingTrack: PlayingTrack) {
    binding.playerScreenAlbumCover.loadImage(playingTrack.coverUrl)
    binding.playerScreenTrackArtist.text = playingTrack.artist
    binding.playerScreenTrackAlbum.text = playingTrack.albumInfo()
    binding.playerScreenTrackTitle.text = playingTrack.title
  }

  override fun updateProgress(position: PlayingPosition) {
    binding.playerScreenCurrentProgress.text = position.currentMinutes()
    binding.playerScreenTotalProgress.text = position.totalMinutes()
    binding.playerScreenProgress.progress = position.current.toInt()
    binding.playerScreenProgress.max = position.total.toInt()
  }

  // todo move scrobble to some menu/dialog

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  private fun ImageButton.setStatusColor(enabled: Boolean) {
    val colorResId = if (enabled) R.color.accent else R.color.button_dark
    setColorFilter(context.getColor(colorResId))
  }
  private fun ImageButton.setIcon(
    enabled: Boolean,
    @DrawableRes onRes: Int,
    @DrawableRes offRes: Int
  ) {
    val iconResId = if (enabled) onRes else offRes
    setImageResource(iconResId)
  }
}
