package com.kelsos.mbrc.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.DeviceSettings;

public class SettingsDialogFragment extends DialogFragment {

  public static final String TAG = "settings_dialog";

  public static final int MAX_PORT = 65535;
  public static final int MIN_PORT = 1;

  public static final String ID = "index";
  public static final String PORT = "port";
  public static final String ADDRESS = "address";
  public static final String NAME = "name";
  public static final String HTTP = "http";

  private EditText hostEdit;
  private EditText nameEdit;
  private EditText portEdit;
  private EditText httpEdit;

  private String currentName;
  private String currentAddress;
  private int currentPort;
  private int currentIndex;
  private int currentHttpPort;

  private SettingsDialogListener mListener;

  public static SettingsDialogFragment newInstance(int index) {
    SettingsDialogFragment fragment = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putInt(ID, index);
    fragment.setArguments(args);
    return fragment;
  }

  public static SettingsDialogFragment newInstance(DeviceSettings settings) {
    SettingsDialogFragment fragment = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putLong(ID, settings.getId());
    args.putString(NAME, settings.getName());
    args.putString(ADDRESS, settings.getAddress());
    args.putInt(PORT, settings.getPort());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mListener = (SettingsDialogListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");
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
      String httpText = httpEdit.getText().toString();

      int portNum = TextUtils.isEmpty(portText) ? 0 : Integer.parseInt(portText);
      int httpNum = TextUtils.isEmpty(httpText) ? 0 : Integer.parseInt(httpText);

      if (isValid(portNum) && isValid(httpNum) && shouldIClose) {

        DeviceSettings settings = new DeviceSettings();
        settings.setAddress(hostname);
        settings.setName(computerName);
        settings.setPort(portNum);
        settings.setHttp(httpNum);

        mListener.onDialogPositiveClick(SettingsDialogFragment.this, settings);
        dialog.dismiss();
      }
    });

    final MaterialDialog materialDialog = builder.build();
    final View view = materialDialog.getCustomView();
    hostEdit = (EditText) view.findViewById(R.id.settings_dialog_host);
    nameEdit = (EditText) view.findViewById(R.id.settings_dialog_name);
    portEdit = (EditText) view.findViewById(R.id.settings_dialog_port);

    httpEdit = (EditText) view.findViewById(R.id.settings_dialog_http);
    return materialDialog;
  }

  @Override public void onStart() {
    super.onStart();
    nameEdit.setText(currentName);
    hostEdit.setText(currentAddress);

    if (currentHttpPort > 0) {
      httpEdit.setText(String.format("%d", currentHttpPort));
    }
    if (currentPort > 0) {
      portEdit.setText(String.format("%d", currentPort));
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
      currentIndex = args.getInt(ID);
      currentPort = args.getInt(PORT);
      currentAddress = args.getString(ADDRESS);
      currentName = args.getString(NAME);
      currentHttpPort = args.getInt(HTTP);
    }
  }

  public interface SettingsDialogListener {
    void onDialogPositiveClick(SettingsDialogFragment dialog, DeviceSettings settings);
  }
}
