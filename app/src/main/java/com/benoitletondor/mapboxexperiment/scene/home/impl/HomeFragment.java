package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.LocationAutoCompleteSearchBar;
import com.benoitletondor.mapboxexperiment.common.map.MapViewFragment;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseMapFragment;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.mapbox.MapBoxFragment;
import com.benoitletondor.mapboxexperiment.mapbox.MapboxAutocompleteLocationItem;
import com.benoitletondor.mapboxexperiment.mapbox.MapboxGeocoderAdapter;
import com.benoitletondor.mapboxexperiment.scene.home.HomePresenter;
import com.benoitletondor.mapboxexperiment.scene.home.HomeView;
import com.benoitletondor.mapboxexperiment.scene.home.injection.DaggerHomeViewComponent;
import com.benoitletondor.mapboxexperiment.scene.home.injection.HomeViewModule;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.services.android.geocoder.ui.GeocoderAdapter;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;

import javax.inject.Inject;

public final class HomeFragment extends BaseMapFragment<HomePresenter, HomeView> implements HomeView
{
    @Inject
    PresenterFactory<HomePresenter> mPresenterFactory;

    private LocationAutoCompleteSearchBar<MapboxAutocompleteLocationItem> mSearchBar;
    private FloatingActionButton mAddLocationFAB;

// ---------------------------------->

    public HomeFragment()
    {
        super(R.id.home_fragment_map_container);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // TODO find a more elegant way to ensure LocationAutoCompleteSearchBar generic type
        mSearchBar = (LocationAutoCompleteSearchBar<MapboxAutocompleteLocationItem>) view.findViewById(R.id.home_fragment_search_bar);
        final GeocoderAdapter adapter = new GeocoderAdapter(view.getContext());
        adapter.setAccessToken(MapboxAccountManager.getInstance().getAccessToken());
        adapter.setType(GeocodingCriteria.TYPE_PLACE);
        mSearchBar.setAdapter(new MapboxGeocoderAdapter(adapter));

        mSearchBar.setOnLocationClickedListener(new LocationAutoCompleteSearchBar.OnLocationClickedListener<MapboxAutocompleteLocationItem>()
        {
            @Override
            public void onLocationClicked(@NonNull MapboxAutocompleteLocationItem locationItem)
            {
                if( mPresenter != null )
                {
                    mPresenter.onLocationSearchEntered(locationItem);
                }
            }
        });

        mAddLocationFAB = (FloatingActionButton) view.findViewById(R.id.home_fragment_add_location_fab);
        mAddLocationFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if( mPresenter != null )
                {
                    mPresenter.onAddLocationFABClicked();
                }
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        mSearchBar = null;
        mAddLocationFAB = null;

        super.onDestroyView();
    }

// ---------------------------------->

    @NonNull
    @Override
    public MapViewFragment createMapView()
    {
        return new MapBoxFragment();
    }

    @Override
    protected PresenterFactory<HomePresenter> getPresenterFactory()
    {
        return mPresenterFactory;
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent)
    {
        DaggerHomeViewComponent.builder()
            .appComponent(appComponent)
            .homeViewModule(new HomeViewModule())
            .build()
            .inject(this);
    }

// ---------------------------------->

    @Override
    public void showLocationNotAvailable(@Nullable String errorDescription)
    {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.no_location_error_title)
            .setMessage(getString(R.string.no_location_error_message, errorDescription))
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    @Override
    public void showLocationPermissionDeniedDisclaimer()
    {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.location_permission_denied_title)
            .setMessage(R.string.location_permission_denied_message)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    @Override
    public void showMapLoadingError(@Nullable String message)
    {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.map_loading_error_title)
            .setMessage(getString(R.string.map_loading_error_message, message))
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    @Override
    public void clearSearchBarFocus()
    {
        mSearchBar.clearFocus();
    }

    @Override
    public void clearSearchBarContent()
    {
        mSearchBar.setText("");
    }

    @Override
    public void hideKeyboard()
    {
        if( getActivity() == null || getContext() == null )
        {
            return;
        }

        final View view = getActivity().getCurrentFocus();
        if ( view != null )
        {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
