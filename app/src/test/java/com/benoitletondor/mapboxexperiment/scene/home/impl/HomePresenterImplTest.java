package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.RxSchedulersOverrideRule;
import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;
import com.benoitletondor.mapboxexperiment.common.map.CameraCenterLocation;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.map.OnCameraMoveListener;
import com.benoitletondor.mapboxexperiment.common.map.OnMapClickListener;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link HomePresenterImpl} and {@link HomeView}
 *
 * @author Benoit LETONDOR
 */
@RunWith(MockitoJUnitRunner.class)
public final class HomePresenterImplTest
{
    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @NonNull
    private static final List<MarkerStorageInteractor.StoredMarker> markers;
    static
    {
        markers = new ArrayList<>();

        for(int i=0; i<10; i++)
        {
            markers.add(new MarkerStorageInteractor.StoredMarker(i, i, "name" + i, "caption" + i));
        }
    }

    @Mock
    MarkerStorageInteractor mMarkerStorageInteractorMock;
    @Mock
    ReverseGeocodingInteractor mReverseGeocodingInteractorMock;
    @Mock
    MapApi mMapApiMock;

// --------------------------------->

    @Test
    public void testStartTimestamp() throws InterruptedException
    {
        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter, false);

        // Ensure it's 0 when not init
        assertEquals(0, presenter.mLastStartTimestamp);

        presenter.onStart(true);

        // Ensure it's updated on first start
        final long currentTs = presenter.mLastStartTimestamp;
        assertTrue(presenter.mLastStartTimestamp > 0);

        // Wait a bit
        Thread.sleep(5);

        presenter.onStop();
        presenter.onStart(false);

        // Ensure it's updated on following starts
        assertTrue(presenter.mLastStartTimestamp > currentTs);
    }

    @Test
    public void ensureMapIsClearedWhenPresenterStops()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Check that the map listener are all set correctly
        Mockito.verify(mMapApiMock).setOnCameraMoveListener(Mockito.eq(presenter));
        Mockito.verify(mMapApiMock).setOnMapClickedListener(Mockito.eq(presenter));

        // Stop the presenter
        presenter.onStop();

        // Check that map listener are all unset
        Mockito.verify(mMapApiMock).setOnCameraMoveListener((OnCameraMoveListener) Mockito.isNull());
        Mockito.verify(mMapApiMock).setOnMapClickedListener((OnMapClickListener) Mockito.isNull());

        // Check that map is not kept
        assertNull(presenter.mMap);
    }

// --------------------------------->

    @Test
    public void testMapNotAvailableTriggersAlert()
    {
        HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        giveLocationPermission(presenter);

        // Ensure that presenter asked for map
        Mockito.verify(view).loadMap();

        // Callback with an error
        presenter.onErrorLoadingMap(new Exception("stub"));

        // Ensure error is shown to user
        Mockito.verify(view).showMapLoadingError("stub");
    }

    @Test
    public void testLocationNotAvailableTriggersAlert()
    {
        HomePresenterImpl presenter = createPresenter(false);
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Ensure that error is shown to user
        Mockito.verify(view).showLocationNotAvailable(Mockito.nullable(String.class));
    }

    @Test
    public void testDenyingLocationPermissionTriggersAlert()
    {
        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Ensure permission is asked
        Mockito.verify(view).requestLocationPermission();

        // Respond negatively
        presenter.onLocationPermissionDenied();

        // Ensure that error is shown to user
        Mockito.verify(view).showLocationPermissionDeniedDisclaimer();
    }

    @Test
    public void testAllSetupGoingOkDoesntTriggerAnyAlert()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Ensure nothing is shown to the user
        Mockito.verify(view, Mockito.never()).showMapLoadingError(Mockito.nullable(String.class));
        Mockito.verify(view, Mockito.never()).showLocationNotAvailable(Mockito.nullable(String.class));
        Mockito.verify(view, Mockito.never()).showLocationPermissionDeniedDisclaimer();
    }

