package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.Playlist;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private List<Playlist> data;
  private int selection;

  @Inject public PlaylistDialogAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    data = new ArrayList<>();
  }

  public void update(List<Playlist> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  public Playlist getSelectedPlaylist() {
    return this.data.get(selection);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.listitem_single_text_only, parent, false);
    final ViewHolder holder = new ViewHolder(view);
    holder.itemView.setOnClickListener(v -> {
      selection = holder.getAdapterPosition();
    });
    return holder;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Playlist playlist = data.get(holder.getAdapterPosition());
    holder.text.setText(playlist.getName());
    holder.itemView.setSelected(selection == holder.getAdapterPosition());
  }

  @Override public int getItemCount() {
    return data.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.line_one) TextView text;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
