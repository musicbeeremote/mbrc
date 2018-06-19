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
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerFragment : Fragment(), PlayerView {

  @Inject
  lateinit var presenter: PlayerPresenter

  private lateinit var dataBinding: FragmentPlayerBinding

  private var menu: Menu? = null
  private var shareActionProvider: ShareActionProvider? = null

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
  ): View? {
    dataBinding = FragmentPlayerBinding.inflate(inflater, container, false)
    return dataBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dataBinding.presenter = presenter
    dataBinding.track = PlayingTrack()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
    presenter.load()
  }

  override fun showChangeLog() {
    showChangeLogDialog()
    //todo manage dialogs somehow
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
        RatingDialogFragment.create(requireActivity() as AppCompatActivity).show()
        true
      }
      R.id.player_screen__action_favorite -> {
        presenter.favorite()
        true
      }
      else -> false
    }
  }

  override fun showVolumeDialog() {
    VolumeDialog.create(requireActivity()).show()
  }

  override fun onStop() {
    presenter.detach()
    super.onStop()
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater?.inflate(R.menu.player_screen__actions, menu)
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

  override fun updateRating(rating: TrackRating) {
    menu?.findItem(R.id.player_screen__action_favorite)?.let {
      val iconResId = if (rating.isFavorite()) {
        R.drawable.ic_favorite_black_24dp
      } else {
        R.drawable.ic_favorite_border_black_24dp
      }
      it.setIcon(iconResId)
    }
  }

  override fun updateStatus(playerStatus: PlayerStatusModel) {
    dataBinding.status = playerStatus
    menu?.findItem(R.id.player_screen__action_scrobbling)?.isChecked = playerStatus.scrobbling
  }

  override fun updateTrackInfo(playingTrack: PlayingTrack) {
    dataBinding.track = playingTrack
    shareActionProvider?.setShareIntent(getShareIntent())
  }

  override fun updateProgress(position: PlayingPosition) {
    dataBinding.position = position
  }

  //todo move scrobble to some player_screen__actions/dialog

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}