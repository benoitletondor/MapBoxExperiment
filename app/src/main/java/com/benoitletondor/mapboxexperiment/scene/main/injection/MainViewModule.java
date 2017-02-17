package com.benoitletondor.mapboxexperiment.scene.main.injection;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.impl.MainPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class MainViewModule
{
    @Provides
    public PresenterFactory<MainPresenter> providePresenterFactory()
    {
        return new PresenterFactory<MainPresenter>()
        {
            @NonNull
            @Override
            public MainPresenter create()
            {
                return new MainPresenterImpl();
            }
        };
    }
}
