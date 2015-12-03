package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;

public class PlaylistDialogFragment extends DialogFragment
    implements LoaderManager.LoaderCallbacks<Cursor>, ListView.OnItemClickListener {
  private static final int URL_LOADER = 0x828e;
  private OnPlaylistSelectedListener mListener;
  private SimpleCursorAdapter mAdapter;
  private MaterialDialog mDialog;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    getLoaderManager().initLoader(URL_LOADER, null, this);
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.playlist_add, true);

    builder.title(R.string.playlist_dialog_title);
    builder.positiveText(android.R.string.cancel);
    builder.callback(new MaterialDialog.ButtonCallback() {
      @Override public void onPositive(MaterialDialog dialog) {
        super.onPositive(dialog);
      }
    });

    mDialog = builder.build();

    final View dialogView = mDialog.getCustomView();

    dialogView.findViewById(R.id.new_playlist_button).setOnClickListener(v -> {
      if (mListener != null) {
        mListener.onNewPlaylistSelected();
        mDialog.dismiss();
      }
    });

    final ListView list = ((ListView) dialogView.findViewById(R.id.playlist));
    list.setOnItemClickListener(this);
    return mDialog;
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }

  /**
   * Setter of the
   * {@link com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment.OnPlaylistSelectedListener}
   *
   * @param mListener the listener
   */
  public void setOnPlaylistSelectedListener(OnPlaylistSelectedListener mListener) {
    this.mListener = mListener;
  }

  /**
   * Interface that must be implemented by the activity or fragment
   * that hosts the @{PlaylistDialogFragment}
   */
  public interface OnPlaylistSelectedListener {
    /**
     * This method is called when the user selects an existing playlist.
     * The hash representing the playlist gets passed as a parameter
     *
     * @param hash The hash representing the playlist selected.
     */
    void onPlaylistSelected(String hash);

    /**
     * This method is called when the user selects to create a new
     * playlist.
     */
    void onNewPlaylistSelected();
  }
}
