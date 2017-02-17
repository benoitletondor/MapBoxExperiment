package com.benoitletondor.mapboxexperiment.common.mvp.presenter.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BasePresenter;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.BaseInteractor;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Abstract presenter implementation that contains base implementation for other presenters.
 * Subclasses must call super for all {@link BasePresenter} method overriding.
 *
 * @author Benoit LETONDOR
 */
public abstract class BasePresenterImpl<V> implements BasePresenter<V>
{
    /**
     * Composite subscription that will be cleared onStop
     */
    @NonNull
    private final CompositeDisposable mCompositeSubscription = new CompositeDisposable();
    /**
     * The interactor
     */
    @Nullable
    private final BaseInteractor[] mInteractors;
    /**
     * The view
     */
    @Nullable
    protected V mView;

// ------------------------------------------->

    protected BasePresenterImpl(@Nullable BaseInteractor... interactors)
    {
        mInteractors = interactors;
    }


    @Override
    public void onViewAttached(@NonNull V view)
    {
        mView = view;
    }


    @Override
    public void onStart(boolean viewCreated)
    {

    }

    @Override
    public void onStop()
    {

    }


    @Override
    public void onViewDetached()
    {
        mView = null;
    }

    @Override
    public void onPresenterDestroyed()
    {
        if( mInteractors != null )
        {
            for(BaseInteractor interactor : mInteractors)
            {
                interactor.finish();
            }
        }

        mCompositeSubscription.clear();
    }

    /**
     * Add the subscription to the stack of subs that will be cleared when the activity stops
     *
     * @param subscription the subscription to clear
     * @return the passed subscription for chaining
     */
    @NonNull
    protected Disposable addSubscription(@NonNull Disposable subscription)
    {
        mCompositeSubscription.add(subscription);
        return subscription;
    }

    /**
     * Clear all previously registered subscription for this presenter
     */
    protected void clearSubscriptions()
    {
        mCompositeSubscription.clear();
    }
}
