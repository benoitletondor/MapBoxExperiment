package com.benoitletondor.mapboxexperiment.common;

import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A list that can contain only a limit number of elements.
 * You must use {@link #addWithLimit(Object)} to add elements, using other methods will throw
 * {@link IllegalStateException}.
 *
 * Adding an object when the list is full will lead to remove of the first element.
 *
 * @param <E> type of elements
 */
public class LimitedSizeList<E> extends LinkedList<E>
{
    private final int mLimit;

// -------------------------------->

    public LimitedSizeList(int limit)
    {
        mLimit = limit;
    }

// -------------------------------->

    @Deprecated
    @Override
    public void addLast(E e)
    {
        throw new IllegalStateException();
    }

    @Deprecated
    @Override
    public void addFirst(E e)
    {
        throw new IllegalStateException();
    }

    @Deprecated
    @Override
    public boolean add(E e)
    {
        throw new IllegalStateException();
    }

    @Deprecated
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        throw new IllegalStateException();
    }

    @Deprecated
    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        throw new IllegalStateException();
    }

    @Deprecated
    @Override
    public void add(int index, E element)
    {
        throw new IllegalStateException();
    }

// -------------------------------->

    /**
     * Add an element to the queue of the list
     *
     * @param o the object to add
     * @return the removed object if the list is full, null otherwise
     */
    @Nullable
    public E addWithLimit(E o)
    {
        super.add(o);

        if( size() > mLimit )
        {
            return super.remove();
        }

        return null;
    }
}
