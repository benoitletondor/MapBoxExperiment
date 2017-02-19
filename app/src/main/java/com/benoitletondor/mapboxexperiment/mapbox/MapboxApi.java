package com.benoitletondor.mapboxexperiment.mapbox;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.map.CameraCenterLocation;
import com.benoitletondor.mapboxexperiment.common.map.MapApi;
import com.benoitletondor.mapboxexperiment.common.map.MapLocationSource;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.map.OnCameraMoveListener;
import com.benoitletondor.mapboxexperiment.common.map.OnMapClickListener;
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

    @NonNull
    @Override
    public CameraCenterLocation getCameraCenterLocation()
    {
        final CameraPosition cameraPosition = mMapboxMap.getCameraPosition();
        return new CameraCenterLocation(cameraPosition.target.getLatitude(), cameraPosition.target.getLongitude());
    }

    @NonNull
    @Override
    public MapMarker addMarker(double latitude, double longitude, @Nullable String title, @Nullable String snippet)
    {
        final MarkerViewOptions options = new MarkerViewOptions()
            .position(new LatLng(latitude, longitude));

        if( title != null )
        {
            options.title(title);
        }

        if( snippet != null )
        {
            options.snippet(snippet);
        }

        return new MapboxMarker(mMapboxMap.addMarker(options));
    }

    @Override
    public void setOnMapClickedListener(@Nullable final OnMapClickListener listener)
    {
        if( listener == null )
        {
            mMapboxMap.setOnMapClickListener(null);
        }
        else
        {
            mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(@NonNull LatLng point)
                {
                    listener.onMapClicked(point.getLatitude(), point.getLongitude());
                }
            });
        }
    }

    @Override
    public void setOnCameraMoveListener(@Nullable final OnCameraMoveListener listener)
    {
        if( listener == null )
        {
            mMapboxMap.setOnCameraChangeListener(null);
        }
        else
        {
            mMapboxMap.setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener()
            {
                @Nullable
                private CameraCenterLocation mLastLocation;

                @Override
                public void onCameraChange(CameraPosition position)
                {
                    final CameraCenterLocation newLocation = new CameraCenterLocation(position.target.getLatitude(), position.target.getLongitude());
                    if( !newLocation.equals(mLastLocation) )
                    {
                        listener.onMapCameraMove(newLocation);
                    }

                    mLastLocation = newLocation;
                }
            });
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location newLocation)
    {
        final LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        if( mUserLocationMarker == null )
        {
            final IconFactory iconFactory = IconFactory.getInstance(mAppContext);
            final VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(mAppContext.getResources(), R.drawable.ic_user_position_map_marker, mAppContext.getTheme());
            assert drawableCompat != null;
            final Icon icon = iconFactory.fromDrawable(drawableCompat);

            mUserLocationMarker = mMapboxMap.addMarker(new MarkerViewOptions()
                .position(position)
                .icon(icon));
        }
        else
        {
            mUserLocationMarker.setPosition(position);
        }
    }
}
