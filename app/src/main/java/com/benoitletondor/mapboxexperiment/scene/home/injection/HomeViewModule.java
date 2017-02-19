package com.benoitletondor.mapboxexperiment.scene.home.injection;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.impl.HomePresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class HomeViewModule
{
    @Provides
    public PresenterFactory<HomePresenter> providePresenterFactory(@NonNull final ReverseGeocodingInteractor reverseGeocodingInteractor)
    {
        return new PresenterFactory<HomePresenter>()
        {
            @NonNull
            @Override
            public HomePresenter create()
            {
                return new HomePresenterImpl(reverseGeocodingInteractor);
            }
        };
    }
}
