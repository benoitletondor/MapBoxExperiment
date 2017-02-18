package com.benoitletondor.mapboxexperiment.mapbox;

import android.support.annotation.NonNull;

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
    private final Marker mMarker;

// ----------------------------------->

    MapboxMarker(@NonNull Marker marker)
    {
        mMarker = marker;
    }

// ----------------------------------->
}
