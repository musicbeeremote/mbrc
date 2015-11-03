package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.PlayerStatusResponse;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.StatusResponse;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.requests.ChangeStateRequest;
import com.kelsos.mbrc.dto.requests.RepeatRequest;
import com.kelsos.mbrc.dto.requests.ShuffleRequest;
import com.kelsos.mbrc.dto.requests.VolumeRequest;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;
import rx.Single;

public interface PlayerService {
  @GET("/player/volume")
  Single<Volume> getVolume();

  @PUT("/player/volume")
  Single<Volume> updateVolume(@Body VolumeRequest body);

  @PUT("/player/shuffle")
  Single<Shuffle> updateShuffleState(@Body ShuffleRequest body);

  @PUT("/player/scrobble")
  Single<BaseResponse> updateScrobbleState(@Body ChangeStateRequest body);

  @PUT("/player/repeat")
  Single<BaseResponse> updateRepeatState(@Body RepeatRequest body);

  @PUT("/player/mute")
  Single<BaseResponse> updateMuteState(@Body ChangeStateRequest body);

  @GET("/player/shuffle")
  Single<Shuffle> getShuffleState();

  @GET("/player/scrobble")
  Single<StatusResponse> getScrobbleState();

  @GET("/player/repeat") Observable<Repeat> getRepeatMode();

  @GET("/player/playstate")
  Single<PlaybackState> getPlaystate();

  @GET("/player/status")
  Single<PlayerStatusResponse> getPlayerStatus();

  @GET("/player/mute")
  Single<StatusResponse> getMuteState();


  @GET("/player/action")
  Single<BaseResponse> performPlayerAction(@Query("action") @PlayerAction.Action String action);

}
