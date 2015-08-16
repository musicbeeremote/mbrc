package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.utilities.FontUtils;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
  private ArrayList<Album> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject public AlbumAdapter(Context context) {
    this.data = new ArrayList<>();
    robotoRegular = FontUtils.getRobotoRegular(context);
  }

  public void updateData(ArrayList<Album> data) {
    this.data = data;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_album, parent, false);
    return new ViewHolder(view, robotoRegular);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Album album = data.get(position);
    final Artist albumArtist = album.getArtist();
    final Cover cover = album.getCover();
    final View itemView = holder.itemView;

    holder.album.setText(album.getName());
    holder.artist.setText(albumArtist.getName());

    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_album);
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (mListener != null) {
          mListener.onMenuItemSelected(menuItem, album);
          return true;
        }
        return false;
      });
      popupMenu.show();
    });

    itemView.setOnClickListener(v -> {
      if (mListener != null) {
        mListener.onItemClicked(album);
      }
    });

    if (cover != null) {
      final File image = new File(RemoteUtils.getStorage(), cover.getHash());

      Picasso.with(itemView.getContext())
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .tag(itemView.getContext())
          .into(holder.image);
    }
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
    @Bind(R.id.line_two) TextView artist;
    @Bind(R.id.line_one) TextView album;
    @Bind(R.id.ui_grid_image) ImageView image;
    @Bind(R.id.ui_item_context_indicator) LinearLayout indicator;

    public ViewHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      album.setTypeface(typeface);
      artist.setTypeface(typeface);
    }
  }
}
