package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
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

    /**
     * The loading view, from XML
     */
    private View mLoadingView;
    /**
     * The recycler view for displaying markers, from XML
     */
    private RecyclerView mMarkersRecyclerView;

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

        mMarkersRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_history_recycler_view);
        mMarkersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mMarkersRecyclerView.setHasFixedSize(true);

        final DividerItemDecoration horizontalDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        final Drawable horizontalDivider = ContextCompat.getDrawable(view.getContext(), R.drawable.cell_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mMarkersRecyclerView.addItemDecoration(horizontalDecoration);

        mLoadingView = view.findViewById(R.id.fragment_history_loading_view);
    }

    @Override
    public void onDestroyView()
    {
        mMarkersRecyclerView = null;
        mLoadingView = null;

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

    @Override
    public void showLoadingView()
    {
        mMarkersRecyclerView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContentView()
    {
        mLoadingView.setVisibility(View.GONE);
        mMarkersRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setHistoryMarkersAdapter(@NonNull RecyclerView.Adapter adapter)
    {
        mMarkersRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoadingMarkersError()
    {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.error_loading_markers_title)
            .setMessage(R.string.error_loading_markers_message)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    @Override
    public void onMarkerClicked(@NonNull MapMarker marker)
    {
        if( mPresenter != null )
        {
            mPresenter.onMarkerClicked();
        }
    }
}
