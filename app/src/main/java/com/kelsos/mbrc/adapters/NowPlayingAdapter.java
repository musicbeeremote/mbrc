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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.ui.drag.ItemTouchHelperAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>
    implements ItemTouchHelperAdapter {

  private List<MusicTrack> nowPlayingList;
  private int playingTrackIndex;
  private Typeface robotoRegular;
  private LayoutInflater inflater;
  private MusicTrack temporary;
  private NowPlayingListener listener;

  @Inject public NowPlayingAdapter(Context context) {
    nowPlayingList = new ArrayList<>();
    inflater = LayoutInflater.from(context);
    robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
  }

  public int getPlayingTrackIndex() {
    return this.playingTrackIndex;
  }

  public void setPlayingTrackIndex(int index) {
    notifyItemChanged(playingTrackIndex);
    this.playingTrackIndex = index;
    notifyItemChanged(index);
  }

  public void setPlayingTrackIndex(MusicTrack track) {
    setPlayingTrackIndex(nowPlayingList.indexOf(track));
  }

  public void update(List<MusicTrack> nowPlayingList) {
    this.nowPlayingList.clear();
    if (nowPlayingList == null) {
      notifyDataSetChanged();
      return;
    }

    this.nowPlayingList = nowPlayingList;
    notifyDataSetChanged();
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
    MusicTrack track = nowPlayingList.get(position);
    holder.title.setText(track.getTitle());
    holder.artist.setText(track.getArtist());
    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing);
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent);
    }
  }

  @Override public int getItemCount() {
    return nowPlayingList.size();
  }

  @Override public boolean onItemMove(int from, int to) {
    if (from < to) {
      for (int i = from; i < to; i++) {
        Collections.swap(nowPlayingList, i, i + 1);
      }
    } else {
      for (int i = from; i > to; i--) {
        Collections.swap(nowPlayingList, i, i - 1);
      }
    }

    if (listener != null) {
      listener.onMove(from, to);
    }

    notifyItemMoved(from, to);

    return true;
  }

  @Override public void onItemDismiss(int position) {
    nowPlayingList.remove(position);
    notifyItemRemoved(position);
    if (listener != null) {
      listener.onDismiss(position);
    }
  }

  public void setListener(NowPlayingListener listener) {
    this.listener = listener;
  }

  public interface NowPlayingListener {
    void onPress(int position);

    void onMove(int from, int to);

    void onDismiss(int position);
  }

  static class TrackHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.track_title) TextView title;
    @BindView(R.id.track_artist) TextView artist;
    @BindView(R.id.track_indicator_view) ImageView trackPlaying;
    @BindView(R.id.track_container) FrameLayout container;

    public TrackHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      title.setTypeface(typeface);
      artist.setTypeface(typeface);
    }
  }
}
