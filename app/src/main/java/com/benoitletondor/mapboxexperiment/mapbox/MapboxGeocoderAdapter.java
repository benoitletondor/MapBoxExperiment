package com.benoitletondor.mapboxexperiment.mapbox;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.mapbox.services.android.geocoder.ui.GeocoderAdapter;

/**
 * Wrapper around {@link GeocoderAdapter} that returns a {@link MapboxAutocompleteLocationItem} as
 * item, to use with {@link com.benoitletondor.mapboxexperiment.common.LocationAutoCompleteSearchBar}
 *
 * @author Benoit LETONDOR
 */
public final class MapboxGeocoderAdapter extends BaseAdapter implements Filterable
{
    @NonNull
    private final GeocoderAdapter mAdapter;

// ----------------------------------->

    public MapboxGeocoderAdapter(@NonNull GeocoderAdapter adapter)
    {
        mAdapter = adapter;
    }

// ----------------------------------->

    @Override
    public int getCount()
    {
        return mAdapter.getCount();
    }

    @Override
    public MapboxAutocompleteLocationItem getItem(int i)
    {
        return new MapboxAutocompleteLocationItem(mAdapter.getItem(i));
    }

    @Override
    public long getItemId(int i)
    {
        return mAdapter.getItemId(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        return mAdapter.getView(i, view, viewGroup);
    }

    @Override
    public Filter getFilter()
    {
        return mAdapter.getFilter();
    }
}
