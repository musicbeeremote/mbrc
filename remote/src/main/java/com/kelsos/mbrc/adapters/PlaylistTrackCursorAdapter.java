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
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.PlaylistTrack;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PlaylistTrackCursorAdapter extends CursorAdapter {

  private LayoutInflater inflater;
  private PublishSubject<Pair<MenuItem, PlaylistTrack>> menuClickPublisher;

  public PlaylistTrackCursorAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    inflater = LayoutInflater.from(context);
    menuClickPublisher = PublishSubject.create();
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
    final View view = inflater.inflate(R.layout.ui_list_dual, viewGroup, false);
    ViewHolder holder = new ViewHolder();
    holder.lineOne = ((TextView) view.findViewById(R.id.line_one));
    holder.lineTwo = ((TextView) view.findViewById(R.id.line_two));
    holder.overflow = view.findViewById(R.id.ui_item_context_indicator);
    view.setTag(holder);
    return view;
  }

  @Override public void bindView(final View view, Context context, Cursor cursor) {
    // FIXME: 8/6/15 Temporarily does nothing
    final PlaylistTrack track = new PlaylistTrack();
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.lineOne.setText(track.getTitle());
    holder.lineTwo.setText(track.getArtist());
    holder.overflow.setOnClickListener(v -> showPopup(v, track));
  }

  private void showPopup(View view, PlaylistTrack track) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    //popupMenu.inflate();
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      menuClickPublisher.onNext(new Pair<>(menuItem, track));
      return true;
    });
    popupMenu.show();
  }

  public Observable<Pair<MenuItem, PlaylistTrack>> getPopupObservable() {
    return menuClickPublisher.asObservable();
  }

  private static final class ViewHolder {
    TextView lineOne;
    TextView lineTwo;
    View overflow;
  }
}
