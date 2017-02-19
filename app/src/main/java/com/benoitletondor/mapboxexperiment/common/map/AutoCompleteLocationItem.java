package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

/**
 * An interface that defines methods that an auto completed location item should implement
 *
 * @author Benoit LETONDOR
 */
public interface AutoCompleteLocationItem
{
    /**
     * Get the name of the location
     *
     * @return a human-readable location name
     */
    @NonNull
    String getLocationName();

    /**
     * Get the latitude of the location
     *
     * @return the latitude of the location
     */
    double getLatitude();

    /**
     * Get the longitude of the location
     *
     * @return the longitude of the location
     */
    double getLongitude();
}
