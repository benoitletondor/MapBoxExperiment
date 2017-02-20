package com.benoitletondor.mapboxexperiment.scene.history;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BasePresenter;

/**
 * Presenter of the {@link HistoryView}
 *
 * @author Benoit LETONDOR
 */
public interface HistoryPresenter extends BasePresenter<HistoryView>
{
    /**
     * Called when the user press a marker on the list
     */
    void onMarkerClicked();
}
