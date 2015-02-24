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
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.AlbumEntry;

import java.util.ArrayList;

public class AlbumEntryAdapter extends RecyclerView.Adapter<AlbumEntryAdapter.ViewHolder> {
    private ArrayList<AlbumEntry> mData;
    private Typeface robotoRegular;
    private MenuItemSelectedListener mListener;

    public AlbumEntryAdapter(Context context, ArrayList<AlbumEntry> data) {
        this.mData = data;
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_list_dual, parent, false);
        return new ViewHolder(view, robotoRegular);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final AlbumEntry entry = mData.get(position);
        holder.album.setText(entry.getAlbum());
        holder.artist.setText(entry.getArtist());

        holder.indicator.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.popup_album);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem menuItem) {
                        if (mListener != null) {
                            mListener.onMenuItemSelected(menuItem, entry);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(entry);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
        mListener = listener;
    }

    public interface MenuItemSelectedListener {
        void onMenuItemSelected(MenuItem menuItem, AlbumEntry entry);

        void onItemClicked(AlbumEntry album);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        TextView album;
        LinearLayout indicator;

        public ViewHolder(View itemView, Typeface typeface) {
            super(itemView);
            album = (TextView) itemView.findViewById(R.id.line_one);
            artist = (TextView) itemView.findViewById(R.id.line_two);
            indicator = (LinearLayout) itemView.findViewById(R.id.ui_item_context_indicator);
            album.setTypeface(typeface);
            artist.setTypeface(typeface);
        }
    }
}
