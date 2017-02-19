package com.benoitletondor.mapboxexperiment.mapbox;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.mapbox.mapboxsdk.annotations.Marker;

/**
 * Mapbox implementation of {@link MapMarker} that wraps a {@link Marker}
 *
 * @author Benoit LETONDOR
 */
final class MapboxMarker implements MapMarker
{
    @NonNull
    final Marker mMarker;

// ----------------------------------->

    MapboxMarker(@NonNull Marker marker)
    {
        mMarker = marker;
    }

// ----------------------------------->

    @Override
    public double getLatitude()
    {
        return mMarker.getPosition().getLatitude();
    }

    @Override
    public double getLongitude()
    {
        return mMarker.getPosition().getLongitude();
    }

    @Nullable
    @Override
    public String getName()
    {
        return mMarker.getTitle();
    }

    @Nullable
    @Override
    public String getCaption()
    {
        return mMarker.getSnippet();
    }
}
