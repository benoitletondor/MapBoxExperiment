package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseFragment;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryPresenter;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryView;
import com.benoitletondor.mapboxexperiment.scene.history.injection.DaggerHistoryViewComponent;
import com.benoitletondor.mapboxexperiment.scene.history.injection.HistoryViewModule;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;

import javax.inject.Inject;

/**
 * Implementation of {@link HistoryView}
 *
 * @author Benoit LETONDOR
 */
public final class HistoryFragment extends BaseFragment<HistoryPresenter, HistoryView> implements HistoryView
{
    /**
     * Presenter factory, used by MVP
     */
    @Inject
    PresenterFactory<HistoryPresenter> mPresenterFactory;
    /**
     * The instance of {@link MainView} this fragment is currently attached to. Will be null if detached
     */
    @Nullable
    private MainView mMainView;

// -------------------------------->

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView()
    {


        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if( context instanceof MainView )
        {
            mMainView = (MainView) context;
        }
    }

    @Override
    public void onDetach()
    {
        mMainView = null;

        super.onDetach();
    }

// -------------------------------->

    @Override
    protected PresenterFactory<HistoryPresenter> getPresenterFactory()
    {
        return mPresenterFactory;
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent)
    {
        DaggerHistoryViewComponent.builder()
            .appComponent(appComponent)
            .historyViewModule(new HistoryViewModule())
            .build()
            .inject(this);
    }

// -------------------------------->

    @Override
    public void setHistoryViewTitle()
    {
        if( mMainView != null )
        {
            mMainView.setViewTitle(getString(R.string.history));
        }
    }
}
