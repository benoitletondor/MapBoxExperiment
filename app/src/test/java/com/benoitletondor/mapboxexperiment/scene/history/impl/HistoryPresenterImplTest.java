package com.benoitletondor.mapboxexperiment.scene.history.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.benoitletondor.mapboxexperiment.RxSchedulersOverrideRule;
import com.benoitletondor.mapboxexperiment.interactor.MarkerStorageInteractor;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryPresenter;
import com.benoitletondor.mapboxexperiment.scene.history.HistoryView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link HistoryPresenterImpl} and {@link HistoryView}
 *
 * @author Benoit LETONDOR
 */
@RunWith(MockitoJUnitRunner.class)
public final class HistoryPresenterImplTest
{
    @Rule
    public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @NonNull
    private static final List<MarkerStorageInteractor.StoredMarker> markers;
    static
    {
        markers = new ArrayList<>();

        for(int i=0; i<10; i++)
        {
            markers.add(new MarkerStorageInteractor.StoredMarker(i, i, "name" + i, "caption" + i));
        }
    }

    @Mock
    MarkerStorageInteractor mMarkerStorageInteractorMock;

// --------------------------------->

    @Test
    public void testInitialStateIsCorrectTriggersLoadData()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.<List<MarkerStorageInteractor.StoredMarker>>empty());

        final HistoryPresenterImpl presenter = createPresenter();
        final HistoryView view = createAndSpyViewForPresenter(presenter);

        // Test that view title is called
        Mockito.verify(view).setHistoryViewTitle();

        // Test that load markers is called
        Mockito.verify(presenter).loadMarkers();

        // Test that view loading is shown
        Mockito.verify(view).showLoadingView();

        // Test that other methods aren't called
        Mockito.verify(view, Mockito.never()).showContentView();
        Mockito.verify(view, Mockito.never()).setHistoryMarkersAdapter(Mockito.any(RecyclerView.Adapter.class));
    }

    @Test
    public void testDataLoadAndDisplay()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.just(markers));

        final HistoryPresenterImpl presenter = createPresenter();
        final HistoryView view = createAndSpyViewForPresenter(presenter);

        // Ensure that data is load correctly
        assertEquals(HistoryPresenterImpl.State.LOAD, presenter.mState);
        assertNotNull(presenter.mMarkers);
        assertEquals(markers.size(), presenter.mMarkers.size());

        // Test that view loading is shown
        Mockito.verify(view).showLoadingView();
        Mockito.verify(view).showContentView();
        Mockito.verify(view).setHistoryMarkersAdapter(Mockito.any(MarkersHistoryAdapter.class));
    }

    @Test
    public void testLoadDataFailure()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.<List<MarkerStorageInteractor.StoredMarker>>error(new Exception("Stub")));

        final HistoryPresenterImpl presenter = createPresenter();
        final HistoryView view = createAndSpyViewForPresenter(presenter);

        // Ensure that data is load correctly
        assertEquals(HistoryPresenterImpl.State.CREATED, presenter.mState);
        assertNull(presenter.mMarkers);

        // Test that view is showing loading and error message
        Mockito.verify(view).showLoadingView();
        Mockito.verify(view).showLoadingMarkersError();

        // Ensure the rest is not called
        Mockito.verify(view, Mockito.never()).showContentView();
        Mockito.verify(view, Mockito.never()).setHistoryMarkersAdapter(Mockito.any(MarkersHistoryAdapter.class));
    }

    public void testForwardMarkerClickToView()
    {
        Mockito
            .when(mMarkerStorageInteractorMock.retrieveStoredMarkers())
            .thenReturn(Observable.<List<MarkerStorageInteractor.StoredMarker>>empty());

        final HistoryPresenterImpl presenter = createPresenter();
        final HistoryView view = createAndSpyViewForPresenter(presenter);

        final MarkerStorageInteractor.StoredMarker marker = markers.get(0);

        presenter.onMarkerClicked(marker);

        Mockito.verify(view).onMarkerClicked(marker);
    }

// --------------------------------->

    @NonNull
    private HistoryPresenterImpl createPresenter()
    {
        final HistoryPresenterImpl presenter = Mockito.spy(new HistoryPresenterImpl(mMarkerStorageInteractorMock));
        assertEquals(HistoryPresenterImpl.State.CREATED, presenter.mState);

        return presenter;
    }

    @NonNull
    private HistoryView createAndSpyViewForPresenter(@NonNull HistoryPresenter presenter, boolean start)
    {
        final HistoryView view = Mockito.spy(HistoryView.class);
        presenter.onViewAttached(view);

        if( start )
        {
            presenter.onStart(true);
        }

        return view;
    }

    @NonNull
    private HistoryView createAndSpyViewForPresenter(@NonNull HistoryPresenter presenter)
    {
        return createAndSpyViewForPresenter(presenter, true);
    }

}
