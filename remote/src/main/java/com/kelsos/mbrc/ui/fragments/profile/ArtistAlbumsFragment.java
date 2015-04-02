package com.kelsos.mbrc.ui.fragments.profile;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.dao.AlbumHelper;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.provided.RoboFragment;

public class ArtistAlbumsFragment extends RoboFragment
    implements LoaderManager.LoaderCallbacks<Cursor>, GridView.OnItemClickListener,
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final String ARTIST_ID = "artistId";
  private static final int URL_LOADER = 0x832d;
  private static final int GROUP_ID = 92;
  @Inject private AlbumCursorAdapter mAdapter;
  private GridView mGrid;
  private long artistId;

  public ArtistAlbumsFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param artistId Parameter 1.
   * @return A new instance of fragment ArtistAlbumsFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ArtistAlbumsFragment newInstance(long artistId) {
    ArtistAlbumsFragment fragment = new ArtistAlbumsFragment();
    Bundle args = new Bundle();
    args.putLong(ARTIST_ID, artistId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      artistId = getArguments().getLong(ARTIST_ID);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    getLoaderManager().initLoader(URL_LOADER, null, this);
    final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
    if (view != null) {
      mGrid = (GridView) view.findViewById(R.id.mbrc_grid_view);
      mGrid.setOnItemClickListener(this);
      mGrid.setAdapter(mAdapter);
    }
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    registerForContextMenu(mGrid);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    final String sortOrder = String.format("%s ASC", AlbumHelper.NAME);
    final String selection = String.format("%s = ?", AlbumHelper.ARTISTID);
    final String[] selectionArgs = {
        String.valueOf(artistId)
    };
    return new CursorLoader(getActivity(), AlbumHelper.CONTENT_URI, AlbumHelper.getProjection(),
        selection, selectionArgs, sortOrder);
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
