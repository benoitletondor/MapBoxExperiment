package com.benoitletondor.mapboxexperiment.scene.history.injection;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryPresenter;
import com.benoitletondor.mapboxexperiment.scene.history.impl.HistoryPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class HistoryViewModule
{
    @Provides
    public PresenterFactory<HistoryPresenter> providePresenterFactory(@NonNull final MarkerStorageInteractor markerStorageInteractor)
    {
        return new PresenterFactory<HistoryPresenter>()
        {
            @NonNull
            @Override
            public HistoryPresenter create()
            {
                return new HistoryPresenterImpl(markerStorageInteractor);
            }
        };
    }
}
