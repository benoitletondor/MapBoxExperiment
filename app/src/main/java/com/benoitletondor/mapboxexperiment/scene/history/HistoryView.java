package com.benoitletondor.mapboxexperiment.scene.history;

import android.support.annotation.UiThread;

/**
 * The history view that displays a list of previously pinned location
 *
 * @author Benoit LETONDOR
 */
@UiThread
public interface HistoryView
{
    /**
     * Set the title of the view as history
     */
    void setHistoryViewTitle();
}
