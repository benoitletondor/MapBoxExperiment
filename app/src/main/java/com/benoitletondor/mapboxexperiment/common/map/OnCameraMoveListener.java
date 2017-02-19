package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

public interface OnCameraMoveListener
{
    void onMapCameraMove(@NonNull CameraCenterLocation newCameraCenterLocation);
}
