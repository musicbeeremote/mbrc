package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Track;
import rx.Observable;
import rx.subjects.PublishSubject;

public class TrackCursorAdapter extends CursorAdapter {

  private final LayoutInflater inflater;
  private PublishSubject<Pair<MenuItem, Track>> menuClickPublisher;

  @Inject public TrackCursorAdapter(Context context) {
    super(context, null, 0);
    inflater = LayoutInflater.from(context);
    menuClickPublisher = PublishSubject.create();
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
    final View view = inflater.inflate(R.layout.ui_list_dual, viewGroup, false);
    ViewHolder holder = new ViewHolder();
    holder.lineOne = (TextView) view.findViewById(R.id.line_one);
    holder.lineTwo = (TextView) view.findViewById(R.id.line_two);
    holder.overflow = view.findViewById(R.id.ui_item_context_indicator);
    view.setTag(holder);
    return view;
  }

  @Override public void bindView(final View view, Context context, Cursor cursor) {
    final Track track = new Track();

    final Artist artist = track.getArtist();

    ViewHolder holder = (ViewHolder) view.getTag();
    holder.lineOne.setText(track.getTitle());
    holder.lineTwo.setText(artist != null ? artist.getName() : "");
    holder.overflow.setOnClickListener(v -> showPopup(v, track));
  }

  private void showPopup(View view, Track track) {
    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    popupMenu.inflate(R.menu.popup_track);
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      menuClickPublisher.onNext(new Pair<>(menuItem, track));
      return true;
    });
    popupMenu.show();
  }

  public Observable<Pair<MenuItem, Track>> getPopupObservable() {
    return menuClickPublisher.asObservable();
  }

  public static class ViewHolder {
    TextView lineOne;
    TextView lineTwo;
    View overflow;
  }
}
