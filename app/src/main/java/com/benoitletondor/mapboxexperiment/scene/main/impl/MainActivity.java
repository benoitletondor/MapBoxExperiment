package com.benoitletondor.mapboxexperiment.scene.main.impl;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.benoitletondor.mapboxexperiment.R;
import com.benoitletondor.mapboxexperiment.common.OnBackPressedInterceptor;
import com.benoitletondor.mapboxexperiment.common.mvp.presenter.loader.PresenterFactory;
import com.benoitletondor.mapboxexperiment.common.mvp.view.impl.BaseActivity;
import com.benoitletondor.mapboxexperiment.injection.AppComponent;
import com.benoitletondor.mapboxexperiment.scene.home.impl.HomeFragment;
import com.benoitletondor.mapboxexperiment.scene.main.MainPresenter;
import com.benoitletondor.mapboxexperiment.scene.main.MainView;
import com.benoitletondor.mapboxexperiment.scene.main.injection.DaggerMainViewComponent;
import com.benoitletondor.mapboxexperiment.scene.main.injection.MainViewModule;

import javax.inject.Inject;

/**
 * Implementation of the {@link MainView}
 *
 * @author Benoit LETONDOR
 */
public final class MainActivity extends BaseActivity<MainPresenter, MainView> implements MainView, NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener
{
    private final static int FRAGMENT_CONTAINER = R.id.activity_main_fragment_container;

    /**
     * Presenter factory, used by MVP
     */
    @Inject
    PresenterFactory<MainPresenter> mPresenterFactory;

    /**
     * Drawer layout from XML
     */
    private DrawerLayout mDrawerLayout;
    /**
     * The navigation view from {@link #mDrawerLayout}
     */
    private NavigationView mNavigationView;
    /**
     * The drawer toggle of {@link #mDrawerLayout}
     */
    private ActionBarDrawerToggle mDrawerToggle;

// -------------------------------->

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        final Fragment activeFragment = getActiveFragment();
        if( activeFragment == null )
        {
            setActiveFragment(new HomeFragment(), false);
        }
        else
        {
            onActiveFragmentChanged(activeFragment);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //noinspection SimplifiableIfStatement
        if ( mDrawerToggle.onOptionsItemSelected(item) )
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if( mDrawerLayout.isDrawerOpen(GravityCompat.START) )
        {
            closeDrawer();
            return;
        }

        // Send on back pressed event to the fragment displayed
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment_container);
        if( fragment != null && fragment instanceof OnBackPressedInterceptor)
        {
            if( ((OnBackPressedInterceptor) fragment).onBackPressed() )
            {
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);

        super.onDestroy();
    }

// -------------------------------->

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent)
    {
        DaggerMainViewComponent.builder()
            .appComponent(appComponent)
            .mainViewModule(new MainViewModule())
            .build()
            .inject(this);
    }

    @Override
    protected PresenterFactory<MainPresenter> getPresenterFactory()
    {
        return mPresenterFactory;
    }

// -------------------------------->

    @Override
    public void setViewTitle(@NonNull String title)
    {
        setTitle(title);
    }

    @Override
    public void showHomeView()
    {
        final Fragment activeFragment = getActiveFragment();
        if( activeFragment != null && activeFragment instanceof HomeFragment )
        {
            return;
        }

        if( getSupportFragmentManager().getBackStackEntryCount() > 0 )
        {
            getSupportFragmentManager().popBackStack();
        }
        else
        {
            setActiveFragment(new HomeFragment(), false);
        }
    }

// -------------------------------->

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        closeDrawer();

        if( mPresenter == null )
        {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.navigation_home:
                mPresenter.onHomeButtonClicked();
                break;
        }

        return true;
    }

    @Override
    public void onBackStackChanged()
    {
        final Fragment fragment = getActiveFragment();
        if( fragment != null )
        {
            onActiveFragmentChanged(fragment);
        }
    }

    /**
     * Called when the active fragment changes
     *
     * @param fragment the new fragment
     */
    private void onActiveFragmentChanged(@NonNull Fragment fragment)
    {
        if( fragment instanceof HomeFragment )
        {
            mNavigationView.setCheckedItem(R.id.navigation_home);
        }
    }

    @Nullable
    private Fragment getActiveFragment()
    {
        return getSupportFragmentManager().findFragmentById(FRAGMENT_CONTAINER);
    }

    private void setActiveFragment(@NonNull Fragment fragment, boolean addToBackstack)
    {
        final FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction()
            .replace(FRAGMENT_CONTAINER, fragment);

        if( addToBackstack )
        {
            transaction.addToBackStack(fragment.getClass().getName());
        }

        transaction.commit();

        onActiveFragmentChanged(fragment);
    }

    private void closeDrawer()
    {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
