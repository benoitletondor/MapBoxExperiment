package com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapLocationSource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BaseMapPresenter;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;

/**
 * Implementation of the {@link BaseMapPresenter} that you should extends to provide a map view. If
 * you create it asking for user location, it will take care of the permission request.
 *
 * @author Benoit LETONDOR
 */
public abstract class BaseMapPresenterImpl<V extends BaseMapView> extends BasePresenterImpl<V> implements BaseMapPresenter<V>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MapLocationSource
{
    private final static String TAG = BaseMapPresenterImpl.class.getName();

    /**
     * Does this view needs geolocation of the user
     */
    private final boolean mNeedGeoloc;
    /**
     * Has the geolocation permission been denied by the user
     */
    private boolean mGeolocPermissionDenied = false;
    /**
     * Play services client
     */
    @Nullable
    @VisibleForTesting
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Current presenter state
     */
    @NonNull
    private State mState = State.CREATED;
    /**
     * Location listener gave by the map to send user location update
     */
    @Nullable
    private MapLocationSource.OnLocationChangedListener mLocationChangeListener;

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
                initPlayServices();
                break;
            case WAITING_FOR_GPS:
                // Nothing to do since GPS are still loading
                break;
            case GPS_ERROR:
                // Retry!
                initPlayServices();
                break;
            case GPS_READY:
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

    @Override
    public void onStop()
    {
        // Stop asking for user location when view moves in background
        mLocationChangeListener = null;

        try
        {
            if( mGoogleApiClient != null )
            {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error while removing location updates onStop", e);
        }

        super.onStop();
    }

    @Override
    public void onPresenterDestroyed()
    {
        // Disconnect for GPS when the presenter gets destroyed
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

        super.onPresenterDestroyed();
    }

    /**
     * Called when the location service is not available
     */
    protected abstract void onLocationNotAvailable(@NonNull ConnectionResult connectionResult);

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        askForLocationIfNeededOrDisplayMap();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        // TODO handle this case properly
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.w(TAG, "onConnectionFailed: "+connectionResult);
        mState = State.GPS_ERROR;
        mGoogleApiClient = null;

        onLocationNotAvailable(connectionResult);

        // Will fail and call onMapNotAvailable callback.
        if( mView != null )
        {
            mView.loadMap();
        }
    }

    /**
     * Init play services if needed and start the map
     */
    @VisibleForTesting
    protected void initPlayServices()
    {
        assert mView != null;

        if (mNeedGeoloc)
        {
            final GoogleApiClient.Builder builder = mView.getAPIBuilder();

            mState = State.WAITING_FOR_GPS;

            builder.addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);

            builder.addApi(LocationServices.API);

            mGoogleApiClient = builder.build();
            mGoogleApiClient.connect();
        }
        else
        {
            mState = State.GPS_READY;
            askForLocationIfNeededOrDisplayMap();
        }
    }

    /**
     * Called when GPS are ready or when there are not needed (since no location needed). This
     * method takes care of requesting the permission if needed or loading the map.
     */
    private void askForLocationIfNeededOrDisplayMap()
    {
        if (mNeedGeoloc && mGoogleApiClient != null)
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

    @Override
    public void onErrorLoadingMap(@NonNull Exception error)
    {
        onMapNotAvailable(error);
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

        if (mNeedGeoloc && !mGeolocPermissionDenied && mGoogleApiClient != null)
        {
            map.setUserLocationEnabledWithSource(this);
        }

        onMapAvailable(map);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if( mLocationChangeListener != null )
        {
            mLocationChangeListener.onLocationChanged(location);
        }

        onUserLocationChanged(location);
    }

    @Override
    public void activate(@NonNull OnLocationChangedListener listener)
    {
        mLocationChangeListener = listener;

        Log.d(TAG, "activate OnLocationChangedListener");

        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), this);
        }
        catch (SecurityException e)
        {
            // Should never happen
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception while activating location updates", e);
        }
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
         * Presenter is waiting for Google Play Services to set-up
         */
        WAITING_FOR_GPS,
        /**
         * An error occurred while setting-up Google Play Services
         */
        GPS_ERROR,
        /**
         * Google Play Services are ready
         */
        GPS_READY,
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
