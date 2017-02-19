package com.benoitletondor.mapboxexperiment.common;

/**
 * Interface that defines a view that can intercept an onBackPressed trigger. This can be used by
 * fragments to intercept on back pressed
 *
 * @author Benoit LETONDOR
 */
public interface OnBackPressedInterceptor
{
    /**
     * Called when back button is pressed
     *
     * @return true if the event has been consumed, false otherwise
     */
    boolean onBackPressed();
}
