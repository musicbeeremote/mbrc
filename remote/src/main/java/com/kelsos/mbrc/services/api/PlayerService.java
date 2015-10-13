package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.Volume;
import com.kelsos.mbrc.rest.requests.ChangeStateRequest;
import com.kelsos.mbrc.rest.requests.RepeatRequest;
import com.kelsos.mbrc.rest.requests.ShuffleRequest;
import com.kelsos.mbrc.rest.requests.VolumeRequest;
import com.kelsos.mbrc.rest.responses.PlayerStatusResponse;
import com.kelsos.mbrc.rest.responses.SuccessBooleanStateResponse;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.rest.responses.SuccessVolumeResponse;
import com.kelsos.mbrc.rest.responses.TextValueResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Single;

public interface PlayerService {
  @GET("/player/volume")
  Single<Volume> getVolume();

  @PUT("/player/volume")
  Single<SuccessVolumeResponse> updateVolume(@Body VolumeRequest body);

  @PUT("/player/shuffle")
  Single<Shuffle> updateShuffleState(@Body ShuffleRequest body);

  @PUT("/player/scrobble")
  Single<SuccessResponse> updateScrobbleState(@Body ChangeStateRequest body);

  @PUT("/player/repeat")
  Single<SuccessResponse> updateRepeatState(@Body RepeatRequest body);

  @PUT("/player/mute")
  Single<SuccessResponse> updateMuteState(@Body ChangeStateRequest body);

  @GET("/player/shuffle")
  Single<Shuffle> getShuffleState();

  @GET("/player/scrobble")
  Single<SuccessBooleanStateResponse> getScrobbleState();

  @GET("/player/repeat")
  Single<TextValueResponse> getRepeatMode();

  @GET("/player/playstate")
  Single<PlaybackState> getPlaystate();

  @GET("/player/status")
  Single<PlayerStatusResponse> getPlayerStatus();

  @GET("/player/mute")
  Single<SuccessBooleanStateResponse> getMuteState();


  @GET("/player/action")
  Single<SuccessResponse> performPlayerAction(@Query("action") @PlaybackAction String action);

}