// --------------------------------->

    @Test
    public void testMapIsZoomedOnUserPositionWhenAvailableOnlyTheFirstTime()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Send first location
        final Location location = mockLocation(10.0d, 10.0d);
        presenter.onUserLocationChanged(location);

        // Ensure map is updated
        Mockito.verify(mMapApiMock).moveCamera(Mockito.eq(location.getLatitude()), Mockito.eq(location.getLongitude()), Mockito.anyDouble(), Mockito.eq(false));


        // Send another location
        final Location secondLocation = mockLocation(20.0d, 20.0d);
        presenter.onUserLocationChanged(secondLocation);

        // Ensure map isn't updated this time
        Mockito.verify(mMapApiMock, Mockito.times(1)).moveCamera(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean());
    }

    @Test
    public void testMapIsZoomedOnUserPositionAnimatedWhenLocationIsLate() throws InterruptedException
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Wait 1second
        Thread.sleep(1001);

        // Send first location
        final Location location = mockLocation(10.0d, 10.0d);
        presenter.onUserLocationChanged(location);

        // Ensure map is updated with animation
        Mockito.verify(mMapApiMock).moveCamera(Mockito.eq(location.getLatitude()), Mockito.eq(location.getLongitude()), Mockito.anyDouble(), Mockito.eq(true));
    }

// --------------------------------->

    @Test
    public void ensureViewIsSetToNormalModeOnStartup()
    {
        HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter, false);

        presenter.mAddLocationState = HomePresenterImpl.AddLocationState.NORMAL;

        presenter.onStart(true);

        // Ensure view state normal is called
        Mockito.verify(presenter).setViewStateNormal();

        // Ensure it calls the right things on the view
        Mockito.verify(view).setAddLocationFABAddLocationIcon();
        Mockito.verify(view).hideCenterMapMarker();
        Mockito.verify(view).setSearchBarDefaultHint();
        Mockito.verify(view).clearSearchBarContent();
        Mockito.verify(view).setDefaultViewTitle();
        Mockito.verify(view).disableSearchBarMultilineDisplay();

        // Ensure other state methods aren't called
        Mockito.verify(view, Mockito.never()).setAddLocationFABValidateIcon();
        Mockito.verify(view, Mockito.never()).showCenterMapMarker();
        Mockito.verify(view, Mockito.never()).setSearchBarSearchingHint();
        Mockito.verify(view, Mockito.never()).setAddLocationViewTitle();
        Mockito.verify(view, Mockito.never()).enableSearchBarMultilineDisplay();
    }

    @Test
    public void ensureViewIsSetToAddingLocationModeOnStartup()
    {
        HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter, false);

        presenter.mAddLocationState = HomePresenterImpl.AddLocationState.ADDING_LOCATION;

        presenter.onStart(true);

        // Ensure view state normal is called
        Mockito.verify(presenter).setViewStateAddingLocation();

        // Ensure it calls the right things on the view
        Mockito.verify(view).setAddLocationFABValidateIcon();
        Mockito.verify(view).showCenterMapMarker();
        Mockito.verify(view).setSearchBarSearchingHint();
        Mockito.verify(view).clearSearchBarContent();
        Mockito.verify(view).setAddLocationViewTitle();
        Mockito.verify(view).enableSearchBarMultilineDisplay();

        // Ensure other state methods aren't called
        Mockito.verify(view, Mockito.never()).setAddLocationFABAddLocationIcon();
        Mockito.verify(view, Mockito.never()).hideCenterMapMarker();
        Mockito.verify(view, Mockito.never()).setSearchBarDefaultHint();
        Mockito.verify(view, Mockito.never()).setDefaultViewTitle();
        Mockito.verify(view, Mockito.never()).disableSearchBarMultilineDisplay();
    }

    @Test
    public void ensureViewIsSetToSavingLocationModeOnStartup()
    {
        HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter, false);

        presenter.mAddLocationState = HomePresenterImpl.AddLocationState.SAVING_LOCATION;

        presenter.onStart(true);

        // Ensure view state normal is called
        Mockito.verify(presenter).setViewStateNormal();

        // Ensure that loading is shown
        Mockito.verify(view).showSavingLocationModal();
    }

    @Test
    public void testStoredMarkersAreDisplayedOnMapOnStartup()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(markers));

        Mockito
            .when(mMapApiMock.addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.nullable(String.class), Mockito.nullable(String.class)))
            .thenAnswer(new Answer<MapMarker>()
            {
                @Override
                public MapMarker answer(InvocationOnMock invocation) throws Throwable
                {
                    return new MarkerStorageInteractor.StoredMarker((double) invocation.getArgument(0),
                        (double) invocation.getArgument(1),
                        (String) invocation.getArgument(2),
                        (String) invocation.getArgument(3));
                }
            });

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Ensure all markers are stored
        assertEquals(10, presenter.mMarkers.size());

        // Ensure all markers are displayed on the map
        Mockito.verify(mMapApiMock, Mockito.times(10)).addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.nullable(String.class), Mockito.nullable(String.class));
    }

