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
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.utilities.FontUtils;
import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
  private ArrayList<TrackDao> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject
  public TrackAdapter(Context context) {
    this.data = new ArrayList<>();
    robotoRegular = FontUtils.getRobotoRegular(context);
  }

  public void updateData(ArrayList<TrackDao> data) {
    this.data = data;
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_list_dual, parent, false);
    return new ViewHolder(view, robotoRegular);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final TrackDao entry = data.get(position);
    final ArtistDao artist = entry.getArtist();
    holder.title.setText(entry.getTitle());
    holder.artist.setText(artist.getName());

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

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, TrackDao entry);

    void onItemClicked(TrackDao track);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
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
