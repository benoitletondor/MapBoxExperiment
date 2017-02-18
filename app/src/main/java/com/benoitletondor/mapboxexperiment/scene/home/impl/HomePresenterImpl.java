package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

public final class HomePresenterImpl extends BaseMapPresenterImpl<HomeView> implements HomePresenter
{
    /**
     * The currently displayed map (will be null until loaded and nullified on each view stop to avoid leaks)
     */
    @Nullable
    private MapApi mMap;
    /**
     * The timestamp of the last view start
     */
    private long mLastStartTimestamp;
    /**
     * Has the map been zoomed on user position yet
     */
    private boolean mMapZoomedOnUserPosition = false;

// ------------------------------------->

    public HomePresenterImpl()
    {
        super(true);
    }

    @Override
    public void onStart(boolean viewCreated)
    {
        super.onStart(viewCreated);
        assert mView != null;

        mLastStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onStop()
    {
        if( mMap != null )
        {
            mMap.clear();
            mMap = null;
        }

        super.onStop();
    }

// ------------------------------------->

    @Override
    protected void onLocationNotAvailable(@NonNull ConnectionResult connectionResult)
    {
        if( mView != null )
        {
            mView.showLocationNotAvailable(connectionResult.getErrorMessage());
        }
    }

    @Override
    public void onLocationPermissionDenied()
    {
        super.onLocationPermissionDenied();

        if( mView != null )
        {
            mView.showLocationPermissionDeniedDisclaimer();
        }
    }

    @Override
    public void onMapAvailable(@NonNull MapApi map)
    {
        mMap = map;
    }

    @Override
    public void onMapNotAvailable(@NonNull Exception error)
    {
        if( mView != null )
        {
            mView.showMapLoadingError(error.getMessage());
        }
    }

    @NonNull
    @Override
    public LocationRequest getLocationRequest()
    {
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(60000)
            .setFastestInterval(5000);
    }

    @Override
    public void onUserLocationChanged(@NonNull Location location)
    {
        if( !mMapZoomedOnUserPosition && mMap != null )
        {
            mMapZoomedOnUserPosition = true;
            mMap.moveCamera(location.getLatitude(), location.getLongitude(), 11f, System.currentTimeMillis() - mLastStartTimestamp > 1000);
        }
    }
}