// --------------------------------->

    @Test
    public void testSearchPlaceProcess()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        Mockito
            .when(mMarkerStorageInteractorMock.storeMarkers(Mockito.<MapMarker>anyList()))
            .thenReturn(Completable.complete());

        Mockito
            .when(mMapApiMock.addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.nullable(String.class), Mockito.nullable(String.class)))
            .thenAnswer(new Answer<MapMarker>()
            {
                @Override
                public MapMarker answer(InvocationOnMock invocation) throws Throwable
                {
                    return new MarkerStorageInteractor.StoredMarker((double) invocation.getArgument(0),
                        (double) invocation.getArgument(1),
                        (String) invocation.getArgument(2),
                        (String) invocation.getArgument(3));
                }
            });

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Send a search to the presenter
        final AutoCompleteLocationItem search = new AutoCompleteLocationItem()
        {
            @NonNull
            @Override
            public String getLocationName()
            {
                return "A";
            }

            @Override
            public double getLatitude()
            {
                return 12.d;
            }

            @Override
            public double getLongitude()
            {
                return 15.d;
            }
        };
        presenter.onLocationSearchEntered(search);

        // Ensure marker is added to the map and map zoomed to the marker
        Mockito.verify(mMapApiMock).addMarker(Mockito.eq(search.getLatitude()), Mockito.eq(search.getLongitude()), Mockito.eq(search.getLocationName()), (String) Mockito.isNull());
        Mockito.verify(mMapApiMock).moveCamera(Mockito.eq(search.getLatitude()), Mockito.eq(search.getLongitude()), Mockito.anyDouble(), Mockito.eq(true));

        // Ensure markers are saved
        Mockito.verify(mMarkerStorageInteractorMock).storeMarkers(Mockito.<MapMarker>anyList());

        // Ensure view state is put back correctly
        Mockito.verify(view, Mockito.atLeastOnce()).clearSearchBarContent();
        Mockito.verify(view, Mockito.atLeastOnce()).hideKeyboard();
        Mockito.verify(view, Mockito.atLeastOnce()).clearSearchBarFocus();
    }

    @Test
    public void testAddLocationProcess()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        Mockito
            .when(mMarkerStorageInteractorMock.storeMarkers(Mockito.<MapMarker>anyList()))
            .thenReturn(Completable.complete());

        Mockito
            .when(mReverseGeocodingInteractorMock.reverseGeocode(Mockito.anyDouble(), Mockito.anyDouble()))
            .thenReturn(Observable.just(new Address(Locale.getDefault())));

        Mockito
            .when(mMapApiMock.getCameraCenterLocation())
            .thenReturn(new CameraCenterLocation(10.d, 10.d));

        Mockito
            .when(mMapApiMock.addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.nullable(String.class), Mockito.nullable(String.class)))
            .thenAnswer(new Answer<MapMarker>()
            {
                @Override
                public MapMarker answer(InvocationOnMock invocation) throws Throwable
                {
                    return new MarkerStorageInteractor.StoredMarker((double) invocation.getArgument(0),
                        (double) invocation.getArgument(1),
                        (String) invocation.getArgument(2),
                        (String) invocation.getArgument(3));
                }
            });

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        Mockito.when(view.formatAddress(Mockito.any(Address.class))).thenReturn(" ");

        // Everything goes ok
        setAllThingsUp(presenter);

        // Press add location button
        presenter.onAddLocationFABClicked();


        // Ensure state and view is updated, camera center get and reverse geocoding started
        assertEquals(HomePresenterImpl.AddLocationState.ADDING_LOCATION, presenter.mAddLocationState);
        Mockito.verify(presenter).setViewStateAddingLocation();
        Mockito.verify(mMapApiMock).getCameraCenterLocation();
        Mockito.verify(presenter).reverseGeocodingForLocation(Mockito.any(CameraCenterLocation.class));

        // Ensure that moving address is filled to the search box
        Mockito.verify(view).setSearchBarContent(Mockito.anyString());


        // Move the map
        final CameraCenterLocation newCenter = new CameraCenterLocation(15.d, 15.d);
        presenter.onMapCameraMove(newCenter);

        // Ensure reverse geocoding started
        Mockito.verify(presenter).reverseGeocodingForLocation(newCenter);

        // Ensure that moving address is filled to the search box
        Mockito.verify(view, Mockito.times(2)).setSearchBarContent(Mockito.anyString());


        // Finish by clicking again on the FAB
        presenter.onAddLocationFABClicked();

        // Ensure that camera center is get again, state update, view state updated and modal shown
        Mockito.verify(mMapApiMock, Mockito.times(2)).getCameraCenterLocation();
        Mockito.verify(presenter, Mockito.times(3)).setViewStateNormal(); // 3 cause it's called again when reverse geocoding succeed
        Mockito.verify(view).showSavingLocationModal();
        assertEquals(HomePresenterImpl.AddLocationState.NORMAL, presenter.mAddLocationState);

        // Ensure marker is added to the map and map zoomed to the marker
        Mockito.verify(mMapApiMock).addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString(), (String) Mockito.isNull());

        // Ensure markers are saved and modal removed
        Mockito.verify(mMarkerStorageInteractorMock).storeMarkers(Mockito.<MapMarker>anyList());
        Mockito.verify(view).hideSavingLocationModal();
    }

    @Test
    public void testAddLocationGeocodingFail()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        Mockito
            .when(mReverseGeocodingInteractorMock.reverseGeocode(Mockito.anyDouble(), Mockito.anyDouble()))
            .thenReturn(Observable.<Address>error(new Exception("stub")));

        Mockito
            .when(mMapApiMock.getCameraCenterLocation())
            .thenReturn(new CameraCenterLocation(10.d, 10.d));


        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Press add location button
        presenter.onAddLocationFABClicked();

        // Finish by clicking again on the FAB
        presenter.onAddLocationFABClicked();

        // Check that view is back to normal and error displayed
        Mockito.verify(presenter, Mockito.times(3)).setViewStateNormal(); // 3 cause it's called again when reverse geocoding succeed
        Mockito.verify(view).hideSavingLocationModal();
        Mockito.verify(view).showSavingLocationError();
    }

    @Test
    public void testHitBackWhileAddingLocation()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(Collections.<MarkerStorageInteractor.StoredMarker>emptyList()));

        Mockito
            .when(mReverseGeocodingInteractorMock.reverseGeocode(Mockito.anyDouble(), Mockito.anyDouble()))
            .thenReturn(Observable.<Address>error(new Exception("stub")));

        Mockito
            .when(mMapApiMock.getCameraCenterLocation())
            .thenReturn(new CameraCenterLocation(10.d, 10.d));


        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Press add location button
        presenter.onAddLocationFABClicked();

        // Hit back, ensure presenter is shallows call
        assertTrue(presenter.onBackPressed());

        // Ensure state and view
        assertEquals(HomePresenterImpl.AddLocationState.NORMAL, presenter.mAddLocationState);
        Mockito.verify(presenter, Mockito.times(2)).setViewStateNormal();
    }

