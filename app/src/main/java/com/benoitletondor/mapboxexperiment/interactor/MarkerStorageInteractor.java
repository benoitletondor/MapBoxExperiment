package com.benoitletondor.mapboxexperiment.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Interactor to store marker added by the user
 *
 * @author Benoit LETONDOR
 */
public interface MarkerStorageInteractor extends BaseInteractor
{
    /**
     * Load the stored list of markers
     *
     * @return an observable of a list containing stored markers
     */
    @NonNull
    Observable<List<StoredMarker>> retrieveStoredMarkers();

    /**
     * Store the given list of markers
     *
     * @param markers the list of markers to save
     * @return a completable of the storage action
     */
    Completable storeMarkers(@NonNull List<MapMarker> markers);

// ---------------------------------->

    /**
     * Implementation of {@link MapMarker} that represents a stored marker
     */
    final class StoredMarker implements MapMarker
    {
        private final double mLatitude;
        private final double mLongitude;
        @Nullable
        private final String mName;
        @Nullable
        private final String mCaption;

        public StoredMarker(double latitude, double longitude, @Nullable String name, @Nullable String caption)
        {
            mLatitude = latitude;
            mLongitude = longitude;
            mName = name;
            mCaption = caption;
        }

        @Override
        public double getLatitude()
        {
            return mLatitude;
        }

        @Override
        public double getLongitude()
        {
            return mLongitude;
        }

        @Nullable
        @Override
        public String getName()
        {
            return mName;
        }

        @Nullable
        @Override
        public String getCaption()
        {
            return mCaption;
        }
    }
}
