package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Album_Table;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.data.library.Track_Table;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AlbumEntryAdapter extends RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private FlowCursorList<Album> data;
  private MenuItemSelectedListener mListener;

  @Inject
  public AlbumEntryAdapter(Activity context) {
    inflater = LayoutInflater.from(context);
  }

  public void init(@Nullable String filter) {
    if (data != null) {
      return;
    }

    final Where<Album> query;
    if (TextUtils.isEmpty(filter)) {
      query = SQLite.select()
          .from(Album.class)
          .orderBy(Album_Table.artist, true)
          .orderBy(Album_Table.album, true);
    } else {
      query = SQLite.select()
          .from(Album.class)
          .leftOuterJoin(Track.class)
          .on(Track_Table.album.withTable().eq(Album_Table.album.withTable()))
          .where(Track_Table.artist.withTable().like('%'+filter+'%'))
          .orderBy(Album_Table.artist.withTable(), true)
          .orderBy(Album_Table.album.withTable(), true);
    }

    Single.create((SingleSubscriber<? super FlowCursorList<Album>> subscriber) -> {
      FlowCursorList<Album> list = new FlowCursorList<>(query);
      subscriber.onSuccess(list);
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(albums -> {
      data = albums;
      notifyDataSetChanged();
    }, throwable -> {
      Timber.v(throwable, "failed to load the data");
    });
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.ui_list_dual, parent, false);
    ViewHolder holder = new ViewHolder(view);
    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_album);
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (mListener == null) {
          return false;
        }
        mListener.onMenuItemSelected(menuItem, data.getItem(holder.getAdapterPosition()));
        return true;
      });
      popupMenu.show();
    });

    holder.itemView.setOnClickListener(v -> {
      if (mListener == null) {
        return;
      }
      mListener.onItemClicked(data.getItem(holder.getAdapterPosition()));
    });
    return holder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Album entry = data.getItem(position);
    holder.album.setText(TextUtils.isEmpty(entry.getAlbum()) ? holder.emptyAlbum : entry.getAlbum());
    holder.artist.setText(TextUtils.isEmpty(entry.getArtist()) ? holder.unknownArtist : entry.getArtist());
  }

  public void refresh() {
    if (data == null) {
      return;
    }

    data.refresh();
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return data != null ? data.getCount() : 0;
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, Album entry);

    void onItemClicked(Album album);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_two)
    TextView artist;
    @BindView(R.id.line_one)
    TextView album;
    @BindView(R.id.ui_item_context_indicator)
    LinearLayout indicator;
    @BindString(R.string.unknown_artist)
    String unknownArtist;
    @BindString(R.string.non_album_tracks)
    String emptyAlbum;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
