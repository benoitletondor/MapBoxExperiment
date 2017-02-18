package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    /**
     * Add a marker on the map with the given attributes
     *
     * @param latitude latitude of the marker
     * @param longitude longitude of the marker
     * @param title optional title of the marker
     * @param snippet optional snippet of the marker
     * @return the newly shown marker
     */
    @NonNull
    MapMarker addMarker(double latitude, double longitude, @Nullable String title, @Nullable String snippet);

    /**
     * Set the map click listener
     *
     * @param listener the map click listener
     */
    void setOnMapClickedListener(@Nullable OnMapClickListener listener);
}
