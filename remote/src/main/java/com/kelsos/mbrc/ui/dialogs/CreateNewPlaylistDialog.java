package com.kelsos.mbrc.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.kelsos.mbrc.R;

public class CreateNewPlaylistDialog extends DialogFragment {

    private EditText mPlaylistNameText;

    private onPlaylistNameSelectedListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.playlist_create, null);
        ((TextView)view.findViewById(R.id.dialog_title)).setText(getString(R.string.playlist_dialog_new_title));
        mPlaylistNameText = ((EditText)view.findViewById(R.id.playlist_name_text));
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setView(view);
        mBuilder.setTitle(null);
        mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = mPlaylistNameText.getText().toString();
                if (mListener != null) {
                    mListener.onPlaylistNameSelected(name);
                }
                dialog.dismiss();
            }
        });
        return mBuilder.create();
    }

    public void setOnPlaylistNameSelectedListener(onPlaylistNameSelectedListener mListener) {
        this.mListener = mListener;
    }

    /**
     * Interface that must be implemented by the fragment or activity
     * hosting the dialog
     */
    public interface onPlaylistNameSelectedListener {
        /**
         * Called when the user presses the ok button of the dialog.
         * @param name The name of the playlist
         */
        void onPlaylistNameSelected(String name);
    }
}
