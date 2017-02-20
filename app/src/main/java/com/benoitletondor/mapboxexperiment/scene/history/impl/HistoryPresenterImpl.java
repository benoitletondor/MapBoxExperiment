package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BasePresenterImpl;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryPresenter;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation of {@link HistoryPresenter}
 *
 * @author Benoit LETONDOR
 */
public final class HistoryPresenterImpl extends BasePresenterImpl<HistoryView> implements HistoryPresenter
{
    /**
     * The marker storage interactor, ready to be used
     */
    @NonNull
    private final MarkerStorageInteractor mMarkerStorageInteractor;
    /**
     * Current state
     */
    @NonNull
    private State mState = State.CREATED;
    /**
     * Load data, will be null until {@link #mState} is {@link State#LOAD}
     */
    @Nullable
    private List<MapMarker> mMarkers;

// ---------------------------------->

    public HistoryPresenterImpl(@NonNull MarkerStorageInteractor markerStorageInteractor)
    {
        super(markerStorageInteractor);

        mMarkerStorageInteractor = markerStorageInteractor;
    }

    @Override
    public void onStart(boolean viewCreated)
    {
        super.onStart(viewCreated);
        assert mView != null;

        if( viewCreated )
        {
            mView.setHistoryViewTitle();

            switch (mState)
            {
                case CREATED:
                    loadMarkers();
                    break;
                case LOADING:
                    mView.showLoadingView();
                    break;
                case LOAD:
                    showData();
                    break;
            }
        }
    }

    private void loadMarkers()
    {
        mState = State.LOADING;

        if( mView != null )
        {
            mView.showLoadingView();
        }

        addSubscription(mMarkerStorageInteractor.retrieveStoredMarkers()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<MarkerStorageInteractor.StoredMarker>>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull List<MarkerStorageInteractor.StoredMarker> storedMarkers) throws Exception
                {
                    mMarkers = new ArrayList<>(storedMarkers.size());
                    for(MarkerStorageInteractor.StoredMarker marker : storedMarkers)
                    {
                        mMarkers.add(marker);
                    }

                    mState = State.LOAD;
                    showData();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    mState = State.CREATED;

                    mView.showLoadingMarkersError();
                    // TODO go back to home screen
                }
            }));
    }

    private void showData()
    {
        assert mMarkers != null;
        assert mView != null;

        final MarkersHistoryAdapter adapter = new MarkersHistoryAdapter(mMarkers, mView);
        mView.setHistoryMarkersAdapter(adapter);

        mView.showContentView();
    }

    @Override
    public void onMarkerClicked()
    {

    }

// ---------------------------------->

    private enum State
    {
        CREATED,

        LOADING,

        LOAD
    }
}
