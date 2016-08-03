package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.NowPlaying;
import com.kelsos.mbrc.data.NowPlaying_Table;
import com.kelsos.mbrc.rx.MapWithIndex;
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>
    implements ItemTouchHelperAdapter, FlowCursorList.OnCursorRefreshListener<NowPlaying> {

  private FlowQueryList<NowPlaying> data;
  private int playingTrackIndex;
  private Typeface robotoRegular;
  private LayoutInflater inflater;
  private NowPlaying temporary;
  private NowPlayingListener listener;

  @Inject public NowPlayingAdapter(Context context) {
    data = SQLite.select()
        .from(NowPlaying.class)
        .orderBy(NowPlaying_Table.position, true)
        .flowQueryList();
    data.setTransact(true);
    data.addOnCursorRefreshListener(this);
    inflater = LayoutInflater.from(context);
    robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
  }

  public int getPlayingTrackIndex() {
    return this.playingTrackIndex;
  }

  public void setPlayingTrackIndex(NowPlaying track) {
    Observable.from(data).compose(MapWithIndex.instance()).filter(indexed -> {
      final NowPlaying info = indexed.value();
      return info.equals(track);
    }).subscribe(indexed -> setPlayingTrackIndex((int) indexed.index()), throwable -> {
      Timber.v(throwable, "Failed");
    });
  }

  public void setPlayingTrackIndex(int index) {
    notifyItemChanged(playingTrackIndex);
    this.playingTrackIndex = index;
    notifyItemChanged(index);
  }

  @Override public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.ui_list_track_item, parent, false);
    TrackHolder holder = new TrackHolder(view, robotoRegular);
    holder.itemView.setOnClickListener(v -> onClick(holder));
    holder.container.setOnClickListener(v -> onClick(holder));
    return holder;
  }

  private void onClick(TrackHolder holder) {
    if (listener == null) {
      return;
    }

    int position = holder.getAdapterPosition();
    setPlayingTrackIndex(position);
    listener.onPress(position);
  }

  @Override public void onBindViewHolder(TrackHolder holder, int position) {
    NowPlaying track = data.get(position);
    holder.title.setText(track.getTitle());
    holder.artist.setText(track.getArtist());
    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing);
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent);
    }
  }

  @Override public int getItemCount() {
    return data.size();
  }

  @Override public boolean onItemMove(int from, int to) {
    swapPositions(from, to);

    if (listener != null) {
      listener.onMove(from, to);
    }

    notifyItemMoved(from, to);

    return true;
  }

  private void swapPositions(int from, int to) {
    Timber.v("Swapping %d => %d", from, to);
    final NowPlaying fromTrack = data.get(from);
    final NowPlaying toTrack = data.get(to);
    Timber.v("from => %s to => %s", fromTrack, toTrack);
    final Integer position = toTrack.getPosition();
    toTrack.setPosition(fromTrack.getPosition());
    fromTrack.setPosition(position);
    toTrack.save();
    fromTrack.save();
    // Before saving remove the listener to avoid interrupting the swapping functionality
    data.removeOnCursorRefreshListener(this);
    data.refresh();
    data.addOnCursorRefreshListener(this);
    Timber.v("after swap => from => %s to => %s", fromTrack, toTrack);
  }

  @Override public void onItemDismiss(int position) {
    data.remove(position);
    notifyItemRemoved(position);
    if (listener != null) {
      listener.onDismiss(position);
    }
  }

  public void refresh() {
    data.refreshAsync();
  }

  public void setListener(NowPlayingListener listener) {
    this.listener = listener;
  }

  /**
   * Callback when cursor refreshes.
   *
   * @param cursorList The object that changed.
   */
  @Override public void onCursorRefreshed(FlowCursorList<NowPlaying> cursorList) {
    notifyDataSetChanged();
  }

  public interface NowPlayingListener {
    void onPress(int position);

    void onMove(int from, int to);

    void onDismiss(int position);
  }

  static class TrackHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.track_title)
    TextView title;
    @BindView(R.id.track_artist)
    TextView artist;
    @BindView(R.id.track_indicator_view)
    ImageView trackPlaying;
    @BindView(R.id.track_container)
    FrameLayout container;

    TrackHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      title.setTypeface(typeface);
      artist.setTypeface(typeface);
    }
  }
}
