package com.benoitletondor.mapboxexperiment.scene.history;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.scene.history.impl.MarkersHistoryAdapter;

/**
 * The history view that displays a list of previously pinned location
 *
 * @author Benoit LETONDOR
 */
@UiThread
public interface HistoryView extends MarkersHistoryAdapter.MarkerClickedListener
{
    /**
     * Set the title of the view as history
     */
    void setHistoryViewTitle();

    /**
     * Show the loading view and hide the content
     */
    void showLoadingView();

    /**
     * Show the content and hide the loading view
     */
    void showContentView();

    /**
     * Set the recycler view adapter to display markers
     *
     * @param adapter the adapter
     */
    void setHistoryMarkersAdapter(@NonNull RecyclerView.Adapter adapter);

    /**
     * Show an error to the user indicating an error occurred loading data
     */
    void showLoadingMarkersError();

    /**
     * Take the user back to the map view and focus on the given marker
     *
     * @param marker marker to display
     */
    void showMapAndFocusToMarker(@NonNull MapMarker marker);
}
