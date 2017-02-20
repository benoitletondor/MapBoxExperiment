package com.benoitletondor.mapboxexperiment.scene.main.impl;

import android.support.annotation.VisibleForTesting;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BasePresenterImpl;
import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;

/**
 * Implementation of the {@link MainPresenter}
 *
 * @author Benoit LETONDOR
 */
@VisibleForTesting // Not final for testing
public class MainPresenterImpl extends BasePresenterImpl<MainView> implements MainPresenter
{
    @Override
    public void onHomeButtonClicked()
    {
        if( mView != null )
        {
            mView.showHomeView();
        }
    }

    @Override
    public void onHistoryButtonClicked()
    {
        if( mView != null )
        {
            mView.showHistoryView();
        }
    }
}
