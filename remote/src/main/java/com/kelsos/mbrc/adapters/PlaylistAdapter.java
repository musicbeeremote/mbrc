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
import com.kelsos.mbrc.dao.Playlist;
import com.kelsos.mbrc.ui.activities.PlaylistActivity;
import com.kelsos.mbrc.utilities.FontUtils;
import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
  private final LayoutInflater inflater;
  private Typeface robotoRegular;
  private List<Playlist> data;

  @Inject
  public PlaylistAdapter(Context context) {
    robotoRegular = FontUtils.getRobotoRegular(context);
    inflater = LayoutInflater.from(context);
    data = new ArrayList<>();
  }

  public void updateData(List<Playlist> data) {
    this.data = data;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.listitem_single, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Playlist playlist = new Playlist();


    holder.lineOne.setTypeface(robotoRegular);
    holder.lineOne.setText(playlist.getName());

    holder.overflow.setOnClickListener(v -> {
      PopupMenu menu = new PopupMenu(v.getContext(), v);
      menu.inflate(R.menu.popup_playlist);
      menu.show();

      menu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.playlist_tracks) {
          Bundle bundle = new Bundle();
          bundle.putString(PlaylistActivity.NAME, playlist.getName());
          bundle.putString(PlaylistActivity.PATH, playlist.getPath());
          Intent intent = new Intent(v.getContext(), PlaylistActivity.class);
          intent.putExtras(bundle);
          v.getContext().startActivity(intent);
          return true;
        }
        return false;
      });
    });
  }

  @Override public int getItemCount() {
    return data.size();
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
