package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.DataArrayAdapter;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.PlaylistArrayAdapter;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.UserActionEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;

public class LibraryArtistsFragment extends RoboSherlockListFragment {
    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    private Bus bus;

    private DataArrayAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bus.register(this);
        setHasOptionsMenu(true);
        afProvider.addActiveFragment(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(this.getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.simple_list_layout, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_LIBRARY_ALL_ARTISTS));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        afProvider.addActiveFragment(this);
        bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_LIBRARY_ALL_ARTISTS));
    }

    @Override
    public void onPause()
    {
        afProvider.removeActiveFragment(this);
        super.onPause();
    }

    @Override
    public void onStop()
    {
        afProvider.removeActiveFragment(this);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        afProvider.removeActiveFragment(this);
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        return super.onContextItemSelected(item);
    }

    public void updateListData(ArrayList<String> list)
    {
        adapter = new DataArrayAdapter(getActivity(), R.layout.playlistview_item, list);
        setListAdapter(adapter);
    }
}
