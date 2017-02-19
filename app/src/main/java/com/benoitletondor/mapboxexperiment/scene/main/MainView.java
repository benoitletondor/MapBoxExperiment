package com.benoitletondor.mapboxexperiment.scene.main;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * Main view that contains all other views
 *
 * @author Benoit LETONDOR
 */
@UiThread
public interface MainView
{
    /**
     * Set the title of this view
     *
     * @param title the view title
     */
    void setViewTitle(@NonNull String title);

    /**
     * Show the home view
     */
    void showHomeView();
}
