package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.ui.SquareImageView;
import com.kelsos.mbrc.utilities.FontUtils;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
  private final File coversDir;
  private List<Track> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject public TrackAdapter(Context context) {
    this.data = new ArrayList<>();
    robotoRegular = FontUtils.getRobotoRegular(context);
    coversDir = new File(context.getFilesDir(), "covers");
  }

  public void updateData(List<Track> data) {
    this.data = data;
    notifyDataSetChanged();
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_list_library_track, parent, false);
    return new ViewHolder(view, robotoRegular);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Track entry = data.get(position);

    holder.title.setText(entry.getTitle());
    holder.artist.setText(entry.getArtist());
    final String cover = entry.getCover();
    if (cover != null) {

      final File image = new File(coversDir, cover);

      Picasso.with(holder.itemView.getContext())
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .tag(holder.itemView.getContext())
          .into(holder.cover);
    }

    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_track);
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (mListener != null) {
          mListener.onMenuItemSelected(menuItem, entry);
          return true;
        }
        return false;
      });
      popupMenu.show();
    });

    holder.itemView.setOnClickListener(v -> {
      if (mListener != null) {
        mListener.onItemClicked(entry);
      }
    });
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override public int getItemCount() {
    return data.size();
  }

  public void clearData() {
    data.clear();
  }

  public void appendData(List<Track> tracks) {
    int previousSize = data.size();
    data.addAll(tracks);
    notifyItemRangeInserted(previousSize, data.size() - 1);
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem item, Track track);

    void onItemClicked(Track track);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.track_cover) SquareImageView cover;
    @Bind(R.id.line_one) TextView artist;
    @Bind(R.id.line_two) TextView title;
    @Bind(R.id.ui_item_context_indicator) LinearLayout indicator;

    public ViewHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      title.setTypeface(typeface);
      artist.setTypeface(typeface);
    }
  }
}