// --------------------------------->

    @Test
    public void testGoingBackFromHistoryViewByClickingOnAMarkerShowsIt()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(markers));

        Mockito
            .when(mMapApiMock.addMarker(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.nullable(String.class), Mockito.nullable(String.class)))
            .thenAnswer(new Answer<MapMarker>()
            {
                @Override
                public MapMarker answer(InvocationOnMock invocation) throws Throwable
                {
                    return new MarkerStorageInteractor.StoredMarker((double) invocation.getArgument(0),
                        (double) invocation.getArgument(1),
                        (String) invocation.getArgument(2),
                        (String) invocation.getArgument(3));
                }
            });

        final HomePresenterImpl presenter = createPresenter();
        final HomeView view = createAndSpyViewForPresenter(presenter, false);

        final MapMarker marker = markers.get(1);

        // Call marker to focus before start
        presenter.onMarkerToFocus(marker);
        assertEquals(marker, presenter.mTempMarkerToFocus);

        presenter.onStart(true);

        // Everything goes ok
        setAllThingsUp(presenter);

        // Ensure that camera is updated and marker selected
        Mockito.verify(mMapApiMock).moveCamera(Mockito.eq(marker.getLatitude()), Mockito.eq(marker.getLongitude()), Mockito.anyDouble(), Mockito.eq(false));
        Mockito.verify(mMapApiMock).selectMarker(Mockito.any(MapMarker.class));
    }

