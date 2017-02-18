package com.benoitletondor.mapboxexperiment.common.mvp.presenter;

import android.location.Location;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapLoadingCallback;
import com.google.android.gms.location.LocationRequest;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

/**
 * Interface for the map presenter that defines methods. You shouldn't directly implement but extend
 * {@link com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl}.
 *
 * @author Benoit LETONDOR
 */
public interface BaseMapPresenter<V extends BaseMapView> extends BasePresenter<V>, MapLoadingCallback
{
    /**
     * Called when the map is set-up and ready to be filled with data
     *
     * @param map the ready to be used map
     */
    void onMapAvailable(@NonNull MapApi map);

    /**
     * Called when the map is not available due to an error
     *
     * @param error an exception containing the encountered
     */
    void onMapNotAvailable(@NonNull Exception error);

    /**
     * You should provide your location request here
     *
     * @return the location request used by the map
     */
    @NonNull
    LocationRequest getLocationRequest();

    /**
     * Called when the user location change
     *
     * @param location the new user location
     */
    void onUserLocationChanged(@NonNull Location location);

// ---------------------------------------->

    /**
     * Method used by the {@link BaseMapView} to send the permission result. There is no need to
     * override this method since {@link com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl} already does.
     */
    void onLocationPermissionGranted();

    /**
     * Method used by the {@link BaseMapView} to send the permission result. There is no need to
     * override this method since {@link com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl} already does.
     */
    void onLocationPermissionDenied();
}
