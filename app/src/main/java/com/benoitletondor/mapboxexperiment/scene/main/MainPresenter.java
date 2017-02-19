package com.benoitletondor.mapboxexperiment.scene.main;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BasePresenter;

/**
 * Presenter of the {@link MainView}
 *
 * @author Benoit LETONDOR
 */
public interface MainPresenter extends BasePresenter<MainView>
{
    /**
     * Called when the user presses the home button from the drawer menu
     */
    void onHomeButtonClicked();

    /**
     * Called when the user pressed the history button from the drawer menu
     */
    void onHistoryButtonClicked();
}
