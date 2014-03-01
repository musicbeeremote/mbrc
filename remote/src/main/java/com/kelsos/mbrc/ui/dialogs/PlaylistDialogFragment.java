package com.kelsos.mbrc.ui.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.Playlist;

public class PlaylistDialogFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ListView.OnItemClickListener {
    private static final int URL_LOADER = 0x828e;
    private onPlaylistSelectedListener mListener;
    private ListView mList;
    private SimpleCursorAdapter mAdapter;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final FragmentActivity activity = getActivity();
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.playlist_add, null);
        mList = ((ListView)view.findViewById(R.id.playlist_list));
        mList.setOnItemClickListener(this);
        ((TextView)view.findViewById(R.id.dialog_title)).setText(getString(R.string.playlist_dialog_title));
        ((Button)view.findViewById(R.id.new_playlist_button)).setOnClickListener(onNewClick);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setView(view);
        mBuilder.setTitle(null);
        return mBuilder.create();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Playlist.getContentUri();
        return new CursorLoader(getActivity(), baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_playlist,
                cursor,
                new String[]{Playlist.PLAYLIST_NAME},
                new int[]{R.id.line_one},
                0);
        mList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            final Cursor cursor = (Cursor) mAdapter.getItem(position);
            final Playlist playlist = new Playlist(cursor);
            mListener.onPlaylistSelected(playlist.getHash());
        }
    }

    /**
     * Setter of the {@link com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment.onPlaylistSelectedListener}
     * @param mListener
     */
    public void setOnPlaylistSelectedListener(onPlaylistSelectedListener mListener) {
        this.mListener = mListener;
    }

    private Button.OnClickListener onNewClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onNewPlaylistSelected();
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
