package com.benoitletondor.mapboxexperiment.common.mvp.view;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapViewFragment;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Base interface for a view displaying a map. Your view interface should extend this one and your view
 * implementation should extends {@link com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseMapFragment}
 *
 * @author Benoit LETONDOR
 */
@UiThread
public interface BaseMapView
{
    /**
     * Creates a builder for Google API Client using the view context
     *
     * @return a builder, ready to be used by the presenter
     */
    @NonNull
    GoogleApiClient.Builder getAPIBuilder();

    /**
     * Display the map fragment and load the map. It should call
     * {@link com.benoitletondor.mapboxexperiment.common.mvp.presenter.BaseMapPresenter#onMapReady(MapApi)} on success.
     */
    void loadMap();

    /**
     * Request the location permission and forward the result to the {@link com.benoitletondor.mapboxexperiment.common.mvp.presenter.BaseMapPresenter}.
     */
    void requestLocationPermission();

    /**
     * Creates a new {@link MapViewFragment}
     *
     * @return a newly created map fragement
     */
    @NonNull
    MapViewFragment createMapView();
}
