package com.benoitletondor.mapboxexperiment.common.map;

import android.location.Address;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * Interface that defines common methods of a reverse geocoder. This is simply a copy of
 * {@link android.location.Geocoder} method.
 *
 * @author Benoit LETONDOR
 */
public interface ReverseGeocoder
{
    /**
     * Returns an array of Addresses that are known to describe the area immediately surrounding the
     * given latitude and longitude.
     * The returned values may be obtained by means of a network lookup. The results are a best guess
     * and are not guaranteed to be meaningful or correct. It may be useful to call this method
     * from a thread separate from your primary UI thread.
     *
     * @param latitude the latitude a point for the search
     * @param longitude the longitude a point for the search
     * @param maxResults max number of addresses to return. Smaller numbers (1 to 5) are recommended
     * @return a list of Address objects. Returns null or empty list if no matches were found or there is no backend service available.
     * @throws IOException if the network is unavailable or any other I/O problem occurs
     */
    @NonNull
    List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException;
}
