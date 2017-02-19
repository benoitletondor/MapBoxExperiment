package com.benoitletondor.mapboxexperiment.scene.main.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.OnBackPressedInterceptor;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseActivity;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.scene.home.impl.HomeFragment;
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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FIXME change that when supporting more views
        if( getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment_container) == null )
        {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_fragment_container, new HomeFragment())
                .commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        // Send on back pressed event to the fragment displayed
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment_container);
        if( fragment != null && fragment instanceof OnBackPressedInterceptor)
        {
            if( ((OnBackPressedInterceptor) fragment).onBackPressed() )
            {
                return;
            }
        }

        super.onBackPressed();
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

    @Override
    public void setViewTitle(@NonNull String title)
    {
        setTitle(title);
    }
}
