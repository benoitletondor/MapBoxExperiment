package com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BaseMapPresenter;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

/**
 * Implementation of the {@link BaseMapPresenter} that you should extends to provide a map view. If
 * you create it asking for user location, it will take care of the permission request.
 *
 * @author Benoit LETONDOR
 */
public abstract class BaseMapPresenterImpl<V extends BaseMapView> extends BasePresenterImpl<V> implements BaseMapPresenter<V>
{
    /**
     * Does this view needs geolocation of the user
     */
    private final boolean mNeedGeoloc;
    /**
     * Has the geolocation permission been denied by the user
     */
    private boolean mGeolocPermissionDenied = false;
    /**
     * Current presenter state
     */
    @NonNull
    private State mState = State.CREATED;

// ------------------------------------------->

    /**
     * Creates a new instance
     *
     * @param needGeolocation does the map needs user location
     */
    protected BaseMapPresenterImpl(boolean needGeolocation, BaseInteractor... interactors)
    {
        super(interactors);

        mNeedGeoloc = needGeolocation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStart(boolean viewCreated)
    {
        super.onStart(viewCreated);
        assert mView != null;

        switch (mState)
        {
            case CREATED:
                askForLocationIfNeededOrDisplayMap();
                break;
            case WAITING_FOR_LOCATION_PERMISSION:
                mView.requestLocationPermission();
                break;
            case LOCATION_PERMISSION_GRANTED:
                mView.loadMap();
                break;
            case LOCATION_PERMISSION_DENIED:
                mView.requestLocationPermission();
                break;
            case WAITING_FOR_MAP:
            case MAP_READY:
            case MAP_AVAILABLE:
                askForLocationIfNeededOrDisplayMap();
                break;
        }
    }

    /**
     * Called when GPS are ready or when there are not needed (since no location needed). This
     * method takes care of requesting the permission if needed or loading the map.
     */
    private void askForLocationIfNeededOrDisplayMap()
    {
        if (mNeedGeoloc)
        {
            mState = State.WAITING_FOR_LOCATION_PERMISSION;
            if( mView != null )
            {
                mView.requestLocationPermission();
            }
        }
        else
        {
            mState = State.WAITING_FOR_MAP;

            if( mView != null )
            {
                mView.loadMap();
            }
        }
    }

    @Override
    public void onLocationPermissionGranted()
    {
        mState = State.LOCATION_PERMISSION_GRANTED;

        mGeolocPermissionDenied = false;
        if( mView != null )
        {
            mView.loadMap();
        }
    }

    @Override
    public void onLocationPermissionDenied()
    {
        mState = State.LOCATION_PERMISSION_DENIED;

        mGeolocPermissionDenied = true;
        if( mView != null )
        {
            mView.loadMap();
        }
    }

    @Override
    public void onMapReady(@NonNull MapApi map)
    {
        if (mState == State.MAP_READY)
        {
            return;
        }

        mState = State.MAP_READY;

        populateMap(map);
    }

    /**
     * Populate the map with the location source if needed and call the {@link #onMapAvailable(MapApi)}
     * callback.
     *
     * @param map the ready map
     */
    private void populateMap(@NonNull MapApi map)
    {
        mState = State.MAP_AVAILABLE;

        if (mNeedGeoloc && !mGeolocPermissionDenied)
        {
            map.setUserLocationEnabled();
        }

        onMapAvailable(map);
    }

    /**
     * States of this presenter
     */
    private enum State
    {
        /**
         * Presenter has just been created and not yet started
         */
        CREATED,
        /**
         * Presenter is waiting for location permission response
         */
        WAITING_FOR_LOCATION_PERMISSION,
        /**
         * Location permission has been granted by the user
         */
        LOCATION_PERMISSION_GRANTED,
        /**
         * Location permission has been denied by the user
         */
        LOCATION_PERMISSION_DENIED,
        /**
         * Presenter is waiting for Google Maps to be ready
         */
        WAITING_FOR_MAP,
        /**
         * Google Maps is ready
         */
        MAP_READY,
        /**
         * Google maps is ready and available for child presenter
         */
        MAP_AVAILABLE
    }
}
