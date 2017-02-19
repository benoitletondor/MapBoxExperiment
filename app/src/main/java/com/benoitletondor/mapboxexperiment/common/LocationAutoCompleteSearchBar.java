package com.benoitletondor.mapboxexperiment.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.map.AutoCompleteLocationItem;

/**
 * An implementation of AutoCompleteTextView made for location search with auto completion. Greatly
 * inspired by Mapbox GeocoderAutoCompleteView but with a more generic interface and a better handling
 * of the reset content button.
 *
 * @param <L> type of item returned by the adapter.
 */
public class LocationAutoCompleteSearchBar<L extends AutoCompleteLocationItem, T extends ListAdapter & Filterable> extends AutoCompleteTextView implements TextWatcher
{
    /**
     * Listener for location click, can be null
     */
    @Nullable
    private OnLocationClickedListener<L> onLocationClickedListener;
    /**
     * Drawable used as clear content button
     */
    @NonNull
    private final Drawable mClearButtonDrawable;
    /**
     * Saved adapter stored while view is disabled to avoid sending request
     */
    @Nullable
    private T mTempDisabledAdapter;

// ---------------------------------->

    @SuppressWarnings("unchecked")
    public LocationAutoCompleteSearchBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Set click listener
        setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // TODO implement a way to ensure adapter is of the right type
                L result = (L) getAdapter().getItem(position);

                // Notify subscribers
                if (onLocationClickedListener != null)
                {
                    onLocationClickedListener.onLocationClicked(result);
                }
            }
        });

        // Add clear button to autocomplete
        mClearButtonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_clear_black);

        setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                LocationAutoCompleteSearchBar et = (LocationAutoCompleteSearchBar) view;
                if (et.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > et.getWidth() - et.getPaddingRight() - mClearButtonDrawable.getIntrinsicWidth()) {
                    setText("");
                }
                return false;
            }
        });

        if( getText().length() != 0 && isEnabled() )
        {
            showClearButton();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setEnabled(boolean enabled)
    {


        super.setEnabled(enabled);

        // Remove the clear button when disabled and remove any adapter to avoid triggering useless searches
        if( !enabled )
        {
            hideClearButton();

            if( getAdapter() != null )
            {
                mTempDisabledAdapter = (T) getAdapter();
                setAdapter(null);
            }
        }
        // Put them back when enabled
        else
        {
            showClearButton();

            if( mTempDisabledAdapter != null )
            {
                setAdapter(mTempDisabledAdapter);
                mTempDisabledAdapter = null;
            }
        }
    }

// -------------------------------->

    private void hideClearButton()
    {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void showClearButton()
    {
        setCompoundDrawablesWithIntrinsicBounds(null, null, mClearButtonDrawable, null);
    }

// -------------------------------->

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
        if( getText().length() == 0 )
        {
            hideClearButton();
        }
        else if( isEnabled() )
        {
            showClearButton();
        }
    }

    @Override
    public void afterTextChanged(Editable editable)
    {

    }

// -------------------------------->

    /**
     * Set the listener that will be call when an auto completed suggestion is clicked by the user
     *
     * @param onLocationClickedListener the listener
     */
    public void setOnLocationClickedListener(@Nullable OnLocationClickedListener<L> onLocationClickedListener)
    {
        this.onLocationClickedListener = onLocationClickedListener;
    }

    /**
     * Defines a listener for location click
     *
     * @param <L> the type of location item
     */
    public interface OnLocationClickedListener<L extends AutoCompleteLocationItem>
    {
        void onLocationClicked(@NonNull L locationItem);
    }
}
