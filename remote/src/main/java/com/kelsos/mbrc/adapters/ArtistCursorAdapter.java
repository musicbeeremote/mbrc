package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.ArtistHelper;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ArtistCursorAdapter extends CursorAdapter {

  private PublishSubject<Pair<MenuItem, Artist>> menuClickPublisher;
  private LayoutInflater inflater;

  @Inject public ArtistCursorAdapter(Context context) {
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
    final Artist artist = ArtistHelper.fromCursor(cursor);
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.lineOne.setText(artist.getName());
    holder.overflow.setOnClickListener(v -> showPopup(v, artist));
  }

  private void showPopup(View view, Artist artist) {
    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    popupMenu.inflate(R.menu.popup_artist);
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      menuClickPublisher.onNext(new Pair<>(menuItem, artist));
      return true;
    });
    popupMenu.show();
  }

  public Observable<Pair<MenuItem, Artist>> getPopupObservable() {
    return menuClickPublisher.asObservable();
  }

  public static class ViewHolder {
    TextView lineOne;
    View overflow;
  }
}
