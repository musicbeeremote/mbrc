package com.kelsos.mbrc.ui.fragments.profile;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistCursorAdapter;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import roboguice.fragment.provided.RoboListFragment;

public class GenreArtistsFragment extends RoboListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final String GENRE_ID = "genreId";
  private static final int GROUP_ID = 2983;
  private static final int URL_LOADER = 0x15;
  @Inject private ArtistCursorAdapter mAdapter;
  private long genreId;

  public GenreArtistsFragment() {
    // Required empty public constructor
  }

  public static GenreArtistsFragment newInstance(long genreId) {
    GenreArtistsFragment fragment = new GenreArtistsFragment();
    Bundle args = new Bundle();
    args.putLong(GENRE_ID, genreId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      genreId = getArguments().getLong(GENRE_ID);
    }
    setListAdapter(mAdapter);
    getLoaderManager().initLoader(URL_LOADER, null, this);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_genre_artists, container, false);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    //final String sortOrder = String.format("%s ASC", ArtistHelper.NAME);
    ////todo add genre to artist?
    //final String selection = String.format("%s = ?", ArtistHelper.ARTIST_ID);
    //final String[] selectionArgs = {
    //    String.valueOf(genreId)
    //};
    //return new CursorLoader(getActivity(), ArtistHelper.CONTENT_URI, ArtistHelper.getProjection(),
    //    selection, selectionArgs, sortOrder);
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
