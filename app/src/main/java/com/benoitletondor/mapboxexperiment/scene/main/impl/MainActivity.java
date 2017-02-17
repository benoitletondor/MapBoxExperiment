package com.benoitletondor.mapboxexperiment.scene.main.impl;

import android.support.annotation.NonNull;
import android.os.Bundle;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseActivity;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;
import com.benoitletondor.mapboxexperiment.scene.main.injection.DaggerMainViewComponent;
import com.benoitletondor.mapboxexperiment.scene.main.injection.MainViewModule;

import javax.inject.Inject;

public final class MainActivity extends BaseActivity<MainPresenter, MainView> implements MainView
{
    @Inject
    PresenterFactory<MainPresenter> mPresenterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent)
    {
        DaggerMainViewComponent.builder()
            .appComponent(appComponent)
            .mainViewModule(new MainViewModule())
            .build()
            .inject(this);
    }

    @Override
    protected PresenterFactory<MainPresenter> getPresenterFactory()
    {
        return mPresenterFactory;
    }
}
