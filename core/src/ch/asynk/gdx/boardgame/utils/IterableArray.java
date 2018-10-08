package ch.asynk.gdx.boardgame.utils;

import java.util.Arrays;
import java.util.Iterator;

public class IterableArray<E> implements Collection<E>
{
    private int idx;
    private int s;
    private int c;
    transient E[] data;

    @SuppressWarnings("unchecked")
    public IterableArray(int capacity)
    {
        this.s = 0;
        this.c = capacity;
        this.data = (E[]) new Object[c];
    }

    @Override public int size()
    {
        return s;
    }

    @Override public boolean isEmpty()
    {
        return (s == 0);
    }

    @Override public void clear()
    {
        for (int i = 0; i < s; i++)
            data[i] = null;
        s = 0;
    }

    @Override public void ensureCapacity(int min)
    {
        if (c > min) return;
        c += (c >> 1);
        if (c < min)
            c = min;
        data = Arrays.copyOf(data, c);
    }

    @Override public boolean contains(E e)
    {
        if (e == null) {
            for (int i = 0; i < s; i++) {
                if (data[i] == null)
                    return true;
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (e.equals(data[i]))
                    return true;
            }
        }
        return false;
    }

    @Override public E get(int i)
    {
        return data[i];
    }

    @Override public int indexOf(E e)
    {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null && data[i].equals(e))
                return i;
        }
        return -1;
    }

    @Override public boolean add(E e)
    {
        ensureCapacity(s + 1);
        data[s] = e;
        s += 1;
        return true;
    }

    @Override public boolean insert(E e, int idx)
    {
        ensureCapacity(s + 1);
        System.arraycopy(data, idx, data, idx+1, (s - idx));
        data[idx] = e;
        s += 1;
        return true;
    }

    @Override public E remove(int i)
    {
        E e = data[i];
        int m = (s - i - 1);
        if (m > 0)
            System.arraycopy(data, i+1, data, i, m);
        data[--s] = null;

        return e;
    }

    @Override public boolean remove(E e)
    {
        for (int i = 0; i < s; i++) {
            if (e.equals(data[i])) {
                int m = (s - i - 1);
                if (m > 0)
                    System.arraycopy(data, i+1, data, i, m);
                data[--s] = null;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override public Iterator<E> iterator()
    {
        this.idx = 0;
        return (Iterator<E>) this;
    }

    @Override public boolean hasNext()
    {
        return (idx < s);
    }

    @Override public E next()
    {
        E e = get(idx);
        idx += 1;
        return e;
    }

    @Override public void remove()
    {
        idx -= 1;
        remove(idx);
    }
}
