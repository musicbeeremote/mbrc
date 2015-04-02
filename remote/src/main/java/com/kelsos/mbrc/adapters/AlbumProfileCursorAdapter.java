package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.dao.TrackHelper;
import rx.Observable;
import rx.subjects.PublishSubject;

public class AlbumProfileCursorAdapter extends CursorAdapter {

  private LayoutInflater inflater;
  private PublishSubject<Pair<MenuItem, Track>> menuClickPublisher;

  @Inject public AlbumProfileCursorAdapter(Context context) {
    super(context, null, 0);
    inflater = LayoutInflater.from(context);
    menuClickPublisher = PublishSubject.create();
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
    final View view = inflater.inflate(R.layout.listitem_single, viewGroup, false);
    ViewHolder holder = new ViewHolder();
    holder.lineOne = (TextView) view.findViewById(R.id.line_one);
    holder.overflow = view.findViewById(R.id.ui_item_context_indicator);
    view.setTag(holder);
    return view;
  }

  @Override public void bindView(final View view, Context context, Cursor cursor) {
    ViewHolder holder = (ViewHolder) view.getTag();
    final Track track = TrackHelper.fromCursor(cursor);
    holder.lineOne.setText(track.getTitle());
    holder.overflow.setOnClickListener(v -> showPopup(v, track));
  }

  private void showPopup(View view, Track track) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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

  private static class ViewHolder {
    TextView lineOne;
    View overflow;
  }
}
