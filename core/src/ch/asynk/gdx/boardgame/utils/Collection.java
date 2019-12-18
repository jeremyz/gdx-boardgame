package ch.asynk.gdx.boardgame.utils;

import java.util.Iterator;

public interface Collection<E> extends Iterator<E>, Iterable<E>
{
    public int size();

    public boolean isEmpty();

    public void clear();

    public void ensureCapacity(int c);

    public boolean contains(E e);

    public E get(int idx);

    public int indexOf(E e);

    public boolean add(E e);

    public boolean insert(E e, int idx);

    public boolean replace(E e, int idx);

    public E remove(int idx);

    public boolean remove(E e);

    public E current();
}
