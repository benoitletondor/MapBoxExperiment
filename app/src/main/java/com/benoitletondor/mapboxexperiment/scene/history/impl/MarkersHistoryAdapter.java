package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.map.MapMarker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the HistoryView recycler view
 *
 * @author Benoit LETONDOR
 */
public final class MarkersHistoryAdapter extends RecyclerView.Adapter<MarkersHistoryAdapter.MarkerViewHolder>
{
    /**
     * List of markers to display
     */
    @NonNull
    private final List<MapMarker> mMarkers;
    /**
     * Weak reference to the click listener
     */
    @NonNull
    private final WeakReference<MarkerClickedListener> mListener;

// -------------------------------------->

    MarkersHistoryAdapter(@NonNull List<MapMarker> markers, @NonNull MarkerClickedListener listener)
    {
        mListener = new WeakReference<>(listener);
        mMarkers = new ArrayList<>(markers);
    }

// -------------------------------------->

    @Override
    public MarkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new MarkerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_marker, parent, false));
    }

    @Override
    public void onBindViewHolder(MarkerViewHolder markerViewHolder, int position)
    {
        final MapMarker marker = mMarkers.get(position);

        markerViewHolder.mNameTextView.setText(marker.getName());
        markerViewHolder.mPositionTextView.setText(markerViewHolder.itemView.getContext().getString(
            R.string.cell_history_position, marker.getLatitude(), marker.getLongitude()));

        markerViewHolder.bind(marker, mListener);
    }

    @Override
    public int getItemCount()
    {
        return mMarkers.size();
    }

// -------------------------------------->

    static final class MarkerViewHolder extends RecyclerView.ViewHolder
    {
        @NonNull
        private final TextView mNameTextView;
        @NonNull
        private final TextView mPositionTextView;

        MarkerViewHolder(View itemView)
        {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.cell_history_marker_name);
            mPositionTextView = (TextView) itemView.findViewById(R.id.cell_history_position);
        }

        void bind(@NonNull final MapMarker marker, @NonNull final WeakReference<MarkerClickedListener> listener)
        {
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if( listener.get() != null )
                    {
                        listener.get().onMarkerClicked(marker);
                    }
                }
            });
        }
    }

    public interface MarkerClickedListener
    {
        void onMarkerClicked(@NonNull MapMarker marker);
    }
}
