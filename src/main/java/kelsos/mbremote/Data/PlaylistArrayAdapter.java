package kelsos.mbremote.Data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import kelsos.mbremote.R;

import java.util.ArrayList;

public class PlaylistArrayAdapter extends ArrayAdapter<MusicTrack> {
    private Context _context;
    private int _layoutResourceId;
    private ArrayList<MusicTrack> _nowPlayingList;

    public PlaylistArrayAdapter(Context context, int resource, ArrayList<MusicTrack> objects) {
        super(context, resource, objects);
        this._layoutResourceId = resource;
        this._context = context;
        this._nowPlayingList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TrackHolder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) _context).getLayoutInflater();
            row = layoutInflater.inflate(_layoutResourceId, parent, false);

            holder = new TrackHolder();
            holder.title = (TextView) row.findViewById(R.id.trackTitle);
            holder.artist = (TextView) row.findViewById(R.id.trackArtist);

            row.setTag(holder);
        } else {
            holder = (TrackHolder) row.getTag();
        }

        MusicTrack track = _nowPlayingList.get(position);
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());

        return row;
    }

    static class TrackHolder {
        TextView title;
        TextView artist;

    }

}
