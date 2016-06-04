package com.kelsos.mbrc.helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.ui.activities.profile.AlbumTracksActivity;
import com.kelsos.mbrc.ui.activities.profile.ArtistAlbumsActivity;
import com.kelsos.mbrc.ui.activities.profile.GenreArtistsActivity;
import com.squareup.otto.Bus;

@Singleton
public class PopupActionHandler {
  @Inject
  private Bus bus;

  @Inject
  private Context context;

  @Inject
  private BasicSettingsHelper settings;

  public void albumSelected(MenuItem menuItem, Album entry) {

    String query = entry.getAlbum();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_album_queue_next:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_album_queue_last:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_album_play:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_album_tracks:
        ua = new UserAction(Protocol.LibraryAlbumTracks, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void artistSelected(MenuItem menuItem, Artist entry) {
    String query = entry.getArtist();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_artist_queue_next:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_artist_queue_last:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_artist_play:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_artist_album:
        ua = new UserAction(Protocol.LibraryArtistAlbums, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void genreSelected(MenuItem menuItem, Genre entry) {
    String query = entry.getGenre();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_genre_queue_next:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_genre_queue_last:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_genre_play:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_genre_artists:
        ua = new UserAction(Protocol.LibraryGenreArtists, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void trackSelected(MenuItem menuItem, Track entry) {
    final String query = entry.getSrc();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_track_queue_next:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_track_queue_last:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_track_play:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.NOW, query));
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void albumSelected(Album album) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, album.getAlbum());
      UserAction data = new UserAction(Protocol.LibraryQueueAlbum, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
    } else {
      openProfile(album);
    }
  }

  public void artistSelected(Artist artist) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, artist.getArtist());
      UserAction data = new UserAction(Protocol.LibraryQueueArtist, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
    } else {
      openProfile(artist);
    }
  }

  public void genreSelected(Genre genre) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, genre.getGenre());
      UserAction action = new UserAction(Protocol.LibraryQueueGenre, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, action);
      bus.post(event);
    } else {
      openProfile(genre);
    }
  }

  public void trackSelected(Track track) {
    String defaultAction = settings.getDefaultAction();
    if (Const.SUB.equals(defaultAction)) {
      defaultAction = Queue.NOW;
    }
    //noinspection WrongConstant
    Queue queue = new Queue(defaultAction, track.getSrc());
    UserAction action = new UserAction(Protocol.LibraryQueueTrack, queue);
    MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, action);
    bus.post(event);
  }

  private void openProfile(Artist artist) {
    Intent intent = new Intent(context, ArtistAlbumsActivity.class);
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.getArtist());
    context.startActivity(intent);
  }

  private void openProfile(Album album) {
    Intent intent = new Intent(context, AlbumTracksActivity.class);
    intent.putExtra(AlbumTracksActivity.ALBUM_NAME, album.getAlbum());
    context.startActivity(intent);
  }

  private void openProfile(Genre genre) {
    Intent intent = new Intent(context, GenreArtistsActivity.class);
    intent.putExtra(GenreArtistsActivity.GENRE_NAME, genre.getGenre());
    context.startActivity(intent);
  }
}
