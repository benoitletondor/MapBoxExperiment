package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.benoitletondor.mapboxexperiment.common.LimitedSizeList;
import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;
import com.benoitletondor.mapboxexperiment.common.map.CameraCenterLocation;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.map.OnCameraMoveListener;
import com.benoitletondor.mapboxexperiment.common.map.OnMapClickListener;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BaseMapPresenterImpl;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation of the {@link HomePresenter}
 *
 * @author Benoit LETONDOR
 */
public final class HomePresenterImpl extends BaseMapPresenterImpl<HomeView> implements HomePresenter, OnMapClickListener, OnCameraMoveListener
{
    private static final String TAG = "HomePresenter";

    /**
     * The reverse geocoding interactor, ready to be used
     */
    @NonNull
    private final ReverseGeocodingInteractor mReverseGeocodingInteractor;
    /**
     * The marker storage interactor, ready to be used
     */
    @NonNull
    private final MarkerStorageInteractor mMarkerStorageInteractor;
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
     * List that stores the shown markers
     */
    @NonNull
    private LimitedSizeList<MapMarker> mMarkers = new LimitedSizeList<>(15);
    /**
     * Background action that perform reverse geocoding on a location, will be null if nothing happens at the moment
     */
    @Nullable
    private Disposable mReverseGeocodingAction;
    /**
     * Temp variable that stores the marker to focus next time the view starts
     */
    @Nullable
    private MapMarker mTempMarkerToFocus;

// ------------------------------------->

    public HomePresenterImpl(
        @NonNull ReverseGeocodingInteractor reverseGeocodingInteractor,
        @NonNull MarkerStorageInteractor markerStorageInteractor)
    {
        super(true, reverseGeocodingInteractor, markerStorageInteractor);

        mReverseGeocodingInteractor = reverseGeocodingInteractor;
        mMarkerStorageInteractor = markerStorageInteractor;
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
                    setViewStateNormal();
                    break;
                case ADDING_LOCATION:
                    setViewStateAddingLocation();
                    break;
                case SAVING_LOCATION:
                    setViewStateNormal();
                    mView.showSavingLocationModal();
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

        mMarkers.clear();

        super.onStop();
    }

    @Override
    public void onPresenterDestroyed()
    {
        super.onPresenterDestroyed();
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

        // FIXME implement a better way than reload all data on each view start
        loadMarkers();
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
            addMarker(item.getLatitude(), item.getLongitude(), item.getLocationName(), null);
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
        if( mMap == null || mAddLocationState == AddLocationState.SAVING_LOCATION )
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
                mAddLocationState = AddLocationState.SAVING_LOCATION;
                setViewStateNormal();

                if( mView != null )
                {
                    mView.showSavingLocationModal();
                }

                addSubscription(mReverseGeocodingInteractor.reverseGeocode(centerLocation.getLatitude(), centerLocation.getLongitude())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Address>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Address address) throws Exception
                        {
                            mAddLocationState = AddLocationState.NORMAL;
                            setViewStateNormal();

                            if( mView != null && mMap != null )
                            {
                                addMarker(centerLocation.getLatitude(), centerLocation.getLongitude(), mView.formatAddress(address), null);
                                mView.hideSavingLocationModal();
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                        {
                            mAddLocationState = AddLocationState.NORMAL;
                            setViewStateNormal();

                            Log.e(TAG, "Error while reverse geocoding for saving location", throwable);

                            if( mView != null )
                            {
                                mView.hideSavingLocationModal();
                                mView.showSavingLocationError();
                            }
                        }
                    }));
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

    @Override
    public void onMarkerToFocus(@NonNull MapMarker marker)
    {
        mTempMarkerToFocus = marker;
    }

    /**
     * Start reverse geocoding for the given location. Will cancel any previous reverse geocoding
     * action started.
     *
     * @param cameraCenterLocation the camera location
     */
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

