package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.Playlist;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

  private final LayoutInflater inflater;
  private List<Playlist> data;
  private OnPlaylistPressedListener playlistPressedListener;

  @Inject public PlaylistAdapter(Activity context) {
    inflater = LayoutInflater.from(context);
    data = new ArrayList<>();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.listitem_single, parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    viewHolder.context.setVisibility(View.GONE);
    viewHolder.itemView.setOnClickListener(v -> {
      if (playlistPressedListener == null) {
        return;
      }
      playlistPressedListener.playlistPressed(data.get(viewHolder.getAdapterPosition()).getUrl());
    });
    return viewHolder;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Playlist playlist = data.get(holder.getAdapterPosition());
    holder.name.setText(playlist.getName());
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public void update(List<Playlist> playlist) {
    this.data.clear();

    if (playlist == null) {
      return;
    }

    this.data = playlist;
    notifyDataSetChanged();
  }

  public void setPlaylistPressedListener(OnPlaylistPressedListener playlistPressedListener) {
    this.playlistPressedListener = playlistPressedListener;
  }

  public interface OnPlaylistPressedListener {
    void playlistPressed(String path);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_one) TextView name;
    @BindView(R.id.ui_item_context_indicator) LinearLayout context;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
