package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.Track;
import java.util.ArrayList;
import java.util.List;

public class AlbumProfileAdapter extends RecyclerView.Adapter<AlbumProfileAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private List<Track> data;
  private MenuItemSelectedListener listener;

  @Inject public AlbumProfileAdapter(Context context) {
    inflater = LayoutInflater.from(context);
    data = new ArrayList<>();
  }

  public void updateData(List<Track> data) {
    this.data.clear();
    this.data.addAll(data);
    notifyDataSetChanged();
  }

  private void showPopup(View view, Track track) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    popupMenu.inflate(R.menu.popup_track);
    popupMenu.setOnMenuItemClickListener(menuItem -> {
      if (listener != null) {
        listener.onMenuItemSelected(menuItem, track);
        return true;
      }
      return false;
    });
    popupMenu.show();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.listitem_single, parent, false);
    ViewHolder holder = new ViewHolder(view);
    holder.overflow.setOnClickListener(v -> {
      int position = holder.getAdapterPosition();
      Track track = data.get(position);
      showPopup(v, track);
    });
    holder.itemView.setOnClickListener(v -> {
      if (listener !=  null) {
        int position = holder.getAdapterPosition();
        Track track = data.get(position);
        listener.onItemClicked(track);
      }
    });
    return holder;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Track track = data.get(position);
    holder.lineOne.setText(track.getTitle());
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public void setListener(MenuItemSelectedListener listener) {
    this.listener = listener;
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, Track entry);

    void onItemClicked(Track track);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.line_one) TextView lineOne;
    @Bind(R.id.ui_item_context_indicator) View overflow;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
