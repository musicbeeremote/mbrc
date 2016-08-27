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
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.data.library.Track_Table;
import com.kelsos.mbrc.domain.AlbumInfo;
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

public class TrackEntryAdapter extends RecyclerView.Adapter<TrackEntryAdapter.ViewHolder> {
  private FlowCursorList<Track> data;
  private MenuItemSelectedListener mListener;
  private LayoutInflater inflater;

  @Inject
  public TrackEntryAdapter(Activity context) {
    inflater = LayoutInflater.from(context);
  }

  public void init(@Nullable AlbumInfo filter) {
    if (data != null) {
      return;
    }

    final Where<Track> query;

    if (filter == null) {
      query = allTracks();
    } else {
      String album = filter.album();

      if (TextUtils.isEmpty(album)) {
        query = nonAlbumTracks(filter.artist());
      } else {
        query = albumTracks(album);
      }

    }

    Single.create((SingleSubscriber<? super FlowCursorList<Track>> subscriber) -> {
      FlowCursorList<Track> list = new FlowCursorList<>(query);
      subscriber.onSuccess(list);
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(genres -> {
      data = genres;
      notifyDataSetChanged();
    }, throwable -> {
      Timber.v(throwable, "failed to load the data");
    });
  }

  private Where<Track> nonAlbumTracks(String artist) {
    Where<Track> query;
    query = SQLite.select()
        .from(Track.class)
        .where(Track_Table.album.is(""))
        .and(Track_Table.artist.is(artist))
        .orderBy(Track_Table.album_artist, true)
        .orderBy(Track_Table.album, true)
        .orderBy(Track_Table.disc, true)
        .orderBy(Track_Table.trackno, true);
    return query;
  }

  private Where<Track> albumTracks(String album) {
    Where<Track> query;
    query = SQLite.select()
        .from(Track.class)
        .where(Track_Table.album.like('%' + album + '%'))
        .orderBy(Track_Table.album_artist, true)
        .orderBy(Track_Table.album, true)
        .orderBy(Track_Table.disc, true)
        .orderBy(Track_Table.trackno, true);
    return query;
  }

  private Where<Track> allTracks() {
    Where<Track> query;
    query = SQLite.select()
        .from(Track.class)
        .orderBy(Track_Table.album_artist, true)
        .orderBy(Track_Table.album, true)
        .orderBy(Track_Table.disc, true)
        .orderBy(Track_Table.trackno, true);
    return query;
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  /**
   * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
   * an item.
   * <p/>
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   * <p/>
   * The new ViewHolder will be used to display items of the adapter using
   * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
   * items in the data set, it is a good idea to cache references to sub views of the View to
   * avoid unnecessary {@link View#findViewById(int)} calls.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   * an adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(ViewHolder, int)
   */
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.ui_list_dual, parent, false);
    ViewHolder holder = new ViewHolder(view);
    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_track);
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

  /**
   * Called by RecyclerView to display the data at the specified position. This method
   * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
   * the given position.
   * <p/>
   * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this
   * method again if the position of the item changes in the data set unless the item itself
   * is invalidated or the new position cannot be determined. For this reason, you should only
   * use the <code>position</code> parameter while acquiring the related data item inside this
   * method and should not keep a copy of it. If you need the position of an item later on
   * (e.g. in a click listener), use {@link ViewHolder#getPosition()} which will have the
   * updated position.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the
   * item at the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Track entry = data.getItem(position);
    holder.title.setText(entry.getTitle());
    String artist = entry.getArtist();
    holder.artist.setText(TextUtils.isEmpty(artist) ? holder.unknownArtist : artist);
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return data != null ? data.getCount() : 0;
  }

  public void refresh() {
    data.refresh();
    notifyDataSetChanged();
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, Track entry);

    void onItemClicked(Track track);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_two)
    TextView artist;
    @BindView(R.id.line_one)
    TextView title;
    @BindView(R.id.ui_item_context_indicator)
    LinearLayout indicator;
    @BindString(R.string.unknown_artist)
    String unknownArtist;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