// --------------------------------->

    /**
     * Create a new location object with the given coordinates
     *
     * @param latitude latitude in degrees
     * @param longitude longitude in degrees
     * @return a new stub location with the given value
     */
    private static Location mockLocation(final double latitude, final double longitude)
    {
        // Simply setting using .setLatitude & .setLongitude doesn't work in tests
        return new Location(""){
            @Override
            public double getLatitude()
            {
                return latitude;
            }

            @Override
            public double getLongitude()
            {
                return longitude;
            }
        };
    }

    /**
     * Stub implmentation of the HomePresenterImpl that bypasses PlayServices setup. Simply set
     * {@code GPSSuccess} to true for successfully connected GPS, and to false for failure
     */
    private static class HomePresenterImplStub extends HomePresenterImpl
    {
        private final boolean mGPSSuccess;

        private HomePresenterImplStub(@NonNull ReverseGeocodingInteractor reverseGeocodingInteractor,
                                     @NonNull MarkerStorageInteractor markerStorageInteractor,
                                     boolean GPSSuccess)
        {
            super(reverseGeocodingInteractor, markerStorageInteractor);

            mGPSSuccess = GPSSuccess;
        }

        @Override
        protected void initPlayServices()
        {
            if( mGPSSuccess )
            {
                mGoogleApiClient = Mockito.mock(GoogleApiClient.class);
                onConnected(null);
            }
            else
            {
                onConnectionFailed(new ConnectionResult(500));
            }
        }
    }

    @NonNull
    private HomePresenterImpl createPresenter(boolean gpsSuccess)
    {
        final HomePresenterImpl presenter = Mockito.spy(new HomePresenterImplStub(mReverseGeocodingInteractorMock, mMarkerStorageInteractorMock, gpsSuccess));
        assertEquals(HomePresenterImpl.AddLocationState.NORMAL, presenter.mAddLocationState);

        return presenter;
    }

    @NonNull
    private HomePresenterImpl createPresenter()
    {
        return createPresenter(true);
    }

    @NonNull
    private HomeView createAndSpyViewForPresenter(@NonNull HomePresenter presenter, boolean start)
    {
        final HomeView view = Mockito.spy(HomeView.class);

        presenter.onViewAttached(view);

        if( start )
        {
            presenter.onStart(true);
        }

        return view;
    }

    @NonNull
    private HomeView createAndSpyViewForPresenter(@NonNull HomePresenter presenter)
    {
        return createAndSpyViewForPresenter(presenter, true);
    }

// ------------------------------------>

    /**
     * Give the location permission to the given presenter
     *
     * @param impl the presenter
     */
    private void giveLocationPermission(@NonNull HomePresenterImpl impl)
    {
        impl.onLocationPermissionGranted();
    }

    /**
     * Give a ready to be used map to the given presenter
     *
     * @param impl the presenter
     */
    private void giveMap(@NonNull HomePresenterImpl impl)
    {
        impl.onMapAvailable(mMapApiMock);
    }

    /**
     * Set all things up and ready for this presenter including location permission and map
     *
     * @param impl the presenter
     */
    private void setAllThingsUp(@NonNull HomePresenterImpl impl)
    {
        giveLocationPermission(impl);
        giveMap(impl);
    }
}
