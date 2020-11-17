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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kelsos.mbrc.R
import com.kelsos.mbrc.changelog.ChangelogDialog
import com.kelsos.mbrc.common.ui.extensions.setIcon
import com.kelsos.mbrc.common.ui.extensions.setStatusColor
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

  private val viewModel: PlayerViewModel by viewModel()

  private var _binding: FragmentPlayerBinding? = null
  private val binding get() = _binding!!

  private var love: MenuItem? = null
  private var scrobble: MenuItem? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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
    binding.playerScreenShuffle.setOnClickListener { viewModel.shuffle() }
    binding.playerScreenRepeat.setOnClickListener { viewModel.repeat() }
    binding.playerScreenPlay.apply {
      setOnClickListener { viewModel.play() }
      setOnLongClickListener { viewModel.stop() }
    }
    binding.playerScreenPlayNext.setOnClickListener { viewModel.next() }
    binding.playerScreenPlayPrevious.setOnClickListener { viewModel.previous() }
    binding.playerScreenProgress.setOnSeekBarChangeListener { progress ->
      viewModel.seek(progress)
    }
    binding.playerScreenVolume.setOnClickListener {
      findNavController().navigate(R.id.volume_dialog)
    }
    viewModel.playerStatus.observe(viewLifecycleOwner) { status ->
      updateStatus(status)
    }

    viewModel.trackPosition.observe(viewLifecycleOwner) { position ->
      binding.playerScreenTotalProgress.text = position.progress()
      binding.playerScreenProgress.progress = position.current.toInt()
      binding.playerScreenProgress.max = position.total.toInt()
    }

    viewModel.trackRating.observe(viewLifecycleOwner) {
      if (it.isFavorite()) {
        love?.setIcon(R.drawable.ic_favorite_black_24dp)
      } else {
        love?.setIcon(R.drawable.ic_favorite_border_black_24dp)
      }
    }

    viewModel.playingTrack.observe(viewLifecycleOwner) { track ->
      binding.playerScreenAlbumCover.loadImage(track.coverUrl)
      binding.playerScreenTrackArtist.text = track.artistInfo()
      binding.playerScreenTrackTitle.text = track.title
    }

    lifecycleScope.launch {
      viewModel.emitter.collect { message ->
        when (message) {
          is PlayerUiMessage.ShowChangelog -> ChangelogDialog.show(
            requireActivity(),
            R.raw.changelog
          )
          is PlayerUiMessage.ShowPluginUpdate -> Unit
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.player_screen__action_scrobbling -> {
        viewModel.toggleScrobbling()
        true
      }
      R.id.player_screen__action_rating -> {
        findNavController().navigate(R.id.rating_dialog)
        true
      }
      R.id.player_screen__action_favorite -> {
        viewModel.favorite()
        true
      }
      R.id.player_screen__action_share -> {
        share()
        true
      }
      else -> false
    }
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
      val track = viewModel.playingTrack.getValue()
      val payload = "Now Playing: ${track?.artist} - ${track?.title}"
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, payload)
    }
  }

  private fun updateStatus(playerStatus: PlayerStatusModel) {
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
}
