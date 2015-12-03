package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.RepeatResponse;
import com.kelsos.mbrc.dto.player.PlayState;
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
  Observable<Volume> getVolume();

  @PUT("/player/volume")
  Single<Volume> updateVolume(@Body VolumeRequest body);

  @PUT("/player/shuffle")
  Single<Shuffle> updateShuffleState(@Body ShuffleRequest body);

  @PUT("/player/scrobble")
  Single<BaseResponse> updateScrobbleState(@Body ChangeStateRequest body);

  @PUT("/player/repeat")
  Observable<RepeatResponse> updateRepeatState(@Body RepeatRequest body);

  @PUT("/player/mute")
  Observable<StatusResponse> updateMuteState(@Body ChangeStateRequest body);

  @GET("/player/shuffle")
  Single<Shuffle> getShuffleState();

  @GET("/player/scrobble")
  Single<StatusResponse> getScrobbleState();

  @GET("/player/repeat") Observable<Repeat> getRepeatMode();

  @GET("/player/playstate") Observable<PlayState> getPlayState();

  @GET("/player/status")
  Single<PlayerStatusResponse> getPlayerStatus();

  @GET("/player/mute")
  Observable<StatusResponse> getMuteState();


  @GET("/player/action")
  Single<BaseResponse> performPlayerAction(@Query("action") @PlayerAction.Action String action);

}
