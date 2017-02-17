package com.benoitletondor.mapboxexperiment.injection;

import android.content.Context;

import com.benoitletondor.mapboxexperiment.App;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent
{
    Context getAppContext();

    App getApp();
}