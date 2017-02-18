package com.benoitletondor.mapboxexperiment;

import android.app.Application;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.injection.AppModule;
import com.benoitletondor.mapboxexperiment.injection.DaggerAppComponent;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.squareup.leakcanary.LeakCanary;

public final class App extends Application
{
    /**
     * The app dagger component
     */
    private AppComponent mAppComponent;

// ---------------------------------->

    @Override
    public void onCreate()
    {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .build();

        LeakCanary.install(this);

        MapboxAccountManager.start(this, BuildConfig.MAPBOX_API_KEY);
    }

// ---------------------------------->

    @NonNull
    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }
}