        mReverseGeocodingAction = addSubscription(mReverseGeocodingInteractor
            .reverseGeocode(cameraCenterLocation.getLatitude(), cameraCenterLocation.getLongitude())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Address>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Address address) throws Exception
                {
                    mReverseGeocodingAction = null;

                    if( mView != null && mAddLocationState == AddLocationState.ADDING_LOCATION )
                    {
                        mView.setSearchBarContent(mView.formatAddress(address));
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    mReverseGeocodingAction = null;
                    Log.e(TAG, "Error while reverse geocoding", throwable);

                    // TODO
                }
            }));
    }

    /**
     * Update the view for the normal state. Will do nothing is view is null
     */
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
            mView.disableSearchBarMultilineDisplay();
        }
    }

    /**
     * Update the view for the adding location state. Will do nothing is view is null
     */
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
            mView.enableSearchBarMultilineDisplay();
        }
    }

    /**
     * Load markers from storage and put them into {@link #mMarkers} list asynchronously. This methods also
     * handle focus on {@link #mTempMarkerToFocus}.
     *
     * FIXME concurrency is not handled properly here: what happens if the map gets re-created while loading
     */
    private void loadMarkers()
    {
        addSubscription(mMarkerStorageInteractor.retrieveStoredMarkers()
            .subscribeOn(Schedulers.computation())
            .map(new Function<List<MarkerStorageInteractor.StoredMarker>, List<MapMarker>>()
            {
                @Override
                public List<MapMarker> apply(@io.reactivex.annotations.NonNull List<MarkerStorageInteractor.StoredMarker> markers) throws Exception
                {
                    if( mMap == null )
                    {
                        throw new Exception("Map is null");
                    }

                    for( MarkerStorageInteractor.StoredMarker marker : markers )
                    {
                        mMarkers.addWithLimit(
                            mMap.addMarker(marker.getLatitude(), marker.getLongitude(), marker.getName(), marker.getCaption()));
                    }

                    return mMarkers;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Function<List<MapMarker>, List<MapMarker>>()
            {
                @Override
                public List<MapMarker> apply(@io.reactivex.annotations.NonNull List<MapMarker> markers) throws Exception
                {
                    if( mTempMarkerToFocus != null && mMap != null )
                    {
                        for(MapMarker marker : markers)
                        {
                            // TODO this is lame, we need to find a better way to ensure the marker is the one we want
                            if( marker.getLatitude() == mTempMarkerToFocus.getLatitude() &&
                                marker.getLongitude() == mTempMarkerToFocus.getLongitude() )
                            {
                                mMap.moveCamera(marker.getLatitude(), marker.getLongitude(), 15f, false);
                                mMap.selectMarker(marker);
                            }
                        }

                        mTempMarkerToFocus = null;
                    }

                    return markers;
                }
            })
            .ignoreElements()
            .subscribe(new Action()
            {
                @Override
                public void run() throws Exception
                {
                    Log.d(TAG, "Markers load");
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    Log.e(TAG, "Error loading markers", throwable);
                }
            }));
    }

    /**
     * Add a marker to the map, handle list limit and asynchronously save the marker list
     *
     * @param latitude latitude of the marker to add
     * @param longitude longitude of the marker to add
     * @param name name of the marker to add
     * @param caption caption of the marker to add
     */
    private void addMarker(double latitude, double longitude, @Nullable String name, @Nullable String caption)
    {
        if( mMap == null )
        {
            return;
        }

        final MapMarker removed = mMarkers.addWithLimit(
            mMap.addMarker(latitude, longitude, name, caption));

        if( removed != null )
        {
            mMap.removeMarker(removed);
        }

        saveMarkers();
    }

    /**
     * Save the markers to storage asynchronously
     */
    private void saveMarkers()
    {
        addSubscription(mMarkerStorageInteractor.storeMarkers(new ArrayList<>(mMarkers))
            .subscribeOn(Schedulers.computation())
            .subscribe(new Action()
            {
                @Override
                public void run() throws Exception
                {
                    Log.d(TAG, "Marker saved");
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    Log.e(TAG, "Error while saving markers", throwable);
                }
            }));
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
        ADDING_LOCATION,

        /**
         * Location added by user is currently being saved (waiting for reverse geocoder)
         */
        SAVING_LOCATION
    }
}
