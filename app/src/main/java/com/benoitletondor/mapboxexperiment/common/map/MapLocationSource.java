package com.benoitletondor.mapboxexperiment.common.map;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Interface that defines method of location source used by the map to display device location
 *
 * @author Benoit LETONDOR
 */
public interface MapLocationSource
{
    /**
     * Activate the source with the given location change listener. After this method has been called
     * user location should be reported to the listener.
     *
     * @param listener the location change listener.
     */
    void activate(@NonNull OnLocationChangedListener listener);

    /**
     * Listener for device location changes
     */
    interface OnLocationChangedListener
    {
        /**
         * Called when the device location changes
         *
         * @param newLocation the new device location
         */
        void onLocationChanged(@NonNull Location newLocation);
    }
}
