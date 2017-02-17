package com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.common.mvp.presenter.BasePresenter;

/**
 * Factory to implement to create a presenter
 *
 * @author Benoit LETONDOR
 */
public interface PresenterFactory<T extends BasePresenter>
{
    @NonNull
    T create();
}
