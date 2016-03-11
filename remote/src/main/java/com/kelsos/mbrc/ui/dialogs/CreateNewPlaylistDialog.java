package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;

public class CreateNewPlaylistDialog extends DialogFragment {

  private OnPlaylistNameSelectedListener mListener;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.title(R.string.playlist_dialog_new_title);
    builder.customView(R.layout.playlist_create, false);
    builder.positiveText(android.R.string.ok);
    builder.negativeText(android.R.string.cancel);
    builder.onPositive((dialog, which) -> {
      EditText mPlaylistNameText = ((EditText) dialog.getCustomView().findViewById(R.id.playlist_name_text));
      final String name = mPlaylistNameText.getText().toString();
      if (mListener != null) {
        mListener.onPlaylistNameSelected(name);
      }
      dialog.dismiss();
    });
    builder.onNegative((dialog, which) -> dialog.dismiss());

    return builder.build();
  }

  public void setOnPlaylistNameSelectedListener(OnPlaylistNameSelectedListener mListener) {
    this.mListener = mListener;
  }

  /**
   * Interface that must be implemented by the fragment or activity
   * hosting the dialog
   */
  public interface OnPlaylistNameSelectedListener {
    /**
     * Called when the user presses the ok button of the dialog.
     *
     * @param name The name of the playlist
     */
    void onPlaylistNameSelected(String name);
  }
}
