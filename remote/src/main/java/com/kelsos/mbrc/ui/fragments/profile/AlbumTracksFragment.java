package com.kelsos.mbrc.ui.fragments.profile;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import roboguice.inject.InjectView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class AlbumTracksFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

    private static final String ALBUM_ID = "albumId";
    public static final int URL_LOADER = 0x928a;

    private long albumId;
    private SimpleCursorAdapter mAdapter;
    @InjectView(R.id.album_header) private View mHeader;
    @InjectView(R.id.header_album) private TextView mAlbum;
    @InjectView(R.id.header_artist) private TextView mArtist;
    @InjectView(R.id.header_tracks) private TextView mTracks;
    @InjectView(R.id.header_artwork) private ImageView mArtwork;

    public static AlbumTracksFragment newInstance(long albumId) {
        AlbumTracksFragment fragment = new AlbumTracksFragment();
        Bundle args = new Bundle();
        args.putLong(ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumTracksFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getLong(ALBUM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_tracks, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }
    @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Track.CONTENT_ALBUM_URI, Uri.encode(String.valueOf(albumId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Track._ID, Track.TITLE, Track.TRACK_NO}, null, null, null);
    }



    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_single,
                cursor,
                new String[] {Track.TITLE},
                new int[] {R.id.line_one},
                0);

        this.getListView().addHeaderView(mHeader);
        this.setListAdapter(mAdapter);

        mTracks.setText(getString(R.string.track_count, mAdapter.getCount()));
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mHeader == null) {
            return;
        }

        final View top = view.getChildAt(firstVisibleItem);
        if (top == null) {
            return;
        }

        if (firstVisibleItem != 0) {
            //mHeader.
        }
    }
}
