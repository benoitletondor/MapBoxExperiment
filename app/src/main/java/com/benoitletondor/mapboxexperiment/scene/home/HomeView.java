package com.benoitletondor.mapboxexperiment.scene.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.benoitletondor.mapboxexperiment.common.OnBackPressedInterceptor;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

@UiThread
public interface HomeView extends BaseMapView, OnBackPressedInterceptor
{
    /**
     * Show a message to the user indicating an error occurred while getting device location
     *
     * @param errorDescription optional description of the error
     */
    void showLocationNotAvailable(@Nullable String errorDescription);

    /**
     * Show a message to the user after he denied the location permission
     */
    void showLocationPermissionDeniedDisclaimer();

    /**
     * Show a message to the user indicating an error occurred while loading the map
     *
     * @param message the error message
     */
    void showMapLoadingError(@Nullable String message);

    /**
     * Clear the focus on the search bar
     */
    void clearSearchBarFocus();

    /**
     * Remove the search bar content
     */
    void clearSearchBarContent();

    /**
     * Set the content of the search bar
     *
     * @param content the search bar content
     */
    void setSearchBarContent(@NonNull String content);

    /**
     * Hide the keyboard if shown
     */
    void hideKeyboard();

    /**
     * Disable the search bar
     */
    void disableSearchBar();

    /**
     * Enable the search bar
     */
    void enableSearchBar();

    /**
     * Set the validate icon to the add location FAB
     */
    void setAddLocationFABValidateIcon();

    /**
     * Set the add location icon to the add location FAB
     */
    void setAddLocationFABAddLocationIcon();

    /**
     * Hide the marker at the center of the map
     */
    void hideCenterMapMarker();

    /**
     * Show the marker at the center of the map
     */
    void showCenterMapMarker();

    /**
     * Set search bar hint to default
     */
    void setSearchBarDefaultHint();

    /**
     * Set search bar hint to searching
     */
    void setSearchBarSearchingHint();

    /**
     * Set the view title to default
     */
    void setDefaultViewTitle();

    /**
     * Set the view title to add location
     */
    void setAddLocationViewTitle();
}
