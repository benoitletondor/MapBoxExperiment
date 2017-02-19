package com.benoitletondor.mapboxexperiment.scene.history.injection;

import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.injection.FragmentScope;
import com.benoitletondor.mapboxexperiment.scene.history.impl.HistoryFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = HistoryViewModule.class)
public interface HistoryViewComponent
{
    void inject(HistoryFragment fragment);
}
