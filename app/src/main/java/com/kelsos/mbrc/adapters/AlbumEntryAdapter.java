package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Album_Table;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class AlbumEntryAdapter extends RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder> {
  private final LayoutInflater inflater;
  private FlowQueryList<Album> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject public AlbumEntryAdapter(Context context) {
    this.data = new FlowQueryList<>(SQLite.select()
        .from(Album.class)
        .orderBy(Album_Table.artist, true)
        .orderBy(Album_Table.album, true));
    robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
    inflater = LayoutInflater.from(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.ui_list_dual, parent, false);
    ViewHolder holder = new ViewHolder(view, robotoRegular);
    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_album);
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (mListener == null) {
          return false;
        }
        mListener.onMenuItemSelected(menuItem, data.get(holder.getAdapterPosition()));
        return true;
      });
      popupMenu.show();
    });

    holder.itemView.setOnClickListener(v -> {
      if (mListener == null) {
        return;
      }
      mListener.onItemClicked(data.get(holder.getAdapterPosition()));
    });
    return holder;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Album entry = data.get(position);
    holder.album.setText(entry.getAlbum());
    holder.artist.setText(TextUtils.isEmpty(entry.getArtist()) ? holder.unknownArtist : entry.getArtist());
  }

  @Override public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, Album entry);

    void onItemClicked(Album album);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_two) TextView artist;
    @BindView(R.id.line_one) TextView album;
    @BindView(R.id.ui_item_context_indicator) LinearLayout indicator;
    @BindString(R.string.unknown_artist) String unknownArtist;

    public ViewHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      album.setTypeface(typeface);
      artist.setTypeface(typeface);
    }
  }
}
