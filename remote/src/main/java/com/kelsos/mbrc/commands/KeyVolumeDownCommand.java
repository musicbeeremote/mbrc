package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.squareup.otto.Bus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KeyVolumeDownCommand implements ICommand {
    public static final int LOWEST_NOT_ZERO = 10;
    public static final int STEP = 10;
    public static final int MOD = 10;
    public static final int BASE = 0;
    public static final int LIMIT = 5;

    @Inject
    private RemoteApi api;
    private Model model;
    private Bus bus;

    @Inject public KeyVolumeDownCommand(Model model, Bus bus) {
        this.model = model;
        this.bus = bus;
    }

    @Override public void execute(IEvent e) {
        if (model.getVolume() >= LOWEST_NOT_ZERO) {
            int mod = model.getVolume() % MOD;
            int volume;

            if (mod == BASE) {
                volume = model.getVolume() - STEP;
            } else if (mod < LIMIT) {
                volume = model.getVolume() - (STEP + mod);
            } else {
                volume = model.getVolume() - mod;
            }

            api.updateVolume(volume, new Callback<SuccessResponse>() {
                @Override
                public void success(SuccessResponse successResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }
}
