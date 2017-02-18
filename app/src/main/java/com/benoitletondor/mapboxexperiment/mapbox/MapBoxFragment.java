package com.benoitletondor.mapboxexperiment.mapbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benoitletondor.mapboxexperiment.common.map.MapLoadingCallback;
import com.benoitletondor.mapboxexperiment.common.map.MapViewFragment;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

/**
 * Mapbox implementation of the {@link MapViewFragment}
 *
 * @author Benoit LETONDOR
 */
public final class MapBoxFragment extends MapViewFragment
{
    @Nullable
    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new MapView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view;
        mMapView.onCreate(savedInstanceState);
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
            mMapView.onDestroy();
            mMapView = null;
        }

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
