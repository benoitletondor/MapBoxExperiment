package com.benoitletondor.mapboxexperiment.common.map;

import android.support.annotation.NonNull;

/**
 * An interface that defines methods that an auto completed location item should implement
 *
 * @author Benoit LETONDOR
 */
public interface AutoCompleteLocationItem
{
    @NonNull
    String getLocationName();

    double getLatitude();

    double getLongitude();
}
