package com.benoitletondor.mapboxexperiment.injection;

import android.content.Context;

import com.benoitletondor.mapboxexperiment.App;
import com.benoitletondor.mapboxexperiment.common.map.ReverseGeocoder;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent
{
    Context getAppContext();

    App getApp();

    ReverseGeocodingInteractor getReverseGeocodingInteractor();

    MarkerStorageInteractor getMarkerStorageInteractor();
}