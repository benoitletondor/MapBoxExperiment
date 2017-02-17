package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

/**
 * An interface that defines callback for map loading.
 *
 * @author Benoit LETONDOR
 */
public interface MapLoadingCallback
{
    /**
     * Called when the map successfully load and can provide a {@link MapApi}
     *
     * @param map the map api, ready to be used
     */
    void onMapReady(@NonNull MapApi map);

    /**
     * Called when an error occurred loading the map
     *
     * @param error the encountered error
     */
    void onErrorLoadingMap(@NonNull Exception error);
}
