package com.kelsos.mbrc.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;

import butterknife.ButterKnife;

public class SettingsDialogFragment extends DialogFragment {

  private static final int MAX_PORT = 65535;
  private static final int MIN_PORT = 1;

  private static final String INDEX = "index";
  private static final String PORT = "port";
  private static final String ADDRESS = "address";
  private static final String NAME = "name";

  private EditText hostEdit;
  private EditText nameEdit;
  private EditText portEdit;

  private String currentName;
  private String currentAddress;
  private int currentPort;
  private int currentIndex;

  private SettingsDialogListener mListener;

  public static SettingsDialogFragment newInstance(int index) {
    SettingsDialogFragment fragment = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putInt(INDEX, index);
    fragment.setArguments(args);
    return fragment;
  }

  public static SettingsDialogFragment newInstance(ConnectionSettings settings) {
    SettingsDialogFragment fragment = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putInt(INDEX, settings.getIndex());
    args.putString(NAME, settings.getName());
    args.putString(ADDRESS, settings.getAddress());
    args.putInt(PORT, settings.getPort());
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (SettingsDialogListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement SettingsDialogListener");
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.customView(R.layout.ui_dialog_settings, false);
    builder.title(R.string.settings_dialog_add);
    builder.positiveText(R.string.settings_dialog_add);
    builder.negativeText(android.R.string.cancel);
    builder.onPositive((dialog, which) -> {
      boolean shouldIClose = true;
      String hostname = hostEdit.getText().toString();
      String computerName = nameEdit.getText().toString();

      if (hostname.length() == 0 || computerName.length() == 0) {
        shouldIClose = false;
      }

      String portText = portEdit.getText().toString();

      int portNum = TextUtils.isEmpty(portText) ? 0 : Integer.parseInt(portText);
      if (isValid(portNum) && shouldIClose) {
        ConnectionSettings settings =
            new ConnectionSettings(hostname, computerName, portNum, currentIndex);
        mListener.onDialogPositiveClick(SettingsDialogFragment.this, settings);
        dialog.dismiss();
      }
    });
    builder.onNegative((dialog, which) -> dialog.dismiss());

    final MaterialDialog materialDialog = builder.build();
    final View view = materialDialog.getCustomView();

    if (view != null) {
      hostEdit = ButterKnife.findById(view, R.id.settings_dialog_host);
      nameEdit = ButterKnife.findById(view, R.id.settings_dialog_name);
      portEdit = ButterKnife.findById(view, R.id.settings_dialog_port);
    }

    return materialDialog;
  }

  @Override public void onStart() {
    super.onStart();
    nameEdit.setText(currentName);
    hostEdit.setText(currentAddress);

    if (currentPort > 0) {
      portEdit.setText(String.valueOf(currentPort));
    }
  }

  private boolean isValid(int port) {
    if (port < MIN_PORT || port > MAX_PORT) {
      final MaterialDialog.Builder alert = new MaterialDialog.Builder(getActivity());
      alert.title(R.string.alert_invalid_range);
      alert.content(R.string.alert_invalid_port_number);
      alert.positiveText(android.R.string.ok);
      alert.show();
      return false;
    } else {
      return true;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      currentIndex = args.getInt(INDEX);
      currentPort = args.getInt(PORT);
      currentAddress = args.getString(ADDRESS);
      currentName = args.getString(NAME);
    }
  }

  public interface SettingsDialogListener {
    void onDialogPositiveClick(SettingsDialogFragment dialog, ConnectionSettings settings);
  }
}
