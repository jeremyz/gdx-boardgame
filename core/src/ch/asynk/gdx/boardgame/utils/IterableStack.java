package ch.asynk.gdx.boardgame.utils;

public class IterableStack<E> extends IterableArray<E>
{
    public IterableStack(int n)
    {
        super(n);
    }

    public void push(E e)
    {
        add(e);
    }

    public E pop()
    {
        if (size() <= 0) return null;
        return remove(size() - 1);
    }

    public E getTop()
    {
        if (size() <= 0) return null;
        return get(size() - 1);
    }
}
