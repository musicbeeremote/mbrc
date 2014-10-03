package com.kelsos.mbrc.ui.dialogs;


import android.app.*;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.kelsos.mbrc.R;

public class PlaylistDialogFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ListView.OnItemClickListener {
    private static final int URL_LOADER = 0x828e;
    private onPlaylistSelectedListener mListener;
    private ListView mList;
    private SimpleCursorAdapter mAdapter;
    private AlertDialog mDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final Activity activity = getActivity();
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.playlist_add, null);
        mList = ((ListView)view.findViewById(R.id.playlist_list));
        mList.setOnItemClickListener(this);
        ((TextView)view.findViewById(R.id.dialog_title)).setText(getString(R.string.playlist_dialog_title));
        view.findViewById(R.id.new_playlist_button).setOnClickListener(onNewClick);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setView(view);
        mBuilder.setTitle(null);
        mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog = mBuilder.create();
        return mDialog;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Setter of the {@link com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment.onPlaylistSelectedListener}
     * @param mListener the listener
     */
    public void setOnPlaylistSelectedListener(onPlaylistSelectedListener mListener) {
        this.mListener = mListener;
    }

    private Button.OnClickListener onNewClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onNewPlaylistSelected();
                mDialog.dismiss();
            }
        }
    };

    /**
     * Interface that must be implemented by the activity or fragment
     * that hosts the @{PlaylistDialogFragment}
     */
    public interface onPlaylistSelectedListener {
        /**
         * This method is called when the user selects an existing playlist.
         * The hash representing the playlist gets passed as a parameter
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
