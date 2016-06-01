package com.kelsos.mbrc.helper;

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
import com.squareup.otto.Bus;

@Singleton
public class PopupActionHandler {
  @Inject
  private Bus bus;

  @Inject
  private BasicSettingsHelper settings;

  public void albumSelected(MenuItem menuItem, Album entry) {

    final String qContext = Protocol.LibraryQueueAlbum;
    final String gSub = Protocol.LibraryAlbumTracks;
    String query = entry.getAlbum();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_album_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_album_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_album_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_album_tracks:
        ua = new UserAction(gSub, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void artistSelected(MenuItem menuItem, Artist entry) {
    final String qContext = Protocol.LibraryQueueArtist;
    final String gSub = Protocol.LibraryArtistAlbums;
    String query = entry.getArtist();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_artist_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_artist_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_artist_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_artist_album:
        ua = new UserAction(gSub, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void genreSelected(MenuItem menuItem, Genre entry) {
    final String qContext = Protocol.LibraryQueueGenre;
    final String gSub = Protocol.LibraryGenreArtists;
    String query = entry.getGenre();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_genre_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_genre_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_genre_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_genre_artists:
        ua = new UserAction(gSub, query);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void trackSelected(MenuItem menuItem, Track entry) {
    final String qContext = Protocol.LibraryQueueTrack;
    final String query = entry.getSrc();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_track_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_track_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_track_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
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
      UserAction data = new UserAction(Protocol.LibraryAlbumTracks, album.getAlbum());
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
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
      UserAction data = new UserAction(Protocol.LibraryArtistAlbums, artist.getArtist());
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
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
      UserAction action = new UserAction(Protocol.LibraryGenreArtists, genre.getGenre());
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, action);
      bus.post(event);
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
}
