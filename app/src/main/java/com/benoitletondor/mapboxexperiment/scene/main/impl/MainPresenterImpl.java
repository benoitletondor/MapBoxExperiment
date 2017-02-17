package com.benoitletondor.mapboxexperiment.scene.main.impl;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl.BasePresenterImpl;
import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;

public final class MainPresenterImpl extends BasePresenterImpl<MainView> implements MainPresenter
{
    public MainPresenterImpl()
    {

    }

    @Override
    public void onStart(boolean viewCreated)
    {
        super.onStart(viewCreated);
        assert mView != null;


    }
}
