package com.benoitletondor.mapboxexperiment.mapbox;

import android.location.Address;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benoitletondor.mapboxexperiment.common.map.ReverseGeocoder;
import com.mapbox.geocoder.android.AndroidGeocoder;

import java.io.IOException;
import java.util.Arrays;
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
        final List<Address> addresses = mGeocoder.getFromLocation(latitude, longitude, maxResults);

        // Mapbox geocoder doesn't properly fill Address fields, so we iterate addresses and try to fix that
        // FIXME this is far from optimal and doesn't properly handle all the cases
        for(Address address : addresses)
        {
            final String addressLine =  address.getAddressLine(0);
            if( addressLine != null )
            {
                final String[] components = addressLine.split(",");
                switch (components.length)
                {
                    case 2:
                        address.setLocality(components[0].trim());
                        address.setCountryName(components[1].trim());
                        break;
                    case 3:
                        address.setLocality(components[0].trim());
                        address.setPostalCode(formatPostalCode(components[1]).trim());
                        address.setCountryName(components[2].trim());
                        break;
                    case 4:
                        address.setLocality(components[1].trim());
                        address.setPostalCode(formatPostalCode(components[2]).trim());
                        address.setCountryName(components[3].trim());
                        break;
                    case 5:
                        address.setLocality(components[2].trim());
                        address.setPostalCode(formatPostalCode(components[3]).trim());
                        address.setCountryName(components[4].trim());
                        break;
                }
            }
        }

        return addresses;
    }

    private static String formatPostalCode(@NonNull String rawPostalCode)
    {
        final String[] parts = rawPostalCode.trim().split(" ");
        if( parts.length == 1 )
        {
            return rawPostalCode;
        }

        return TextUtils.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
    }
}
