package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;
import com.benoitletondor.mapboxexperiment.common.map.CameraCenterLocation;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.OnCameraMoveListener;
import com.benoitletondor.mapboxexperiment.common.map.OnMapClickListener;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public final class HomePresenterImpl extends BaseMapPresenterImpl<HomeView> implements HomePresenter, OnMapClickListener, OnCameraMoveListener
{
    /**
     * The reverse geocoding interactor, ready to be used
     */
    @NonNull
    private final ReverseGeocodingInteractor mReverseGeocodingInteractor;
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
    /**
     * Background action that perform reverse geocoding on a location, will be null if nothing happens at the moment
     */
    @Nullable
    private Disposable mReverseGeocodingAction;

// ------------------------------------->

    public HomePresenterImpl(@NonNull ReverseGeocodingInteractor reverseGeocodingInteractor)
    {
        super(true, reverseGeocodingInteractor);

        mReverseGeocodingInteractor = reverseGeocodingInteractor;
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

        if( mReverseGeocodingAction != null )
        {
            mReverseGeocodingAction.dispose();
        }

        mView.clearSearchBarContent();

        mReverseGeocodingAction = mReverseGeocodingInteractor
            .reverseGeocode(cameraCenterLocation.getLatitude(), cameraCenterLocation.getLongitude())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Address>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Address address) throws Exception
                {
                    mReverseGeocodingAction = null;

                    if( mView != null )
                    {
                        mView.setSearchBarContent(address.toString());
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    mReverseGeocodingAction = null;
                    // TODO
                }
            });
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
            mView.setSearchBarSearchingHint();
            mView.clearSearchBarContent();
            mView.setAddLocationViewTitle();
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
