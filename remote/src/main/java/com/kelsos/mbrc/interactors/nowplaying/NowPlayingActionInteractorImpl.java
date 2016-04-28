package com.kelsos.mbrc.interactors.nowplaying;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dto.requests.MoveRequest;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;
import com.kelsos.mbrc.services.api.NowPlayingService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NowPlayingActionInteractorImpl implements NowPlayingActionInteractor {

    @Inject
    private NowPlayingService service;

    @Override
    public Observable<Boolean> play(String path) {
        PlayPathRequest request = new PlayPathRequest();
        request.setPath(path);
        return service.nowPlayingPlayTrack(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS));
    }

    @Override
    public Observable<Boolean> remove(int position) {
        return service.nowPlayingRemoveTrack(position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS));
    }

    @Override
    public Observable<Boolean> move(int from, int to) {
        MoveRequest request = new MoveRequest();
        request.setFrom(from);
        request.setTo(to);

        return service.nowPlayingMoveTrack(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS));
    }
}
