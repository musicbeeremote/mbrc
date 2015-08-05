package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import roboguice.fragment.RoboDialogFragment;

public class CreateNewPlaylistDialog extends RoboDialogFragment {

  private OnPlaylistNameSelectedListener mListener;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getActivity());
    mBuilder.title(R.string.playlist_dialog_new_title);
    mBuilder.customView(R.layout.playlist_create, false);
    mBuilder.positiveText(android.R.string.ok);
    mBuilder.negativeText(android.R.string.cancel);
    mBuilder.callback(new MaterialDialog.ButtonCallback() {
      @Override public void onPositive(MaterialDialog dialog) {
        EditText mPlaylistNameText =
            ((EditText) dialog.getCustomView().findViewById(R.id.playlist_name_text));
        final String name = mPlaylistNameText.getText().toString();
        if (mListener != null) {
          mListener.onPlaylistNameSelected(name);
        }
        dialog.dismiss();
      }

      @Override public void onNegative(MaterialDialog dialog) {
        dialog.dismiss();
      }
    });

    return mBuilder.build();
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
