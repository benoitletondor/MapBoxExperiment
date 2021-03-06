package com.benoitletondor.mapboxexperiment.scene.home.impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.LocationAutoCompleteSearchBar;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
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
import com.benoitletondor.mapboxexperiment.scene.main.MainView;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.services.android.geocoder.ui.GeocoderAdapter;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;

import javax.inject.Inject;

/**
 * Implementation of the {@link HomeView}
 *
 * @author Benoit LETONDOR
 */
public final class HomeFragment extends BaseMapFragment<HomePresenter, HomeView> implements HomeView
{
    /**
     * Presenter factory, used by MVP
     */
    @Inject
    PresenterFactory<HomePresenter> mPresenterFactory;
    /**
     * The instance of {@link MainView} this fragment is currently attached to. Will be null if detached
     */
    @Nullable
    private MainView mMainView;
    /**
     * The progress dialog shown to indicate a location is being saved to the user. Will be null is not shown
     */
    @Nullable
    private ProgressDialog mSavingLocationProgressDialog;

    /**
     * Search bar XML element
     */
    private LocationAutoCompleteSearchBar<MapboxAutocompleteLocationItem, MapboxGeocoderAdapter> mSearchBar;
    /**
     * Add location FAB XML element
     */
    private FloatingActionButton mAddLocationFAB;
    /**
     * Center of map marker ImageView XML element
     */
    private ImageView mCenterMapMarkerImageView;

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
        mSearchBar = (LocationAutoCompleteSearchBar<MapboxAutocompleteLocationItem, MapboxGeocoderAdapter>) view.findViewById(R.id.home_fragment_search_bar);
        final GeocoderAdapter adapter = new GeocoderAdapter(view.getContext().getApplicationContext());
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

        mCenterMapMarkerImageView = (ImageView) view.findViewById(R.id.home_fragment_map_center_marker);
    }

    @Override
    public void onDestroyView()
    {
        mSearchBar = null;
        mAddLocationFAB = null;
        mCenterMapMarkerImageView = null;

        if( mSavingLocationProgressDialog != null )
        {
            mSavingLocationProgressDialog.dismiss();
            mSavingLocationProgressDialog = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if( context instanceof MainView )
        {
            mMainView = (MainView) context;
        }
    }

    @Override
    public void onDetach()
    {
        mMainView = null;

        super.onDetach();
    }

    @Override
    public boolean onBackPressed()
    {
        return mPresenter != null && mPresenter.onBackPressed();
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
    public void setSearchBarContent(@NonNull String content)
    {
        mSearchBar.setText(content);
    }

    @NonNull
    @Override
    public String formatAddress(@NonNull Address address)
    {
        final StringBuilder builder = new StringBuilder();

        final String addressLine = address.getAddressLine(0);
        if( addressLine != null )
        {
            builder.append(address.getAddressLine(0).substring(0, addressLine.indexOf(",")));
        }

        if( address.getPostalCode() != null )
        {
            if( builder.length() > 0 )
            {
                builder.append(", ");
            }

            builder.append(address.getPostalCode());
        }

        if( address.getLocality() != null )
        {
            if( builder.length() > 0 )
            {
                builder.append(", ");
            }

            builder.append(address.getLocality());
        }

        return builder.toString();
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

    @Override
    public void disableSearchBar()
    {
        mSearchBar.setEnabled(false);
    }

    @Override
    public void enableSearchBar()
    {
        mSearchBar.setEnabled(true);
        mSearchBar.setMaxLines(2);
    }

    @Override
    public void enableSearchBarMultilineDisplay()
    {
        mSearchBar.setMaxLines(3);
        mSearchBar.setSingleLine(false);
    }

    @Override
    public void disableSearchBarMultilineDisplay()
    {
        mSearchBar.setMaxLines(1);
        mSearchBar.setSingleLine(true);
    }

    @Override
    public void setAddLocationFABValidateIcon()
    {
        // To avoid making change if content is already as we want
        final int targetColor = ContextCompat.getColor(getContext(), R.color.add_location_fab_validate_color);
        if( mAddLocationFAB.getBackgroundTintList() != null && mAddLocationFAB.getBackgroundTintList().getDefaultColor() == targetColor )
        {
            return;
        }

        mAddLocationFAB.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_check_white_24dp, getContext().getTheme()));
        mAddLocationFAB.setBackgroundTintList(ColorStateList.valueOf(targetColor));

        mAddLocationFAB.setScaleX(0);
        mAddLocationFAB.setScaleY(0);
        ViewCompat.animate(mAddLocationFAB)
            .scaleX(1)
            .scaleY(1)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .start();
    }

    @Override
    public void setAddLocationFABAddLocationIcon()
    {
        // To avoid making change if content is already as we want
        final int targetColor = ContextCompat.getColor(getContext(), R.color.add_location_fab_default_color);
        if( mAddLocationFAB.getBackgroundTintList() != null && mAddLocationFAB.getBackgroundTintList().getDefaultColor() == targetColor )
        {
            return;
        }

        mAddLocationFAB.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_add_location_white_24dp, getContext().getTheme()));
        mAddLocationFAB.setBackgroundTintList(ColorStateList.valueOf(targetColor));

        mAddLocationFAB.setScaleX(0);
        mAddLocationFAB.setScaleY(0);
        ViewCompat.animate(mAddLocationFAB)
            .scaleX(1)
            .scaleY(1)
            .setDuration(250)
            .setInterpolator(new OvershootInterpolator())
            .start();
    }

    @Override
    public void hideCenterMapMarker()
    {
        mCenterMapMarkerImageView.setVisibility(View.GONE);
    }

    @Override
    public void showCenterMapMarker()
    {
        mCenterMapMarkerImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSearchBarDefaultHint()
    {
        mSearchBar.setHint(R.string.search_location_hint);
    }

    @Override
    public void setSearchBarSearchingHint()
    {
        mSearchBar.setHint(R.string.searching_hint);
    }

    @Override
    public void setDefaultViewTitle()
    {
        if( mMainView != null )
        {
            mMainView.setViewTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void setAddLocationViewTitle()
    {
        if( mMainView != null )
        {
            mMainView.setViewTitle(getString(R.string.add_location_view_title));
        }
    }

    @Override
    public void showSavingLocationModal()
    {
        mSavingLocationProgressDialog = ProgressDialog.show(
            getContext(),
            getString(R.string.saving_location),
            getString(R.string.please_wait),
            true,
            false);
    }

    @Override
    public void hideSavingLocationModal()
    {
        if( mSavingLocationProgressDialog != null )
        {
            mSavingLocationProgressDialog.dismiss();
            mSavingLocationProgressDialog = null;
        }
    }

    @Override
    public void showSavingLocationError()
    {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.save_location_error_title)
            .setMessage(R.string.save_location_error_message)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    public void focusToMarker(@NonNull MapMarker marker)
    {
        if( mPresenter != null )
        {
            mPresenter.onMarkerToFocus(marker);
        }
    }
}
