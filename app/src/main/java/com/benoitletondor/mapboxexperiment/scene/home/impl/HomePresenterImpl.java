package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;
import com.benoitletondor.mapboxexperiment.common.map.CameraCenterLocation;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.OnCameraMoveListener;
import com.benoitletondor.mapboxexperiment.common.map.OnMapClickListener;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

public final class HomePresenterImpl extends BaseMapPresenterImpl<HomeView> implements HomePresenter, OnMapClickListener, OnCameraMoveListener
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
    /**
     * The state of user adding a location using the FAB
     */
    @NonNull
    private AddLocationState mAddLocationState = AddLocationState.NORMAL;

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

        if( viewCreated )
        {
            switch (mAddLocationState)
            {
                case NORMAL:
                    // Nothing to do here since the view is already init in normal state
                    break;
                case ADDING_LOCATION:
                    setViewStateAddingLocation();
                    break;
            }
        }
    }

    @Override
    public void onStop()
    {
        if( mMap != null )
        {
            mMap.clear();
            mMap.setOnMapClickedListener(null);
            mMap.setOnCameraMoveListener(null);
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
        mMap.setOnMapClickedListener(this);
        mMap.setOnCameraMoveListener(this);
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

    @Override
    public void onLocationSearchEntered(@NonNull AutoCompleteLocationItem item)
    {
        if( mMap != null )
        {
            mMap.addMarker(item.getLatitude(), item.getLongitude(), item.getLocationName(), null); // TODO save
            mMap.moveCamera(item.getLatitude(), item.getLongitude(), 14f, true);

            if( mView != null )
            {
                mView.clearSearchBarContent();
                mView.hideKeyboard();
                mView.clearSearchBarFocus();
            }
        }
    }

    @Override
    public void onAddLocationFABClicked()
    {
        if( mMap == null )
        {
            return;
        }

        switch (mAddLocationState)
        {
            case NORMAL:
                mAddLocationState = AddLocationState.ADDING_LOCATION;
                setViewStateAddingLocation();
                reverseGeocodingForLocation(mMap.getCameraCenterLocation());
                break;
            case ADDING_LOCATION:
                final CameraCenterLocation centerLocation = mMap.getCameraCenterLocation();
                mMap.addMarker(centerLocation.getLatitude(), centerLocation.getLongitude(), null, null); // TODO add name and save

                mAddLocationState = AddLocationState.NORMAL;
                setViewStateNormal();
                break;
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if( mAddLocationState == AddLocationState.ADDING_LOCATION && mView != null )
        {
            mAddLocationState = AddLocationState.NORMAL;
            setViewStateNormal();
            return true;
        }

        return false;
    }

    private void reverseGeocodingForLocation(@NonNull CameraCenterLocation cameraCenterLocation)
    {
        if( mView == null )
        {
            return;
        }

        mView.clearSearchBarContent();
        mView.setSearchBarSearchingHint();
    }

    private void setViewStateNormal()
    {
        if( mView != null )
        {
            mView.enableSearchBar();
            mView.setAddLocationFABAddLocationIcon();
            mView.hideCenterMapMarker();
            mView.setSearchBarDefaultHint();
            mView.clearSearchBarContent();
            mView.setDefaultViewTitle();
        }
    }

    private void setViewStateAddingLocation()
    {
        if( mView != null )
        {
            mView.disableSearchBar();
            mView.setAddLocationFABValidateIcon();
            mView.showCenterMapMarker();
            mView.setAddLocationViewTitle();
            mView.clearSearchBarContent();
        }
    }

// ------------------------------------->

    @Override
    public void onMapClicked(double latitude, double longitude)
    {
        if( mView != null )
        {
            mView.clearSearchBarFocus();
        }
    }

    @Override
    public void onMapCameraMove(@NonNull CameraCenterLocation newCameraCenterLocation)
    {
        if( mAddLocationState == AddLocationState.ADDING_LOCATION )
        {
            reverseGeocodingForLocation(newCameraCenterLocation);
        }
    }

// ------------------------------------->

    private enum AddLocationState
    {
        /**
         * Normal state, user is not adding a location
         */
        NORMAL,

        /**
         * User is currently adding a location
         */
        ADDING_LOCATION
    }
}
