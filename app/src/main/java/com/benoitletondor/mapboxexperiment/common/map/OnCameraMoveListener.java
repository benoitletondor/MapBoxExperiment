package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

/**
 * An interface that defines callback for a camera move on the map.
 *
 * @author Benoit LETONDOR
 */
public interface OnCameraMoveListener
{
    /**
     * Called when the map camera moved
     *
     * @param newCameraCenterLocation the new camera center location
     */
    void onMapCameraMove(@NonNull CameraCenterLocation newCameraCenterLocation);
}
