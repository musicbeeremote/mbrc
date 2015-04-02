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
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.GenreHelper;
import rx.Observable;
import rx.subjects.PublishSubject;

public class GenreCursorAdapter extends CursorAdapter {

  private LayoutInflater inflater;
  private PublishSubject<Pair<MenuItem, Genre>> menuClickPublisher;

  @Inject public GenreCursorAdapter(Context context) {
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

  @Override public void bindView(final View view, final Context context, Cursor cursor) {
    final Genre genre = GenreHelper.fromCursor(cursor);
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.lineOne.setText(genre.getName());
    holder.overflow.setOnClickListener(v -> showPopup(v, genre));
  }

  private void showPopup(View view, Genre genre) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    popupMenu.inflate(R.menu.popup_genre);
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      menuClickPublisher.onNext(new Pair<>(menuItem, genre));
      return true;
    });
    popupMenu.show();
  }

  public Observable<Pair<MenuItem, Genre>> getPopupObservable() {
    return menuClickPublisher.asObservable();
  }

  public static class ViewHolder {
    TextView lineOne;
    View overflow;
  }
}
