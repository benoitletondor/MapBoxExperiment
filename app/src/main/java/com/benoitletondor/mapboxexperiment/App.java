package com.benoitletondor.mapboxexperiment;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.injection.AppModule;
import com.benoitletondor.mapboxexperiment.injection.DaggerAppComponent;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Entry point of the application
 *
 * @author Benoit LETONDOR
 */
public final class App extends MultiDexApplication
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

    /**
     * Return the dagger app component
     *
     * @return the app component
     */
    @NonNull
    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }
}
