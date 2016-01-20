package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.ui.activities.PlaylistTrackActivity;
import com.kelsos.mbrc.utilities.FontUtils;
import java.util.ArrayList;
import java.util.List;

public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.ViewHolder> {
  private final LayoutInflater inflater;
  private Typeface robotoRegular;
  private List<Playlist> data;
  private OnPlaylistPlayPressedListener onPlaylistPlayPressedListener;

  @Inject public PlaylistListAdapter(Context context) {
    robotoRegular = FontUtils.getRobotoRegular(context);
    inflater = LayoutInflater.from(context);
    data = new ArrayList<>();
  }

  public void setOnPlaylistPlayPressedListener(OnPlaylistPlayPressedListener onPlaylistPlayPressedListener) {
    this.onPlaylistPlayPressedListener = onPlaylistPlayPressedListener;
  }

  public void updateData(List<Playlist> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  @Override public long getItemId(int position) {
    return data.get(position).getId();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.listitem_single, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Playlist playlist = data.get(position);
    holder.lineOne.setTypeface(robotoRegular);
    holder.lineOne.setText(playlist.getName());
    holder.itemView.setOnClickListener(v1 -> {
      startPlaylistActivity(data.get(holder.getAdapterPosition()), v1);
    });

    holder.overflow.setOnClickListener(v -> {
      PopupMenu menu = new PopupMenu(v.getContext(), v);
      menu.inflate(R.menu.popup_playlist);
      menu.show();

      menu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.playlist_tracks) {
          startPlaylistActivity(playlist, v);
          return true;
        } else if (item.getItemId() == R.id.playlist_play) {
          if (onPlaylistPlayPressedListener != null) {
            onPlaylistPlayPressedListener.playlistPlayPressed(data.get(holder.getAdapterPosition()),
                holder.getAdapterPosition());
          }
          return true;
        }
        return false;
      });
    });
  }

  private void startPlaylistActivity(Playlist playlist, View v) {
    Bundle bundle = new Bundle();
    bundle.putString(PlaylistTrackActivity.NAME, playlist.getName());
    bundle.putLong(PlaylistTrackActivity.ID, playlist.getId());
    Intent intent = new Intent(v.getContext(), PlaylistTrackActivity.class);
    intent.putExtras(bundle);
    v.getContext().startActivity(intent);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public interface OnPlaylistPlayPressedListener {
    void playlistPlayPressed(Playlist playlist, int position);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.line_one) TextView lineOne;
    @Bind(R.id.ui_item_context_indicator) LinearLayout overflow;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
