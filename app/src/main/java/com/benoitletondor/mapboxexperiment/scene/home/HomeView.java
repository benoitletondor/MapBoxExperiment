package com.benoitletondor.mapboxexperiment.scene.home;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

@UiThread
public interface HomeView extends BaseMapView
{

    /**
     * Show a message to the user indicating an error occurred while getting device location
     *
     * @param errorDescription optional description of the error
     */
    void showLocationNotAvailable(@Nullable String errorDescription);

    /**
     * Show a message to the user after he denied the location permission
     */
    void showLocationPermissionDeniedDisclaimer();

    /**
     * Show a message to the user indicating an error occurred while loading the map
     *
     * @param message the error message
     */
    void showMapLoadingError(@Nullable String message);
}
