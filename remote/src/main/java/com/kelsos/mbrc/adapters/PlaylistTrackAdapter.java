package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;

import java.util.List;

public class PlaylistTrackAdapter extends ArrayAdapter<NowPlayingTrack>{
    private Context mContext;
    private int mResource;
    private List<NowPlayingTrack> mList;
    private Typeface robotoLight;

    public PlaylistTrackAdapter(Context context, int resource, List<NowPlayingTrack> objects) {
        super(context, resource, objects);
        this.mResource = resource;
        this.mContext = context;
        this.mList = objects;
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TrackHolder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(mResource, parent, false);

            holder = new TrackHolder();
            holder.title = (TextView) row.findViewById(R.id.trackTitle);
            holder.artist = (TextView) row.findViewById(R.id.trackArtist);
            holder.trackPlaying = (ImageView) row.findViewById(R.id.listview_item_image);
            holder.title.setTypeface(robotoLight);
            holder.artist.setTypeface(robotoLight);

            row.setTag(holder);
        } else {
            holder = (TrackHolder) row.getTag();
        }

        NowPlayingTrack track = mList.get(position);
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());
        holder.trackPlaying.setImageResource(android.R.color.transparent);
        holder.trackPlaying.setOnClickListener(showContextMenu);

        return row;
    }

    private final View.OnClickListener showContextMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.showContextMenu();
        }
    };

    static class TrackHolder {
        TextView title;
        TextView artist;
        ImageView trackPlaying;

    }
}
