package com.benoitletondor.mapboxexperiment.scene.main;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;

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

    /**
     * Show the history view
     */
    void showHistoryView();

    /**
     * Take the user back to the home view and focus on the given marker
     *
     * @param marker the marker to display
     */
    void showHomeToMarker(@NonNull MapMarker marker);
}
