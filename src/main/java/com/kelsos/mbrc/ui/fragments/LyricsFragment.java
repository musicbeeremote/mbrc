package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.Arrays;

public class LyricsFragment extends BaseFragment {
    @InjectView(R.id.lyrics_list_view) private ListView lyricsView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
    }

    @Subscribe public void updateLyricsData(LyricsUpdated update) {
        lyricsView.setAdapter(new LyricsAdapter(getActivity(), R.layout.ui_list_lyrics_item,
                new ArrayList<String>(Arrays.asList(update.getLyrics().split("\r\n")))));

    }
}
