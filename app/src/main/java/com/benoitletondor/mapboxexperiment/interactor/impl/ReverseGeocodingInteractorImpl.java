package com.benoitletondor.mapboxexperiment.interactor.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.map.ReverseGeocoder;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.impl.BaseInteractorImpl;
import com.benoitletondor.mapboxexperiment.interactor.ReverseGeocodingInteractor;

import java.io.InterruptedIOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Implementation of {@link ReverseGeocodingInteractor}
 *
 * @author Benoit LETONDOR
 */
public final class ReverseGeocodingInteractorImpl extends BaseInteractorImpl implements ReverseGeocodingInteractor
{
    @NonNull
    private final ReverseGeocoder mReverseGeocoder;

// ------------------------------------->

    public ReverseGeocodingInteractorImpl(@NonNull ReverseGeocoder reverseGeocoder)
    {
        mReverseGeocoder = reverseGeocoder;
    }

// ------------------------------------->

    @NonNull
    @Override
    public Observable<Address> reverseGeocode(final double latitude, final double longitude)
    {
        return Observable.fromCallable(new Callable<Address>()
        {
            @Override
            public Address call() throws Exception
            {
                final List<Address> addresses;
                try
                {
                     addresses = mReverseGeocoder.getFromLocation(latitude, longitude, 1);
                }
                // FIXME this is needed cause okhttp throws if the observable is disposed and it crashes cause it cannot deliver the exception
                // TODO find a better way to handle this
                catch (InterruptedIOException e)
                {
                    return new Address(Locale.getDefault());
                }

                if( addresses.isEmpty() )
                {
                    return new Address(Locale.getDefault());
                }

                return addresses.get(0);
            }
        });
    }
}
