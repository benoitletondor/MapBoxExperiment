package com.benoitletondor.mapboxexperiment.scene.main;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

@UiThread
public interface MainView
{
    void setViewTitle(@NonNull String title);
}
