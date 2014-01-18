package com.kelsos.mbrc.ui.fragments.profile;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.ui.base.BaseListFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistAlbumsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistAlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ArtistAlbumsFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARTIST_ID = "artistId";
    private static final int URL_LOADER = 0x832d;

    // TODO: Rename and change types of parameters
    private long artistId;

    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param artistId Parameter 1.
     * @return A new instance of fragment ArtistAlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistAlbumsFragment newInstance(long artistId) {
        ArtistAlbumsFragment fragment = new ArtistAlbumsFragment();
        Bundle args = new Bundle();
        args.putLong(ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }
    public ArtistAlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getLong(ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_albums, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Album.CONTENT_ARTIST_URI, Uri.encode(String.valueOf(artistId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Album.ALBUM_NAME,Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_dual,
                cursor,
                new String[] {Artist.ARTIST_NAME, Album.ALBUM_NAME},
                new int[] {R.id.line_one, R.id.line_two},
                0);

        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
