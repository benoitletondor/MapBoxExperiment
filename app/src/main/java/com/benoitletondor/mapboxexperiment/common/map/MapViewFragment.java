package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * An abstract fragment that contains common methods of a MapView
 *
 * @author Benoit LETONDOR
 */
public abstract class MapViewFragment extends Fragment
{
    /**
     * Loads the {@link MapApi} associated with this Map view
     *
     * @param callback the load callback
     */
    public abstract void loadMapAPI(@NonNull MapLoadingCallback callback);
}