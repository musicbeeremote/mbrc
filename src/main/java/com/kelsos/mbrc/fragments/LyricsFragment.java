package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.Arrays;

public class LyricsFragment extends RoboSherlockFragment {
    @InjectView(R.id.lyrics_list_view) ListView lyricsView;
    @Inject Bus bus;
    @Inject ActiveFragmentProvider afProvider;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        String array[] = {""};
        lyricsView.setAdapter(new LyricsAdapter(getActivity(), R.layout.ui_list_lyrics_item,
                new ArrayList<String>(Arrays.asList(array))

        ));
        bus.register(this);
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe public void updateLyricsData(LyricsUpdated update) {
        String array[] = update.getLyrics().split("\r\n");
        lyricsView.setAdapter(new LyricsAdapter(getActivity(), R.layout.ui_list_lyrics_item,
                new ArrayList<String>(Arrays.asList(array))));

    }
}
