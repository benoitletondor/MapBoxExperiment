package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BasePresenterImpl;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryPresenter;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryView;

/**
 * Implementation of {@link HistoryPresenter}
 *
 * @author Benoit LETONDOR
 */
public final class HistoryPresenterImpl extends BasePresenterImpl<HistoryView> implements HistoryPresenter
{
    @NonNull
    private final MarkerStorageInteractor mMarkerStorageInteractor;

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
        }
    }
}
