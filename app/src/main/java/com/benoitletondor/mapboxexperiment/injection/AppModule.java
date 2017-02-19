package com.benoitletondor.mapboxexperiment.injection;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.App;
import com.benoitletondor.mapboxexperiment.common.map.ReverseGeocoder;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;
import com.benoitletondor.mapboxexperiment.interactor.impl.ReverseGeocodingInteractorImpl;
import com.benoitletondor.mapboxexperiment.mapbox.MapboxReverseGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule
{
    @NonNull
    private final App mApp;

    public AppModule(@NonNull App app)
    {
        mApp = app;
    }

    @Provides
    public Context provideAppContext()
    {
        return mApp;
    }

    @Provides
    public App provideApp()
    {
        return mApp;
    }

    @Provides
    public ReverseGeocoder reverseGeocoder()
    {
        final AndroidGeocoder geocoder = new AndroidGeocoder(mApp, Locale.getDefault());
        geocoder.setAccessToken(MapboxAccountManager.getInstance().getAccessToken());

        return new MapboxReverseGeocoder(geocoder);
    }

    @Singleton
    @Provides
    public ReverseGeocodingInteractor provideReverseGeocoderInteractor(@NonNull ReverseGeocoder reverseGeocoder)
    {
        return new ReverseGeocodingInteractorImpl(reverseGeocoder);
    }
}