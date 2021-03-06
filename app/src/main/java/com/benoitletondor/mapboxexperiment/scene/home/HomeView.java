package com.benoitletondor.mapboxexperiment.scene.home;

import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.benoitletondor.mapboxexperiment.common.OnBackPressedInterceptor;
import com.benoitletondor.mapboxexperiment.common.mvp.view.BaseMapView;

/**
 * The home view that displays a map and handles map interactions such as searching for an address
 * or adding a pin
 *
 * @author Benoit LETONDOR
 */
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
     * @param content the content to display
     */
    void setSearchBarContent(@NonNull String content);

    /**
     * Format the given address for display. The targeted format is:
     * <ul>
     *     <li>Street number and street name</li>
     *     <li>Postal code</li>
     *     <li>City</li>
     * </ul>
     *
     * @param address the address to format
     * @return a human readable version of this address
     */
    @NonNull
    String formatAddress(@NonNull Address address);

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
     * Enable the display of multiline content into the search bar
     */
    void enableSearchBarMultilineDisplay();

    /**
     * Disable the display of multiline content into the search bar
     */
    void disableSearchBarMultilineDisplay();

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

    /**
     * Show a modal indicating to the user that the current location is being saved
     */
    void showSavingLocationModal();

    /**
     * Hide the saving location modal
     */
    void hideSavingLocationModal();

    /**
     * Show an error indicating to the user the location failed to be saved
     */
    void showSavingLocationError();
}
