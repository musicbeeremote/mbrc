package com.kelsos.mbrc.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.PlaylistTrack;
import java.util.ArrayList;
import java.util.List;

public class PlaylistTrackAdapter
    extends RecyclerView.Adapter<PlaylistTrackAdapter.PlaylistTrackViewHolder> {

  private LayoutInflater inflater;
  private MenuItemSelectedListener listener;
  private List<PlaylistTrack> data;

  public PlaylistTrackAdapter() {
    data = new ArrayList<>();
  }

  private void showPopup(View view, PlaylistTrack track) {
    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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

  @Override public PlaylistTrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (inflater == null) {
      inflater = LayoutInflater.from(parent.getContext());
    }

    final View view = inflater.inflate(R.layout.ui_list_dual, parent, false);
    return new PlaylistTrackViewHolder(view);
  }

  @Override public void onBindViewHolder(PlaylistTrackViewHolder holder, int position) {
    final PlaylistTrack track = data.get(position);
    holder.lineOne.setText(track.getTitle());
    holder.lineTwo.setText(track.getArtist());
    holder.overflow.setOnClickListener(v -> showPopup(v, track));
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClicked(track);
      }
    });
  }

  @Override public int getItemCount() {
    return data.size();
  }

  public PlaylistTrackAdapter setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    this.listener = listener;
    return this;
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, PlaylistTrack track);

    void onItemClicked(PlaylistTrack track);
  }

  public static final class PlaylistTrackViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.line_one) TextView lineOne;
    @Bind(R.id.line_two) TextView lineTwo;
    @Bind(R.id.ui_item_context_indicator) View overflow;

    public PlaylistTrackViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
