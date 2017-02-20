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
     * Get the current location of the camera center
     *
     * @return the location of the camera center
     */
    @NonNull
    CameraCenterLocation getCameraCenterLocation();

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
     * Remove the given marker from the map
     *
     * @param marker removed marker
     */
    void removeMarker(@NonNull MapMarker marker);

    /**
     * Set the map click listener
     *
     * @param listener the map click listener
     */
    void setOnMapClickedListener(@Nullable OnMapClickListener listener);

    /**
     * Set the map camera move listener
     *
     * @param listener the map camera move listener
     */
    void setOnCameraMoveListener(@Nullable OnCameraMoveListener listener);

    /**
     * Select the given marker
     *
     * @param marker the marker to select
     */
    void selectMarker(@NonNull MapMarker marker);
}
