package com.benoitletondor.mapboxexperiment.interactor.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benoitletondor.mapboxexperiment.common.map.MapMarker;
import com.benoitletondor.mapboxexperiment.common.mvp.interactor.impl.BaseInteractorImpl;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;

/**
 * Implementation of the {@link MarkerStorageInteractor} using JSON object serialisation.
 * FIXME this is a kinda naive implementation, far from optimal both in speed and maintainability
 *
 * @author Benoit LETONDOR
 */
public final class MarkerStorageInteractorImpl extends BaseInteractorImpl implements MarkerStorageInteractor
{
    /**
     * Name of the sharing preferences containing the saved markers
     */
    private final static String MARKER_SHARED_PREFERENCES_NAME = "marker_sp";

    /**
     * Key of the list of markers into the {@link #mMarkerSharedPreferences}
     */
    private final static String MARKER_LIST_KEY = "marker_list";

    /**
     * Key of the latitude data into a marker JSON
     */
    private final static String LATITUDE_KEY = "latitude";
    /**
     * Key of the longitude data into a marker JSON
     */
    private final static String LONGITUDE_KEY = "longitude";
    /**
     * Key of the name data into a marker JSON
     */
    private final static String NAME_KEY = "name";
    /**
     * Key of the caption data into a marker JSON
     */
    private final static String CAPTION_KEY = "caption";

    /**
     * The shared preferences containing stored markers
     */
    @NonNull
    private final SharedPreferences mMarkerSharedPreferences;

// ------------------------------------->

    public MarkerStorageInteractorImpl(@NonNull Context context)
    {
        mMarkerSharedPreferences = context.getSharedPreferences(MARKER_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

// ------------------------------------->

    @NonNull
    @Override
    public Observable<List<StoredMarker>> retrieveStoredMarkers()
    {
        return Observable.fromCallable(new Callable<List<StoredMarker>>()
        {
            @Override
            public List<StoredMarker> call() throws Exception
            {
                final String serializedData = mMarkerSharedPreferences.getString(MARKER_LIST_KEY, null);
                if( serializedData == null )
                {
                    return new ArrayList<>(0);
                }

                return markersFromJSON(new JSONArray(serializedData));
            }
        });
    }

    @Override
    public Completable storeMarkers(@NonNull final List<MapMarker> markers)
    {
        return Completable.fromAction(new Action()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void run() throws Exception
            {
                if( !mMarkerSharedPreferences
                    .edit()
                    .putString(MARKER_LIST_KEY, markersToJson(markers).toString())
                    .commit() )
                {
                    throw new IOException("Unable to store markers");
                }
            }
        });
    }

// ------------------------------------->

    /**
     * Deserialize markers from json
     *
     * @param jsonArray an array of serialized markers
     * @return a list of {@link StoredMarker}
     * @throws JSONException on error de-serializing data
     */
    @NonNull
    private static List<StoredMarker> markersFromJSON(@NonNull JSONArray jsonArray) throws JSONException
    {
        final List<StoredMarker> markers = new ArrayList<>(jsonArray.length());

        for(int i = 0; i<jsonArray.length(); i++)
        {
            markers.add(markerFromJSON(jsonArray.getJSONObject(i)));
        }

        return markers;
    }

    /**
     * Deserialize a marker from its JSON representation
     *
     * @param jsonObject json representation of a marker
     * @return a {@link StoredMarker}
     * @throws JSONException on error de-serializing marker
     */
    @NonNull
    private static StoredMarker markerFromJSON(@NonNull JSONObject jsonObject) throws JSONException
    {
        return new StoredMarker(
            jsonObject.getDouble(LATITUDE_KEY),
            jsonObject.getDouble(LONGITUDE_KEY),
            jsonObject.isNull(NAME_KEY) ? null : jsonObject.getString(NAME_KEY),
            jsonObject.isNull(CAPTION_KEY) ? null : jsonObject.getString(CAPTION_KEY)
        );
    }

    /**
     * Serialize a list of markers to JSON
     *
     * @param markers the markers to serialize
     * @return an array of markers JSON representation
     * @throws JSONException on error serializing data
     */
    @NonNull
    private static JSONArray markersToJson(@NonNull List<MapMarker> markers) throws JSONException
    {
        final JSONArray jsonArray = new JSONArray();

        for(MapMarker marker : markers)
        {
            jsonArray.put(markerToJson(marker));
        }

        return jsonArray;
    }

    /**
     * Serialize a marker to JSON
     *
     * @param marker the marker to serialize
     * @return a JSON representation of the marker
     * @throws JSONException on error serializing marker
     */
    @NonNull
    private static JSONObject markerToJson(@NonNull MapMarker marker) throws JSONException
    {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(LATITUDE_KEY, marker.getLatitude());
        jsonObject.put(LONGITUDE_KEY, marker.getLongitude());
        jsonObject.put(NAME_KEY, marker.getName() != null ? marker.getName() : JSONObject.NULL);
        jsonObject.put(CAPTION_KEY, marker.getCaption() != null ? marker.getCaption() : JSONObject.NULL);

        return jsonObject;
    }
}
