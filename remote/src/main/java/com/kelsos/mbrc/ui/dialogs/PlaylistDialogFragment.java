package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistDialogAdapter;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.presenters.PlaylistDialogPresenter;
import com.kelsos.mbrc.ui.views.PlaylistDialogView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import roboguice.RoboGuice;
import timber.log.Timber;

public class PlaylistDialogFragment extends DialogFragment implements PlaylistDialogView {

  static final int ADD_MODE = 0;
  static final int CREATE_MODE = 1;

  @Bind(R.id.playlist_dialog_playlists) RecyclerView playlistsRecycler;
  @Bind(R.id.playlist_name_wrapper) RelativeLayout nameWrapper;
  @Bind(R.id.playlist_name_text) EditText name;
  @Bind(R.id.playlist_name_til) TextInputLayout textInputLayout;

  @Inject private PlaylistDialogAdapter adapter;
  @Inject private PlaylistDialogPresenter presenter;

  private PlaylistActionListener playlistActionListener;

  @Mode private int mode;
  private long selectionId;

  public static PlaylistDialogFragment newInstance(long id) {
    final PlaylistDialogFragment dialog = new PlaylistDialogFragment();
    dialog.selectionId = id;
    return dialog;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    mode = ADD_MODE;
    RoboGuice.getInjector(getContext()).injectMembers(this);
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.playlist_add, true);
    builder.autoDismiss(false);
    builder.title(R.string.playlist_dialog_title);

    builder.positiveText(R.string.playlist_add);
    builder.onPositive((dialog, which) -> {

      if (playlistActionListener == null) {
        return;
      }

      switch (mode) {
        case ADD_MODE:
          playlistActionListener.onExistingSelected(selectionId, adapter.getSelectedPlaylist().getId());
          break;
        case CREATE_MODE:
          final Editable text = name.getText();
          textInputLayout.setError(null);
          textInputLayout.setErrorEnabled(false);
          if (TextUtils.isEmpty(text)) {
            textInputLayout.setError(getString(R.string.field_cannot_be_empty));
            return;
          }
          playlistActionListener.onNewSelected(selectionId, text.toString());
          break;
        default:
          Timber.wtf("It was neither add nor create");
      }

      dialog.dismiss();
    });

    builder.onNegative((dialog, which) -> dialog.dismiss());
    builder.neutralText(R.string.playlist_dialog_create);
    builder.onNeutral((dialog, which) -> {
      if (mode == ADD_MODE) {
        // Previous mode was add, switching to create
        mode = CREATE_MODE;
        nameWrapper.setVisibility(View.VISIBLE);
        playlistsRecycler.setVisibility(View.GONE);
        dialog.setActionButton(DialogAction.NEUTRAL, R.string.playlist_dialog_select);
      } else {
        mode = ADD_MODE;
        nameWrapper.setVisibility(View.GONE);
        playlistsRecycler.setVisibility(View.VISIBLE);
        dialog.setActionButton(DialogAction.NEUTRAL, R.string.playlist_dialog_create);
      }
    });

    builder.negativeText(android.R.string.cancel);

    MaterialDialog dialog = builder.build();

    final View view = dialog.getCustomView();
    ButterKnife.bind(this, view);
    playlistsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    playlistsRecycler.setAdapter(adapter);
    presenter.bind(this);
    presenter.load();
    return dialog;
  }

  @Override public void update(List<Playlist> playlists) {
    adapter.update(playlists);
  }

  public void setPlaylistActionListener(PlaylistActionListener playlistActionListener) {
    this.playlistActionListener = playlistActionListener;
  }

  public interface PlaylistActionListener {
    void onExistingSelected(long selectionId, long playlistId);

    void onNewSelected(long selectionId, String name);
  }

  @IntDef({
      ADD_MODE, CREATE_MODE
  }) @Retention(RetentionPolicy.SOURCE) @interface Mode {

  }
}
