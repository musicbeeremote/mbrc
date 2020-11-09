package com.kelsos.mbrc.ui.navigation.player

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import com.kelsos.mbrc.features.library.PlayingTrack
import org.koin.android.ext.android.inject

class PlayerFragment : Fragment(), VolumeDialogProvider {

  private val viewModel: PlayerViewModel by inject()

  private lateinit var dataBinding: FragmentPlayerBinding

  private var menu: Menu? = null
  private var shareActionProvider: ShareActionProvider? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dataBinding = FragmentPlayerBinding.inflate(inflater, container, false).apply {
      volumeProvider = this@PlayerFragment
      track = PlayingTrack()
    }
    return dataBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dataBinding.viewModel = viewModel

    viewModel.playerStatus.observe(this) {
      dataBinding.status = it
      menu?.findItem(R.id.player_screen__action_scrobbling)?.isChecked = it.scrobbling
    }

    viewModel.playingTrack.observe(this) {
      dataBinding.track = it
      shareActionProvider?.setShareIntent(getShareIntent())
    }

    viewModel.trackPosition.observe(this) {
      dataBinding.position = it
    }

    viewModel.trackRating.observe(this) {
      updateRating(it)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    dataBinding.unbind()
  }

  fun showChangeLog() {
    showChangeLogDialog()
    // todo manage dialogs somehow
  }

  fun notifyPluginOutOfDate() {
    showPluginOutOfDateDialog()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.player_screen__action_scrobbling -> {
        viewModel.toggleScrobbling()
        true
      }
      R.id.player_screen__action_rating -> {
        RatingDialogFragment.create(requireActivity() as AppCompatActivity).show()
        true
      }
      R.id.player_screen__action_favorite -> {
        viewModel.favorite()
        true
      }
      else -> false
    }
  }

  override fun showVolumeDialog() {
    VolumeDialog.create(requireActivity()).show()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.player_screen__actions, menu)
    this.menu = menu
    this.menu?.findItem(R.id.player_screen__action_share)?.let {
      shareActionProvider = MenuItemCompat.getActionProvider(it) as ShareActionProvider
    }
  }

  private fun getShareIntent(): Intent {
    return Intent(Intent.ACTION_SEND).apply {
      val track = dataBinding.track
      val payload = "Now Playing: ${track?.artist} - ${track?.title}"
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, payload)
    }
  }

  private fun updateRating(rating: TrackRating) {
    menu?.findItem(R.id.player_screen__action_favorite)?.let {
      val iconResId = if (rating.isFavorite()) {
        R.drawable.ic_favorite_black_24dp
      } else {
        R.drawable.ic_favorite_border_black_24dp
      }
      it.setIcon(iconResId)
    }
  }
}

interface VolumeDialogProvider {
  fun showVolumeDialog()
}