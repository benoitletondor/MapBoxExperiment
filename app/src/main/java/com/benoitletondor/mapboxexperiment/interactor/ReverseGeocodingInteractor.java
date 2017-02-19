package com.benoitletondor.mapboxexperiment.interactor;

import android.location.Address;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;

import io.reactivex.Observable;

public interface ReverseGeocodingInteractor extends BaseInteractor
{
    @NonNull
    Observable<Address> reverseGeocode(double latitude, double longitude);
}
