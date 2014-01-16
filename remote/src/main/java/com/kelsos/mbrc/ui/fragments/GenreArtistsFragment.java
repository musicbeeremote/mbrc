package com.kelsos.mbrc.ui.fragments;

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
import com.kelsos.mbrc.data.Artist;
import com.kelsos.mbrc.ui.base.BaseListFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenreArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenreArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GenreArtistsFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String GENRE_ID = "genreId";
    private static final int URL_LOADER = 0x15;

    // TODO: Rename and change types of parameters
    private long genreId;

    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param genreId The id of the genre.
     * @return A new instance of fragment GenreArtistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenreArtistsFragment newInstance(long genreId) {
        GenreArtistsFragment fragment = new GenreArtistsFragment();
        Bundle args = new Bundle();
        args.putLong(GENRE_ID, genreId);
        fragment.setArguments(args);
        return fragment;
    }
    public GenreArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genreId = getArguments().getLong(GENRE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genre_artists, container, false);
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
        baseUri = Uri.withAppendedPath(Artist.CONTENT_GENRE_URI, Uri.encode(String.valueOf(genreId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_single,
                cursor,
                new String[] {Artist.ARTIST_NAME},
                new int[] {R.id.line_one},
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
