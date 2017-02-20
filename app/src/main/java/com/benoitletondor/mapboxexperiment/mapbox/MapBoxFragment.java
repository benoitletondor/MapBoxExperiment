package com.benoitletondor.mapboxexperiment.mapbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benoitletondor.mapboxexperiment.common.map.MapLoadingCallback;
import com.benoitletondor.mapboxexperiment.common.map.MapViewFragment;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

/**
 * Mapbox implementation of the {@link MapViewFragment}.
 *
 * This implementation contains an ugly hack that manually persists the state of the map and
 * re-inject it using MapboxMapOptions in onCreateView because of a bad handling of state restoration
 * when used in backstack.
 *
 * @author Benoit LETONDOR
 */
public final class MapBoxFragment extends MapViewFragment
{
    @Nullable
    private MapView mMapView;
    @Nullable
    private Bundle mSavedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // FIXME remove that trick when a suitable solution is found
        MapboxMapOptions options = null;
        if( mSavedState != null )
        {
            options = new MapboxMapOptions();
            final CameraPosition cameraPosition = mSavedState.getParcelable(MapboxConstants.STATE_CAMERA_POSITION);
            options.camera(cameraPosition);
        }

        if( options == null )
        {
            return new MapView(container.getContext());
        }
        else
        {
            return new MapView(container.getContext(), options);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view;
        mMapView.onCreate(savedInstanceState == null ? mSavedState : savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if( mMapView != null )
        {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause()
    {
        if( mMapView != null )
        {
            mMapView.onPause();
        }

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if( mMapView != null )
        {
            mMapView.onSaveInstanceState(outState);
        }
        else if( mSavedState != null )
        {
            outState.putAll(mSavedState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        if( mMapView != null )
        {
            mMapView.onLowMemory();
        }

        super.onLowMemory();
    }

    @Override
    public void onDestroyView()
    {
        if( mMapView != null )
        {
            mSavedState = new Bundle();
            mMapView.onSaveInstanceState(mSavedState);
            mMapView.onDestroy();
        }

        mMapView = null;

        super.onDestroyView();
    }

    @Override
    public void loadMapAPI(@NonNull final MapLoadingCallback callback)
    {
        if( mMapView == null )
        {
            callback.onErrorLoadingMap(new NullPointerException("MapBoxFragment.loadMapAPI called while view is not created"));
            return;
        }

        mMapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                callback.onMapReady(new MapboxApi(getContext(), mapboxMap));
            }
        });
    }
}
