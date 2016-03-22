package com.kelsos.mbrc.ui.fragments.profile;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumProfileAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.presenters.AlbumTracksPresenter;
import com.kelsos.mbrc.ui.views.AlbumTrackView;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;
import roboguice.RoboGuice;

public class AlbumTracksActivity extends AppCompatActivity implements AlbumTrackView,
    AlbumProfileAdapter.MenuItemSelectedListener {

  public static final String ALBUM_ID = "albumId";
  @Bind(R.id.imageView_list) ImageView imageViewList;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
  @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
  @Bind(R.id.list_tracks) RecyclerView listTracks;
  @Bind(R.id.album_title) TextView albumTitle;
  @Bind(R.id.album_year) TextView albumYear;
  @Bind(R.id.album_tracks) TextView albumTracks;

  @Inject private AlbumProfileAdapter adapter;
  @Inject private AlbumTracksPresenter presenter;
  private long albumId;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public AlbumTracksActivity() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_album_tracks);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);
    presenter.bind(this);
    final Bundle extras = getIntent().getExtras();
    albumId = 0;

    if (extras != null) {
      albumId = extras.getLong(ALBUM_ID, 0);
    }

    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
    }

    collapsingToolbar.setTitleEnabled(true);
    listTracks.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    listTracks.setAdapter(adapter);

    if (albumId == 0) {
      finish();
    }
    adapter.setListener(this);

    presenter.load(albumId);
  }

  @Override public void updateAlbum(Album album) {
    final String cover = album.getCover();

    albumTitle.setText(album.getName());
    albumYear.setText(album.getYear());
    collapsingToolbar.setTitle(album.getArtist());

    if (!TextUtils.isEmpty(cover)) {

      final File image = new File(new File(getFilesDir(), "covers"), cover);

      Picasso.with(getBaseContext())
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .into(imageViewList);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void updateTracks(List<Track> tracks) {
    adapter.updateData(tracks);
    albumTracks.setText(getString(R.string.number_of_tracks, tracks.size()));
  }

  @Override public void showPlaySuccess() {
    showSnackbar(R.string.album_play_success);
  }

  private void showSnackbar(@StringRes int resId) {
    Snackbar.make(appBarLayout, resId, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void showPlayFailed() {
    showSnackbar(R.string.album_play_failure);
  }

  @Override public void showTrackSuccess() {
    showSnackbar(R.string.track_added_successfully);
  }

  @Override public void showTrackFailed() {
    showSnackbar(R.string.track_add_failed);
  }

  @OnClick(R.id.play_album) public void onPlayClicked() {
    presenter.play(albumId);
  }


  @Override public void onMenuItemSelected(MenuItem menuItem, Track entry) {
    switch (menuItem.getItemId()) {
      case R.id.popup_track_play:
        presenter.queue(entry, Queue.NOW);
        break;
      case R.id.popup_track_queue_last:
        presenter.queue(entry, Queue.LAST);
        break;
      case R.id.popup_track_queue_next:
        presenter.queue(entry, Queue.NEXT);
        break;
      case R.id.popup_track_playlist:
        break;
      default:
        break;
    }
  }

  @Override public void onItemClicked(Track track) {
    presenter.queue(track, Queue.NOW);
  }
}
