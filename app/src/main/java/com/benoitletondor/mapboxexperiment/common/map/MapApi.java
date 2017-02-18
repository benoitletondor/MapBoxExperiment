package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

/**
 * An interface that defines methods of a map API.
 *
 * @author Benoit LETONDOR
 */
public interface MapApi
{
    /**
     * Enable user location display on the map with the given location source
     *
     * @param source the location source
     */
    void setUserLocationEnabledWithSource(@NonNull MapLocationSource source);

    /**
     * Clear all displayed markers on the map
     */
    void clear();

    /**
     * Move the map camera
     *
     * @param latitude new latitude of the camera
     * @param longitude new longitude of the camera
     * @param zoom new zoom of the camera
     * @param animated should the move be animated or not
     */
    void moveCamera(double latitude, double longitude, double zoom, boolean animated);
}
