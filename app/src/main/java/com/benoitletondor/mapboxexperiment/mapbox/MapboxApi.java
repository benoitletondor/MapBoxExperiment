package com.benoitletondor.mapboxexperiment.mapbox;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapLocationSource;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Wrapper around the {@link MapboxMap} that implements {@link MapApi}
 *
 * @author Benoit LETONDOR
 */
final class MapboxApi implements MapApi, MapLocationSource.OnLocationChangedListener
{
    @NonNull
    private final MapboxMap mMapboxMap;
    @NonNull
    private final Context mAppContext;

    @Nullable
    private Marker mUserLocationMarker;

// ---------------------------------->

    MapboxApi(@NonNull Context context, @NonNull MapboxMap mapboxMap)
    {
        mAppContext = context.getApplicationContext();
        mMapboxMap = mapboxMap;
    }

// ---------------------------------->

    @Override
    public void setUserLocationEnabledWithSource(@NonNull MapLocationSource source)
    {
        /**
         * We don't simply use mMapboxMap.setMyLocationEnabled(true); here for compat reason with other
         * maps system that doesn't include advanced functions using built-in location (as Gmaps)
         */
        source.activate(this);
    }

    @Override
    public void clear()
    {
        mMapboxMap.clear();
    }

    @Override
    public void moveCamera(double latitude, double longitude, double zoom, boolean animated)
    {
        final CameraPosition newPosition = new CameraPosition.Builder(mMapboxMap.getCameraPosition())
            .target(new LatLng(latitude, longitude))
            .zoom(zoom)
            .build();

        final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(newPosition);

        if( animated )
        {
            mMapboxMap.animateCamera(cameraUpdate);
        }
        else
        {
            mMapboxMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location newLocation)
    {
        final LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        if( mUserLocationMarker == null )
        {
            mUserLocationMarker = mMapboxMap.addMarker(new MarkerViewOptions()
                .position(position));

            final IconFactory iconFactory = IconFactory.getInstance(mAppContext);
            final VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(mAppContext.getResources(), R.drawable.ic_user_position_map_marker, mAppContext.getTheme());
            assert drawableCompat != null;
            final Icon icon = iconFactory.fromDrawable(drawableCompat);

            mUserLocationMarker.setIcon(icon);
        }
        else
        {
            mUserLocationMarker.setPosition(position);
        }
    }
}
