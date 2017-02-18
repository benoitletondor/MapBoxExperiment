package com.benoitletondor.mapboxexperiment.common.mvp.view.impl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapLoadingCallback;
import com.benoitletondor.mapboxexperiment.common.map.MapViewFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BaseMapPresenter;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

/**
 * A base fragment for all views including a map, wrapping all the runtime callbacks. Your fragment
 * displaying a map should extend this one.
 *
 * @author Benoit LETONDOR
 */
public abstract class BaseMapFragment<P extends BaseMapPresenter<V>, V extends BaseMapView> extends BaseFragment<P, V> implements BaseMapView
{
    /**
     * Default value for {@link #mTempLocationResult} == null
     */
    private static final int DEFAULT_TEMP_LOCATION_RESULT = -10;
    /**
     * Request code for location permission request
     */
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    /**
     * Temp storage of the location permission result to avoid doing with the result outside of the lifecycle
     */
    private int mTempLocationResult = DEFAULT_TEMP_LOCATION_RESULT;
    /**
     * The res id of the container for the {@link MapViewFragment}
     */
    @IdRes
    private final int mMapContainerId;

// ------------------------------------------>

    /**
     * Creates a new fragment
     *
     * @param mapContainerId the res id of the container for the {@link MapViewFragment}.
     */
    protected BaseMapFragment(@IdRes int mapContainerId)
    {
        mMapContainerId = mapContainerId;
    }

    @Override
    @NonNull
    public GoogleApiClient.Builder getAPIBuilder()
    {
        return new GoogleApiClient.Builder(getActivity().getApplicationContext());
    }

    @Override
    public void loadMap()
    {
        MapViewFragment mapFragment = (MapViewFragment) getChildFragmentManager().findFragmentById(mMapContainerId);
        if( mapFragment == null )
        {
            mapFragment = createMapView();

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(mMapContainerId, mapFragment);
            fragmentTransaction.commitNow(); // Now is important, otherwise the following loadMapAPI would failed on first display cause map would'nt be loaded
        }

        mapFragment.loadMapAPI(new MapLoadingCallback()
        {
            @Override
            public void onMapReady(@NonNull MapApi map)
            {
                if( mPresenter != null )
                {
                    mPresenter.onMapReady(map);
                }
            }

            @Override
            public void onErrorLoadingMap(@NonNull Exception error)
            {
                if( mPresenter != null )
                {
                    mPresenter.onMapNotAvailable(error);
                }
            }
        });
    }

    @Override
    public void requestLocationPermission()
    {
        if( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            if( mPresenter != null )
            {
                mPresenter.onLocationPermissionGranted();
            }
        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0 )
        {
            if( mPresenter != null )
            {
                if( grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    mPresenter.onLocationPermissionGranted();
                }
                else
                {
                    mPresenter.onLocationPermissionDenied();
                }
            }
            else
            {
                mTempLocationResult = grantResults[0];
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if( mTempLocationResult != DEFAULT_TEMP_LOCATION_RESULT && mPresenter != null )
        {
            if( mTempLocationResult == PackageManager.PERMISSION_GRANTED )
            {
                mPresenter.onLocationPermissionGranted();
            }
            else
            {
                mPresenter.onLocationPermissionDenied();
            }

            mTempLocationResult = DEFAULT_TEMP_LOCATION_RESULT;
        }
    }
}
