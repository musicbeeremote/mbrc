package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.kelsos.mbrc.domain.QueueTrack;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import roboguice.util.Ln;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.TrackHolder> {
  private List<QueueTrack> data;
  private int playingTrackIndex;
  private LayoutInflater inflater;
  private OnUserActionListener listener;

  public NowPlayingAdapter() {
    data = new ArrayList<>();
    setHasStableIds(true);
  }

  public void updateData(List<QueueTrack> data) {
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  public void clearData() {
    this.data.clear();
    notifyDataSetChanged();
  }

  public int getPlayingTrackIndex() {
    return this.playingTrackIndex;
  }

  public void setPlayingTrackIndex(int index) {
    this.playingTrackIndex = index;
    notifyDataSetChanged();
  }

  @Override public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (inflater == null) {
      final Context context = parent.getContext();
      inflater = LayoutInflater.from(context);
    }
    final View view = inflater.inflate(R.layout.ui_list_track_item, parent, false);
    return new TrackHolder(view);
  }

  @Override public void onBindViewHolder(TrackHolder holder, int position) {
    QueueTrack track = data.get(position);
    holder.title.setText(track.getTitle());
    holder.artist.setText(track.getArtist());

    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing);
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent);
    }

    holder.container.setOnClickListener(v -> {
      Ln.v("Clicked");
      if (listener == null) {
        return;
      }

      listener.onItemClicked(position, track);
    });
  }

  @Override public int getItemCount() {
    return data.size();
  }

  @Override public long getItemId(int position) {
    return data.get(position).getPosition();
  }

 public void onMoveItem(int from, int to) {
    final QueueTrack track = data.get(from);
    data.remove(track);
    data.add(to, track);

    notifyItemMoved(from, to);

    if (listener != null) {
      listener.onTrackMoved(from, to);
    }
  }


  public void setOnUserActionListener(OnUserActionListener listener) {
    this.listener = listener;
  }

  /**
   * This method is used to restore the tracks to their original positions in case the move
   * failed to complete.
   *
   * @param from The original position of the {@link QueueTrackDao}.
   * @param to The position the element was original moved to.
   */
  public void restorePositions(int from, int to) {
    final QueueTrack track = data.get(to);
    data.remove(track);
    data.add(from, track);
  }

  public void insert(QueueTrack track, int index) {
    data.add(index, track);
  }

  public void setPlayingTrack(QueueTrack track) {
    setPlayingTrackIndex(data.indexOf(track));
    notifyDataSetChanged();
  }

  public interface OnUserActionListener {
    void onTrackRemoved(int position);

    void onTrackMoved(int from, int to);

    void onItemClicked(int position, QueueTrack track);
  }

  static class TrackHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.drag_handle) View dragHandle;
    @Bind(R.id.track_title) TextView title;
    @Bind(R.id.track_artist) TextView artist;
    @Bind(R.id.track_indicator_view) ImageView trackPlaying;
    @Bind(R.id.container) FrameLayout container;

    public TrackHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
