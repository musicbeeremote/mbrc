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
import androidx.fragment.app.Fragment
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.databinding.FragmentPlayerBinding
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerFragment : Fragment(), PlayerView {

  @Inject
  lateinit var presenter: PlayerPresenter

  private lateinit var dataBinding: FragmentPlayerBinding

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
      R.id.menu_lastfm_scrobble -> {
        presenter.toggleScrobbling()
        true
      }
      R.id.menu_rating_dialog -> {
        RatingDialogFragment.create(requireActivity() as AppCompatActivity).show()
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
    inflater?.inflate(R.menu.menu, menu)
    //todo fix rating.
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

  }

  override fun updateStatus(playerStatus: PlayerStatusModel) {
    dataBinding.status = playerStatus
  }

  override fun updateTrackInfo(playingTrack: PlayingTrack) {
    dataBinding.track = playingTrack
    //shareActionProvider?.setShareIntent(getShareIntent())
  }

  override fun updateProgress(position: PlayingPosition) {
    dataBinding.position = position
  }

  //todo move scrobble to some menu/dialog

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }
}