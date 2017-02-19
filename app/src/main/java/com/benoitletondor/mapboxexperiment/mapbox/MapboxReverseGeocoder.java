package com.benoitletondor.mapboxexperiment.mapbox;

import android.location.Address;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.map.ReverseGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;

import java.io.IOException;
import java.util.List;

/**
 * Mapbox implementation of {@link ReverseGeocoder} that wraps an {@link AndroidGeocoder}
 *
 * @author Benoit LETONDOR
 */
public final class MapboxReverseGeocoder implements ReverseGeocoder
{
    @NonNull
    private final AndroidGeocoder mGeocoder;

// ---------------------------------->

    public MapboxReverseGeocoder(@NonNull AndroidGeocoder geocoder)
    {
        mGeocoder = geocoder;
    }

// ---------------------------------->

    @NonNull
    @Override
    public List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException
    {
        return mGeocoder.getFromLocation(latitude, longitude, maxResults);
    }
}
