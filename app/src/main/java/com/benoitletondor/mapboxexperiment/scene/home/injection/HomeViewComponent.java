package com.benoitletondor.mapboxexperiment.scene.home.injection;

import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.injection.FragmentScope;
import com.benoitletondor.mapboxexperiment.scene.home.impl.HomeFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = HomeViewModule.class)
public interface HomeViewComponent
{
    void inject(HomeFragment fragment);
}