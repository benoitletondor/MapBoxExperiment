package com.benoitletondor.mapboxexperiment.mapbox;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;

/**
 * Mapbox implementation of the {@link AutoCompleteLocationItem}
 *
 * @author Benoit LETONDOR
 */
public final class MapboxAutocompleteLocationItem implements AutoCompleteLocationItem
{
    @NonNull
    private final CarmenFeature mFeature;

// -------------------------------->

    MapboxAutocompleteLocationItem(@NonNull CarmenFeature feature)
    {
        mFeature = feature;
    }

// -------------------------------->

    @NonNull
    @Override
    public String getLocationName()
    {
        return mFeature.toString();
    }

    @Override
    public double getLatitude()
    {
        return mFeature.asPosition().getLatitude();
    }

    @Override
    public double getLongitude()
    {
        return mFeature.asPosition().getLongitude();
    }
}
