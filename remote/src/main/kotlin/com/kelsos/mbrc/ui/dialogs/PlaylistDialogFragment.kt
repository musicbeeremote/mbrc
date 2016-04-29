package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.Bind
import butterknife.ButterKnife
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.adapters.PlaylistDialogAdapter
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.presenters.PlaylistDialogPresenter
import com.kelsos.mbrc.ui.views.PlaylistDialogView
import roboguice.RoboGuice
import timber.log.Timber

class PlaylistDialogFragment : DialogFragment(), PlaylistDialogView {

  @Bind(R.id.playlist_dialog_playlists) internal lateinit var playlistsRecycler: RecyclerView
  @Bind(R.id.playlist_name_wrapper) internal lateinit var nameWrapper: RelativeLayout
  @Bind(R.id.playlist_name_text) internal lateinit var name: EditText
  @Bind(R.id.playlist_name_til) internal lateinit var textInputLayout: TextInputLayout

  @Inject private lateinit var adapter: PlaylistDialogAdapter
  @Inject private lateinit var presenter: PlaylistDialogPresenter

  private var playlistActionListener: PlaylistActionListener? = null

  @Mode private var mode: Long = 0
  private var selectionId: Long = 0

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    mode = ADD_MODE
    RoboGuice.getInjector(context).injectMembers(this)
    val builder = MaterialDialog.Builder(activity)
    builder.customView(R.layout.playlist_add, true)
    builder.autoDismiss(false)
    builder.title(R.string.playlist_dialog_title)

    builder.positiveText(R.string.playlist_add)
    builder.onPositive positive@{ dialog, which ->

      if (playlistActionListener == null) {
        return@positive
      }

      when (mode) {
        ADD_MODE -> playlistActionListener!!.onExistingSelected(selectionId, adapter.selectedPlaylist.id)
        CREATE_MODE -> {
          val text = name.text
          textInputLayout.error = null
          textInputLayout.isErrorEnabled = false
          if (TextUtils.isEmpty(text)) {
            textInputLayout.error = getString(R.string.field_cannot_be_empty)
            return@positive
          }
          playlistActionListener!!.onNewSelected(selectionId, text.toString())
        }
        else -> Timber.wtf("It was neither add nor create")
      }

      dialog.dismiss()
    }

    builder.onNegative { dialog, which -> dialog.dismiss() }
    builder.neutralText(R.string.playlist_dialog_create)
    builder.onNeutral { dialog, which ->
      if (mode == ADD_MODE) {
        // Previous mode was add, switching to create
        mode = CREATE_MODE
        nameWrapper.visibility = View.VISIBLE
        playlistsRecycler.visibility = View.GONE
        dialog.setActionButton(DialogAction.NEUTRAL, R.string.playlist_dialog_select)
      } else {
        mode = ADD_MODE
        nameWrapper.visibility = View.GONE
        playlistsRecycler.visibility = View.VISIBLE
        dialog.setActionButton(DialogAction.NEUTRAL, R.string.playlist_dialog_create)
      }
    }

    builder.negativeText(android.R.string.cancel)

    val dialog = builder.build()

    val view = dialog.customView
    ButterKnife.bind(this, view)
    playlistsRecycler.layoutManager = LinearLayoutManager(context)
    playlistsRecycler.adapter = adapter
    presenter.bind(this)
    presenter.load()
    return dialog
  }

  override fun update(playlists: List<Playlist>) {
    adapter.update(playlists)
  }

  fun setPlaylistActionListener(playlistActionListener: PlaylistActionListener) {
    this.playlistActionListener = playlistActionListener
  }

  interface PlaylistActionListener {
    fun onExistingSelected(selectionId: Long, playlistId: Long)

    fun onNewSelected(selectionId: Long, name: String)
  }

  @IntDef(ADD_MODE, CREATE_MODE)
  @Retention(AnnotationRetention.SOURCE)
  internal annotation class Mode

  companion object {

    const val ADD_MODE = 0L
    const val CREATE_MODE = 1L

    fun newInstance(id: Long): PlaylistDialogFragment {
      val dialog = PlaylistDialogFragment()
      dialog.selectionId = id
      return dialog
    }
  }
}
