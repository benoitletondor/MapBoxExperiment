package com.benoitletondor.mapboxexperiment.interactor;

import android.location.Address;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;

import io.reactivex.Observable;

/**
 * Interactor that performs reverse geocoding actions
 *
 * @author Benoit LETONDOR
 */
public interface ReverseGeocodingInteractor extends BaseInteractor
{
    /**
     * Reverse the given coordinates into an address
     *
     * @param latitude latitude
     * @param longitude longitude
     * @return the matching address
     */
    @NonNull
    Observable<Address> reverseGeocode(double latitude, double longitude);
}
