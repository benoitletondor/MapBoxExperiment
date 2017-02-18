package com.benoitletondor.mapboxexperiment.common.map;

/**
 * An interface that defines callback for a click on the map.
 *
 * @author Benoit LETONDOR
 */
public interface OnMapClickListener
{
    /**
     * Called when the map has been clicked
     *
     * @param latitude the latitude of the touch
     * @param longitude the longitude of the touch
     */
    void onClick(double latitude, double longitude);
}
