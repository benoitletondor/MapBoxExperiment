package com.benoitletondor.mapboxexperiment.scene.main.injection;

import com.benoitletondor.mapboxexperiment.injection.ActivityScope;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.scene.main.impl.MainActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = MainViewModule.class)
public interface MainViewComponent
{
    void inject(MainActivity activity);
}