package com.kelsos.mbrc.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.DrawerAdapter;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.data.NavigationEntry;
import com.kelsos.mbrc.enums.DisplayFragment;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.DrawerSelection;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboListFragment;
import roboguice.inject.InjectView;
import rx.Observable;

import java.util.ArrayList;

public class DrawerFragment extends RoboListFragment implements FragmentManager.OnBackStackChangedListener {

    @InjectView(R.id.menuConnector)
    private TextView menuConnector;
    @InjectView(R.id.drawer_version_indicator)
    private TextView versionIndicator;
    @InjectView(R.id.menu_exit)
    private TextView exitButton;

    private Typeface robotoMedium;
    private DrawerLayout mDrawerLayout;
    private int mSelection;
    private boolean mBackstackChanging;
    private Observable<DrawerSelection> drawerSelectionObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_medium.ttf");
        mSelection = 0;
        mBackstackChanging = false;

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt("mSelection");
        }
        getActivity().getFragmentManager().addOnBackStackChangedListener(this);
        createObservable();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_drawer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        menuConnector.setOnClickListener(v ->
                Events.Messages.onNext(new Message(EventType.START_CONNECTION)));

        menuConnector.setOnLongClickListener(view -> {
            new Message(EventType.RESET_CONNECTION);
            return false;
        });

        exitButton.setOnClickListener(v -> {
            final Activity activity = getActivity();
            activity.stopService(new Intent(activity, Controller.class));
            activity.finish();
        });
        menuConnector.setTypeface(robotoMedium);
        exitButton.setTypeface(robotoMedium);

        ArrayList<NavigationEntry> nav = new ArrayList<>();
        nav.add(new NavigationEntry(getString(R.string.nav_home)));
        nav.add(new NavigationEntry(getString(R.string.nav_library)));
        nav.add(new NavigationEntry(getString(R.string.nav_currentqueue)));
        nav.add(new NavigationEntry(getString(R.string.nav_lyrics)));
        nav.add(new NavigationEntry(getString(R.string.nav_playlists)));
        nav.add(new NavigationEntry(getString(R.string.nav_settings)));

        setListAdapter(new DrawerAdapter(getActivity(), R.layout.ui_drawer_item, nav));
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(mSelection, true);

        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        }

        versionIndicator.setText(String.format(getString(R.string.ui_drawer_menu_version), BuildConfig.VERSION_NAME));
    }

    private void createObservable() {
        drawerSelectionObservable = Observable.create(subscriber ->
                getListView().setOnItemClickListener((parent, view, position, id) -> {
                    mBackstackChanging = true;
                    DrawerSelection dEvent;
                    if (mSelection != position) {

                        getListView().setItemChecked(position, true);
                        mSelection = position;

                        DisplayFragment dfrag = DisplayFragment.values()[position];
                        dEvent = new DrawerSelection(dfrag);
                    } else {
                        dEvent = new DrawerSelection();
                    }

                    subscriber.onNext(dEvent);

                    dEvent.getNavigate();
                }));
    }

    public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        if (menuConnector == null) {
            return;
        }
        switch (change.getStatus()) {
            case CONNECTION_OFF:
                menuConnector.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_connectivy_off, 0, 0, 0);
                menuConnector.setText(R.string.drawer_connection_status_off);
                break;
            case CONNECTION_ACTIVE:
                menuConnector.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_connectivity_active, 0, 0, 0);
                menuConnector.setText(R.string.drawer_connection_status_active);
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackStackChanged() {
        if (!mBackstackChanging && getActivity().getFragmentManager().getBackStackEntryCount() == 0) {
            mSelection = 0;
            getListView().setItemChecked(mSelection, true);
        }
        mBackstackChanging = false;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("mSelection", mSelection);
        super.onSaveInstanceState(outState);
    }

    public Observable<DrawerSelection> getDrawerSelectionObservable() {
        return drawerSelectionObservable;
    }
}
