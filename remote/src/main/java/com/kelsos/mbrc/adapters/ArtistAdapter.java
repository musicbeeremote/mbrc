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
import com.kelsos.mbrc.utilities.FontUtils;
import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
  private ArrayList<ArtistDao> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject
  public ArtistAdapter(Context context) {
    this.data = new ArrayList<>();
    robotoRegular = FontUtils.getRobotoRegular(context);
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  public void updateData(ArrayList<ArtistDao> data) {
    this.data = data;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_list_single, parent, false);
    return new ViewHolder(view, robotoRegular);
  }
  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final ArtistDao entry = data.get(position);
    holder.title.setText(entry.getName());

    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_artist);
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
    return data == null ? 0 : data.size();
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, ArtistDao entry);

    void onItemClicked(ArtistDao artist);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.line_one) TextView title;
    @Bind(R.id.ui_item_context_indicator) LinearLayout indicator;

    public ViewHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      title.setTypeface(typeface);
    }
  }
}
