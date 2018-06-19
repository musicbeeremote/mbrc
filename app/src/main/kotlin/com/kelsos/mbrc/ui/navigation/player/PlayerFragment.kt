package com.kelsos.mbrc.ui.navigation.player

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kelsos.mbrc.R
import com.kelsos.mbrc.changelog.ChangelogDialog
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import com.kelsos.mbrc.extensions.setIcon
import com.kelsos.mbrc.extensions.setStatusColor
import toothpick.Toothpick
import javax.inject.Inject

class PlayerFragment : Fragment(), PlayerView {

  @Inject
  lateinit var presenter: PlayerPresenter

  private var _binding: FragmentPlayerBinding? = null
  private val binding get() = _binding!!

  private var love: MenuItem? = null
  private var scrobble: MenuItem? = null

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
    binding.playerScreenShuffle.setOnClickListener { presenter.shuffle() }
    binding.playerScreenRepeat.setOnClickListener { presenter.repeat() }
    binding.playerScreenPlay.apply {
      setOnClickListener { presenter.play() }
      setOnLongClickListener { presenter.stop() }
    }
    binding.playerScreenPlayNext.setOnClickListener { presenter.next() }
    binding.playerScreenPlayPrevious.setOnClickListener { presenter.previous() }
    binding.playerScreenProgress.setOnSeekBarChangeListener { progress ->
      presenter.seek(progress)
    }
    binding.playerScreenVolume.setOnClickListener {
      findNavController().navigate(R.id.volume_dialog)
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
      R.id.player_screen__action_scrobbling -> {
        presenter.toggleScrobbling()
        true
      }
      R.id.player_screen__action_rating -> {
        findNavController().navigate(R.id.rating_dialog)
        true
      }
      R.id.player_screen__action_favorite -> {
        presenter.favorite()
        true
      }
      R.id.player_screen__action_share -> {
        share()
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
    inflater.inflate(R.menu.player_screen__actions, menu)
    love = menu.findItem(R.id.player_screen__action_favorite)
    scrobble = menu.findItem(R.id.player_screen__action_scrobbling)
  }

  private fun share() {
    val shareIntent = Intent.createChooser(sendIntent(), null)
    requireContext().startActivity(shareIntent)
  }

  private fun sendIntent(): Intent {
    return Intent(Intent.ACTION_SEND).apply {
      // TODO: hook up the playing track
      val track = PlayingTrack()
      val payload = "Now Playing: ${track.artist} - ${track.title}"
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, payload)
    }
  }

  override fun updateRating(rating: TrackRating) {
    if (rating.isFavorite()) {
      love?.setIcon(R.drawable.ic_favorite_black_24dp)
    } else {
      love?.setIcon(R.drawable.ic_favorite_border_black_24dp)
    }
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
    binding.playerScreenRepeat.setStatusColor(!playerStatus.isRepeatOff())
    binding.playerScreenShuffle.setStatusColor(!playerStatus.isShuffleOff())
    scrobble?.isChecked = playerStatus.scrobbling
  }

  override fun updateTrackInfo(playingTrack: PlayingTrack) {
    binding.playerScreenAlbumCover.loadImage(playingTrack.coverUrl)
    binding.playerScreenTrackArtist.text = playingTrack.artistInfo()
    binding.playerScreenTrackTitle.text = playingTrack.title
  }

  override fun updateProgress(position: PlayingPosition) {
    binding.playerScreenTotalProgress.text = position.progress()
    binding.playerScreenProgress.progress = position.current.toInt()
    binding.playerScreenProgress.max = position.total.toInt()
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}
