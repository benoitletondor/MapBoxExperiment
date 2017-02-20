package com.benoitletondor.mapboxexperiment.scene.main.impl;

import android.support.annotation.NonNull;

import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link MainPresenterImpl} and {@link MainView}
 *
 * @author Benoit LETONDOR
 */
@RunWith(MockitoJUnitRunner.class)
public final class MainPresenterImplTest
{

    @Test
    public void testHittingHomeMenuButtonShowsHome()
    {
        final MainPresenterImpl presenter = createPresenter();
        final MainView view = createAndSpyViewForPresenter(presenter);

        presenter.onHomeButtonClicked();

        Mockito.verify(view).showHomeView();
    }

    @Test
    public void testHittingHistoryMenuButtonShowsHistory()
    {
        final MainPresenterImpl presenter = createPresenter();
        final MainView view = createAndSpyViewForPresenter(presenter);

        presenter.onHistoryButtonClicked();

        Mockito.verify(view).showHistoryView();
    }

// --------------------------------->

    @NonNull
    private MainPresenterImpl createPresenter()
    {
        return Mockito.spy(new MainPresenterImpl());
    }

    @NonNull
    private MainView createAndSpyViewForPresenter(@NonNull MainPresenter presenter, boolean start)
    {
        final MainView view = Mockito.spy(MainView.class);
        presenter.onViewAttached(view);

        if( start )
        {
            presenter.onStart(true);
        }

        return view;
    }

    @NonNull
    private MainView createAndSpyViewForPresenter(@NonNull MainPresenter presenter)
    {
        return createAndSpyViewForPresenter(presenter, true);
    }
}
