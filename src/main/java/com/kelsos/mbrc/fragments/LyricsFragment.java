package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import roboguice.inject.InjectView;

public class LyricsFragment extends RoboSherlockFragment
{
	@InjectView (R.id.lyrics_list_view)
	ListView lyricsView;
	@InjectView(R.id.lyrics_view_track_info_label)
	TextView trackInfo;
	@Inject
	Bus bus;
	@Inject
	ActiveFragmentProvider afProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		afProvider.addActiveFragment(this);
	}

    @Override
    public void onStart(){
        super.onStart();
        String array[] = {""};
        lyricsView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.ui_list_lyrics_item,array));
        bus.post(new MessageEvent(UserInputEvent.RequestLyrics));
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
	}

	@Override
	public void onDestroy()
	{
		afProvider.addActiveFragment(this);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void updateLyricsData(String lyrics, String artist, String title)
	{
		String array[] = lyrics.split("\r\n");
		lyricsView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.ui_list_lyrics_item,array));
		trackInfo.setText(artist + " - " + title);
	}
}
