package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.Nullable;

/**
 * Interface that defines common methods of a map Marker
 *
 * @author Benoit LETONDOR
 */
public interface MapMarker
{
    /**
     * Get the latitude of the marker
     *
     * @return the latitude of the marker
     */
    double getLatitude();

    /**
     * Get the longitude of the marker
     *
     * @return the longitude of the marker
     */
    double getLongitude();

    /**
     * Get the name of the marker
     *
     * @return the name if any, null otherwise
     */
    @Nullable
    String getName();

    /**
     * Get the caption of the marker
     *
     * @return the caption if any, null otherwise
     */
    @Nullable
    String getCaption();
}
