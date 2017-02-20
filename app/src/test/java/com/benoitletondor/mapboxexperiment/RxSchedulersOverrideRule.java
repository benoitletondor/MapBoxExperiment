package com.benoitletondor.mapboxexperiment;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * A JUnit rule that override RX schedulers to perform all work on the current thread
 *
 * @author Benoit LETONDOR
 */
public final class RxSchedulersOverrideRule implements TestRule
{
    @Override
    public Statement apply(final Statement base, Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                RxAndroidPlugins.reset();
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>()
                {
                    @Override
                    public Scheduler apply(@io.reactivex.annotations.NonNull Callable<Scheduler> schedulerCallable) throws Exception
                    {
                        return Schedulers.trampoline();
                    }
                });

                RxJavaPlugins.reset();
                RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>()
                {
                    @Override
                    public Scheduler apply(@io.reactivex.annotations.NonNull Scheduler scheduler) throws Exception
                    {
                        return Schedulers.trampoline();
                    }
                });
                RxJavaPlugins.setComputationSchedulerHandler(new Function<Scheduler, Scheduler>()
                {
                    @Override
                    public Scheduler apply(@io.reactivex.annotations.NonNull Scheduler scheduler) throws Exception
                    {
                        return Schedulers.trampoline();
                    }
                });

                base.evaluate();

                RxAndroidPlugins.reset();
                RxJavaPlugins.reset();
            }
        };
    }
}
